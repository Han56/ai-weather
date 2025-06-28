package com.han56.weather.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.han56.weather.models.entity.ForecastHourly;
import com.han56.weather.models.entity.PotraitSettingInfo;
import com.han56.weather.models.response.*;
import com.han56.weather.annotation.PerformanceMonitor;
import com.han56.weather.service.CityMappingService;
import com.han56.weather.service.PotraitSettingInfoService;
import com.han56.weather.service.WeatherForecastService;
import com.han56.weather.utils.RedisUtil;
import com.han56.weather.utils.ServiceResult;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class WeatherForecastServiceImpl implements WeatherForecastService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherForecastServiceImpl.class);

    //墨迹天气API
    private static final String MOJI_GATE_WAY_URL = "http://aliv18.mojicb.com";

    //APP-Code
    private static final String APP_CODE = "92918db90a2b4a00b63e0485cf803f7e";

    private WebClient webClient;

    @Autowired
    private CityMappingService cityMappingService;

    @Autowired
    private PotraitSettingInfoService potraitSettingInfoService;

    @Autowired
    private RedisUtil redisUtil;

    // 预热接口配置
    private static final List<WarmupEndpoint> WARMUP_ENDPOINTS = Arrays.asList(
            new WarmupEndpoint("/whapi/json/alicityweather/condition", "50b53ff8dd7d9fa320d3d3ca32cf8ed1"),
            new WarmupEndpoint("/whapi/json/alicityweather/aqiforecast5days", "0418c1f4e5e66405d33556418189d2d0"),
            new WarmupEndpoint("/whapi/json/alicityweather/forecast15days", "f9f212e1996e79e0e602b08ea297ffb0"),
            new WarmupEndpoint("/whapi/json/alicityweather/forecast24hours", "008d2ad9197090c5dddc76f583616606"),
            new WarmupEndpoint("/whapi/json/alicityweather/alert", "7ebe966ee2e04bbd8cdbc0b84f7f3bc7"),
            new WarmupEndpoint("/whapi/json/alicityweather/index", "5944a84ec4a071359cc4f6928b797f91"),
            new WarmupEndpoint("/whapi/json/alicityweather/aqi", "8b36edf8e3444047812be3a59d27bab9"),
            new WarmupEndpoint("/whapi/json/alicityweather/limit", "27200005b3475f8b0e26428f9bfb13e9")
    );

    @PostConstruct
    public void init() {
        // 创建连接池配置
        ConnectionProvider connectionProvider = ConnectionProvider.builder("weather-connection-pool")
                .maxConnections(500)
                .maxIdleTime(Duration.ofSeconds(60))
                .maxLifeTime(Duration.ofMinutes(5))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        // 创建HttpClient配置
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)  // 增加连接超时时间
                .responseTimeout(Duration.ofMillis(15000))  // 增加响应超时时间
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(15000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(15000, TimeUnit.MILLISECONDS)));

        // 创建WebClient实例
        webClient = WebClient.builder()
                .baseUrl(MOJI_GATE_WAY_URL)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        // 预热连接池
        warmupConnectionPool();
    }

    /**
     * 预热接口配置类
     */
    private static class WarmupEndpoint {
        private final String path;
        private final String token;

        public WarmupEndpoint(String path, String token) {
            this.path = path;
            this.token = token;
        }
    }

    /**
     * 预热连接池
     */
    private void warmupConnectionPool() {
        try {
            logger.info("开始预热连接池...");
            int successCount = 0;
            int maxRetries = 3;
            int maxWarmupAttempts = WARMUP_ENDPOINTS.size() * 2; // 每个接口预热2次
            int currentAttempt = 0;

            for (WarmupEndpoint endpoint : WARMUP_ENDPOINTS) {
                for (int i = 0; i < 2; i++) { // 每个接口预热2次
                    try {
                        currentAttempt++;
                        webClient.post()
                                .uri(endpoint.path + "?cityId=2&token=" + endpoint.token)
                                .header("Authorization", "APPCODE " + APP_CODE)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(String.class)
                                .retryWhen(Retry.backoff(maxRetries, Duration.ofSeconds(2))
                                        .filter(throwable -> throwable instanceof ReadTimeoutException))
                                .doOnError(e -> logger.warn("预热请求失败，尝试重试: {} - {}", endpoint.path, e.getMessage()))
                                .block(Duration.ofSeconds(20));  // 设置block超时时间

                        successCount++;
                        logger.info("预热请求成功: {}/{} - {}", successCount, maxWarmupAttempts, endpoint.path);
                        
                        // 如果成功率达到60%，就认为预热成功
                        if (successCount >= maxWarmupAttempts * 0.6) {
                            logger.info("连接池预热完成，成功率: {}/{}", successCount, maxWarmupAttempts);
                            return;
                        }
                        
                        // 请求间隔
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        logger.warn("预热请求异常: {} - {}", endpoint.path, e.getMessage());
                    }
                }
            }
            
            if (successCount < maxWarmupAttempts * 0.6) {
                logger.warn("连接池预热未达到预期成功率: {}/{}", successCount, maxWarmupAttempts);
            }
        } catch (Exception e) {
            logger.error("连接池预热过程发生异常", e);
        }
    }

    @Override
    public ServiceResult<AQIMojiResponse> aqiForeCast5Days(String cityId) {
        //根据前端传入的行政区划代码获取 fid 即cityid
        String fid = cityMappingService.getFidsByDivisionCode(Integer.getInteger(cityId)).get(0).toString();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/aqiforecast5days?cityId={0}&token={1}"
                        ,fid,"0418c1f4e5e66405d33556418189d2d0")
                .header("Authorization","APPCODE "+APP_CODE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e->{
                    logger.error("[Moji],call aqi 5 days forecast error!",e);
                })
                .onErrorResume(e-> Mono.empty());

        AQIMojiResponse aqiMojiResponse = JSON.parseObject(resultString.block(), AQIMojiResponse.class);

        return ServiceResult.success(aqiMojiResponse);
    }

    @Override
    public ServiceResult<RealTimeMojiWeatherResponse> realTimeWeatherCondition(String cityId) {
        String fid = cityMappingService.getFidsByDivisionCode(Integer.valueOf(cityId)).get(0).toString();
        try {
            Mono<String> resultString = webClient.post()
                    .uri("/whapi/json/alicityweather/condition?cityId={0}&token={1}"
                            ,fid,"50b53ff8dd7d9fa320d3d3ca32cf8ed1")
                    .header("Authorization","APPCODE "+APP_CODE)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .filter(throwable -> throwable instanceof ReadTimeoutException))
                    .doOnError(e->{
                        logger.error("[Moji] 实时天气请求失败: {}", e.getMessage());
                    })
                    .onErrorResume(e-> Mono.empty());

            String response = resultString.block(Duration.ofSeconds(15));

            RealTimeMojiWeatherResponse weatherResponse = JSON.parseObject(response, 
                    RealTimeMojiWeatherResponse.class);
            return ServiceResult.success(weatherResponse);
        } catch (Exception e) {
            logger.error("[Moji] 实时天气请求异常", e);
        }
        return null;
    }

    @Override
    public ServiceResult<Forecast15DaysMojiResponse> forecast15DaysWeather(String cityId) {
        String fid = cityMappingService.getFidsByDivisionCode(Integer.valueOf(cityId)).get(0).toString();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/forecast15days?cityId={0}&token={1}"
                        ,fid,"f9f212e1996e79e0e602b08ea297ffb0")
                .header("Authorization","APPCODE "+APP_CODE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e->{
                    logger.error("[Moji],call Weather forecast 15days service  error!",e);
                })
                .onErrorResume(e-> Mono.empty());

        Forecast15DaysMojiResponse response = JSON.parseObject(resultString.block(),
                Forecast15DaysMojiResponse.class);

        return ServiceResult.success(response);
    }

    @Override
    public ServiceResult<ForecastHourlyMojiResponse> forecastHourlyWeather(String cityId) {
        String fid = cityMappingService.getFidsByDivisionCode(Integer.valueOf(cityId)).get(0).toString();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/forecast24hours?cityId={0}&token={1}"
                        ,fid,"008d2ad9197090c5dddc76f583616606")
                .header("Authorization","APPCODE "+APP_CODE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e->{
                    logger.error("[Moji],call Weather forecast 24h service  error!",e);
                })
                .onErrorResume(e-> Mono.empty());

        ForecastHourlyMojiResponse response = JSON.parseObject(resultString.block(),
                ForecastHourlyMojiResponse.class);

        return ServiceResult.success(response);
    }

    @Override
    public ServiceResult<WeatherAlertMojiResponse> weatherAlert(String cityId) {
        String fid = cityMappingService.getFidsByDivisionCode(Integer.valueOf(cityId)).get(0).toString();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/alert?cityId={0}&token={1}"
                        ,fid,"7ebe966ee2e04bbd8cdbc0b84f7f3bc7")
                .header("Authorization","APPCODE "+APP_CODE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e->{
                    logger.error("[Moji],call Weather alert service  error!",e);
                })
                .onErrorResume(e-> Mono.empty());

        WeatherAlertMojiResponse response = JSON.parseObject(resultString.block(),
                WeatherAlertMojiResponse.class);

        return ServiceResult.success(response);
    }

    @Override
    public ServiceResult<LiveMojiResponse> liveIndex(String cityId) {
        String fid = cityMappingService.getFidsByDivisionCode(Integer.valueOf(cityId)).get(0).toString();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/index?cityId={0}&token={1}"
                        ,fid,"5944a84ec4a071359cc4f6928b797f91")
                .header("Authorization","APPCODE "+APP_CODE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e->{
                    logger.error("[Moji],call Weather live index service  error!",e);
                })
                .onErrorResume(e-> Mono.empty());

        LiveMojiResponse response = JSON.parseObject(resultString.block(),
                LiveMojiResponse.class);

        return ServiceResult.success(response);
    }

    @Override
    public ServiceResult<AQIRealTimeMojiResponse> aqiRealTime(String cityId) {
        String fid = cityMappingService.getFidsByDivisionCode(Integer.valueOf(cityId)).get(0).toString();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/aqi?cityId={0}&token={1}"
                        ,fid,"8b36edf8e3444047812be3a59d27bab9")
                .header("Authorization","APPCODE "+APP_CODE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e->{
                    logger.error("[Moji],call Weather aqi real time service  error!",e);
                })
                .onErrorResume(e-> Mono.empty());

        AQIRealTimeMojiResponse response = JSON.parseObject(resultString.block(),
                AQIRealTimeMojiResponse.class);

        return ServiceResult.success(response);
    }

    @Override
    public ServiceResult<LimitInfoMojiResponse> limitInfo(String cityId) {
        String fid = cityMappingService.getFidsByDivisionCode(Integer.valueOf(cityId)).get(0).toString();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/limit?cityId={0}&token={1}"
                        ,fid,"27200005b3475f8b0e26428f9bfb13e9")
                .header("Authorization","APPCODE "+APP_CODE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e->{
                    logger.error("[Moji],call limit information service  error!",e);
                })
                .onErrorResume(e-> Mono.empty());

        LimitInfoMojiResponse response = JSON.parseObject(resultString.block(),
                LimitInfoMojiResponse.class);

        return ServiceResult.success(response);
    }

    @Override
    @PerformanceMonitor(description = "AI穿衣推荐服务", slowThreshold = 3000, logParams = true)
    public ServiceResult<AiClothingRecommendationsResponse> aiClothingRecommendations(String cityId, String openId) {
        long startTime = System.currentTimeMillis();
        try {
            // 1. 快速缓存检查
            String cacheKey = String.format("ai_recommend:%s:%s", cityId, openId);
            Object cachedResult = redisUtil.get(cacheKey);
            if (cachedResult != null) {
                logger.info("缓存命中，耗时={}ms", System.currentTimeMillis() - startTime);
                return ServiceResult.success((AiClothingRecommendationsResponse) cachedResult);
            }

            // 2. 并行获取数据
            CompletableFuture<String> fidFuture = CompletableFuture.supplyAsync(() -> getFidByCityId(cityId));
            CompletableFuture<PotraitSettingInfo> portraitFuture = CompletableFuture.supplyAsync(() -> {
                String cacheKey2 = "user:" + openId;
                Object cached = redisUtil.get(cacheKey2);
                if (cached != null) return (PotraitSettingInfo) cached;
                
                PotraitSettingInfo potrait = potraitSettingInfoService.selectByOpenId(openId);
                if (potrait != null) redisUtil.set(cacheKey2, potrait, 1800);
                return potrait;
            });

            // 等待并行任务完成
            String fid = fidFuture.get(5, TimeUnit.SECONDS);
            PotraitSettingInfo potrait = portraitFuture.get(5, TimeUnit.SECONDS);
            
            if (fid == null || potrait == null) {
                logger.warn("数据获取失败, fid={}, potrait={}", fid != null, potrait != null);
                return null;
            }

            // 3. 获取天气数据（带缓存）
            List<ForecastHourly> weatherList = getWeatherData(fid);
            if (weatherList == null || weatherList.isEmpty()) {
                logger.warn("天气数据获取失败");
                return null;
            }

            // 4. 快速AI推荐（简化提示词）
            AiClothingRecommendationsResponse rec = getQuickRecommendation(potrait, weatherList);
            if (rec == null) {
                logger.warn("AI推荐生成失败");
                return null;
            }

            // 5. 先缓存文案结果（不包含图片）
            redisUtil.set(cacheKey, rec, 900);

            // 6. 异步生成图片（不阻塞主流程）
            generateImageAsync(rec, potrait, cacheKey);

            logger.info("AI推荐完成，总耗时={}ms", System.currentTimeMillis() - startTime);
            return ServiceResult.success(rec);
        } catch (Exception e) {
            logger.error("aiClothingRecommendations error", e);
            return null;
        }
    }

    /**
     * 获取天气数据
     */
    private List<ForecastHourly> getWeatherData(String fid) {
        String cacheKey = "weather:" + fid;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return (List<ForecastHourly>) cached;
        }
        
        try {
            Mono<String> resultString = webClient.post()
                    .uri("/whapi/json/alicityweather/forecast24hours?cityId={0}&token={1}", fid, "008d2ad9197090c5dddc76f583616606")
                    .header("Authorization", "APPCODE " + APP_CODE)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .doOnError(e -> logger.error("天气API调用失败", e))
                    .onErrorResume(e -> Mono.empty());

            String response = resultString.block(Duration.ofSeconds(15));
            if (response != null) {
                ForecastHourlyMojiResponse forecastResponse = JSON.parseObject(response, ForecastHourlyMojiResponse.class);
                List<ForecastHourly> weatherList = forecastResponse.getForecastHourlyMojiData().getForecastHourlyList();
                if (weatherList != null && !weatherList.isEmpty()) {
                    // 只取前5小时数据，减少处理量
                    List<ForecastHourly> result = weatherList.size() >= 5 ? weatherList.subList(0, 5) : weatherList;
                    redisUtil.set(cacheKey, result, 900);
                    return result;
                }
            }
        } catch (Exception e) {
            logger.error("getWeatherData error", e);
        }
        return null;
    }

    /**
     * 快速AI推荐（简化版）
     */
    private AiClothingRecommendationsResponse getQuickRecommendation(PotraitSettingInfo potrait, List<ForecastHourly> weatherList) {
        try {
            // 简化的提示词
            String simplePrompt = buildSimplePrompt(potrait, weatherList);
            
            String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
            String apiKey = "sk-83512bc1351648b2bd30015e02d8aa7c";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoOutput(true);
            
            // 更短的超时时间
            connection.setConnectTimeout(5000);  // 5秒连接超时
            connection.setReadTimeout(15000);    // 15秒读取超时

            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "qwen2.5-32b-instruct");
            payload.put("max_tokens", 500);  // 限制输出长度
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个穿衣搭配专家。根据天气和用户特征给出简洁的穿衣建议，以JSON格式输出。请严格按照以下格式输出：{\"clothing_info\": {\"top\": \"上衣\", \"bottom\": \"下装\", \"shoes\": \"鞋子\", \"accessories\": [\"配饰1\", \"配饰2\"], \"background\": \"背景\"}, \"detailed_recommendation\": {\"title\": \"推荐标题\", \"content\": \"推荐内容\"}}");
            messages.add(systemMessage);
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", simplePrompt);
            messages.add(userMessage);
            
            payload.put("messages", messages);
            Map<String, String> responseFormat = new HashMap<>();
            responseFormat.put("type", "json_object");
            payload.put("response_format", responseFormat);
            
            String jsonInputString = JSON.toJSONString(payload);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    String responseBody = response.toString();
                    JSONObject llmResponse = JSON.parseObject(responseBody);
                    String content = llmResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                    
                    // 尝试解析AI返回的JSON
                    try {
                        return JSON.parseObject(content, AiClothingRecommendationsResponse.class);
                    } catch (Exception e) {
                        logger.warn("AI返回的JSON格式不标准，尝试修复: {}", content);
                        return fixAiResponse(content);
                    }
                }
            } else {
                logger.error("AI API调用失败，状态码: {}", responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            logger.error("getQuickRecommendation error", e);
        }
        return null;
    }

    /**
     * 修复AI返回的JSON格式问题
     */
    private AiClothingRecommendationsResponse fixAiResponse(String aiContent) {
        try {
            JSONObject jsonObject = JSON.parseObject(aiContent);
            
            AiClothingRecommendationsResponse response = new AiClothingRecommendationsResponse();
            
            // 处理clothing_info
            if (jsonObject.containsKey("clothing_info")) {
                JSONObject clothingInfoJson = jsonObject.getJSONObject("clothing_info");
                AiClothingRecommendationsResponse.ClothingInfo clothingInfo = new AiClothingRecommendationsResponse.ClothingInfo();
                
                clothingInfo.setTop(clothingInfoJson.getString("top"));
                clothingInfo.setBottom(clothingInfoJson.getString("bottom"));
                clothingInfo.setShoes(clothingInfoJson.getString("shoes"));
                clothingInfo.setBackground(clothingInfoJson.getString("background"));
                
                // 处理accessories
                Object accessoriesObj = clothingInfoJson.get("accessories");
                if (accessoriesObj instanceof List) {
                    clothingInfo.setAccessories((List<String>) accessoriesObj);
                } else if (accessoriesObj instanceof String) {
                    String accessoriesStr = (String) accessoriesObj;
                    if ("无".equals(accessoriesStr) || "none".equalsIgnoreCase(accessoriesStr)) {
                        clothingInfo.setAccessories(new ArrayList<>());
                    } else {
                        clothingInfo.setAccessories(Arrays.asList(accessoriesStr.split(",")));
                    }
                } else {
                    clothingInfo.setAccessories(new ArrayList<>());
                }
                
                response.setClothingInfo(clothingInfo);
            }
            
            // 处理detailed_recommendation
            if (jsonObject.containsKey("detailed_recommendation")) {
                Object detailedObj = jsonObject.get("detailed_recommendation");
                AiClothingRecommendationsResponse.DetailedRecommendation detailedRecommendation = new AiClothingRecommendationsResponse.DetailedRecommendation();
                
                if (detailedObj instanceof JSONObject) {
                    JSONObject detailedJson = (JSONObject) detailedObj;
                    detailedRecommendation.setTitle(detailedJson.getString("title"));
                    detailedRecommendation.setContent(detailedJson.getString("content"));
                } else if (detailedObj instanceof String) {
                    // 如果detailed_recommendation是字符串，将其作为content
                    detailedRecommendation.setTitle("今日穿搭推荐");
                    detailedRecommendation.setContent((String) detailedObj);
                }
                
                response.setDetailedRecommendation(detailedRecommendation);
            }
            
            return response;
        } catch (Exception e) {
            logger.error("修复AI响应失败", e);
            return createDefaultResponse();
        }
    }

    /**
     * 创建默认响应
     */
    private AiClothingRecommendationsResponse createDefaultResponse() {
        AiClothingRecommendationsResponse response = new AiClothingRecommendationsResponse();
        
        // 创建默认的clothing_info
        AiClothingRecommendationsResponse.ClothingInfo clothingInfo = new AiClothingRecommendationsResponse.ClothingInfo();
        clothingInfo.setTop("白色T恤");
        clothingInfo.setBottom("牛仔裤");
        clothingInfo.setShoes("运动鞋");
        clothingInfo.setAccessories(new ArrayList<>());
        clothingInfo.setBackground("城市街道");
        response.setClothingInfo(clothingInfo);
        
        // 创建默认的detailed_recommendation
        AiClothingRecommendationsResponse.DetailedRecommendation detailedRecommendation = new AiClothingRecommendationsResponse.DetailedRecommendation();
        detailedRecommendation.setTitle("今日穿搭推荐");
        detailedRecommendation.setContent("根据当前天气情况，推荐穿着舒适简约的搭配。上身选择白色T恤，下身搭配牛仔裤，脚蹬运动鞋，既舒适又时尚。");
        response.setDetailedRecommendation(detailedRecommendation);
        
        return response;
    }

    /**
     * 构建简化提示词
     */
    private String buildSimplePrompt(PotraitSettingInfo potrait, List<ForecastHourly> weatherList) {
        // 只取第一个天气数据
        ForecastHourly weather = weatherList.get(0);
        
        String userInfo = String.format("用户：%s，%s，%s，%s", 
            potrait.getGender(), potrait.getAgeGroup(), potrait.getClothingStyle(), potrait.getCountryRegion());
        
        String weatherInfo = String.format("天气：%s，温度%s°C，%s", 
            weather.getCondition(), weather.getTemp(), weather.getWindDir());
        
        return String.format("根据以下信息给出穿衣建议：%s。%s。请以JSON格式输出，包含clothing_info和detailed_recommendation字段。", 
            userInfo, weatherInfo);
    }

    /**
     * 异步生成图片
     */
    private void generateImageAsync(AiClothingRecommendationsResponse rec, PotraitSettingInfo potrait, String cacheKey) {
        CompletableFuture.runAsync(() -> {
            try {
                long imageStartTime = System.currentTimeMillis();
                String imgUrl = generateClothingImage(rec, potrait);
                if (imgUrl != null) {
                    rec.setImgUrl(imgUrl);
                    // 更新缓存（包含图片）
                    redisUtil.set(cacheKey, rec, 900);
                    logger.info("异步图片生成成功，耗时={}ms, url={}", 
                        System.currentTimeMillis() - imageStartTime, imgUrl);
                } else {
                    logger.warn("异步图片生成失败");
                }
            } catch (Exception e) {
                logger.error("异步图片生成异常", e);
            }
        });
    }

    /**
     * 生成穿衣图片
     */
    private String generateClothingImage(AiClothingRecommendationsResponse rec, PotraitSettingInfo potrait) {
        try {
            AiClothingRecommendationsResponse.ClothingInfo clothingInfo = rec.getClothingInfo();
            if (clothingInfo == null) {
                logger.warn("clothingInfo为空，无法生成图片");
                return null;
            }
            
            // 安全获取accessories
            List<String> accessoriesList = clothingInfo.getAccessories();
            String accessories = (accessoriesList != null && !accessoriesList.isEmpty()) ? 
                String.join(", ", accessoriesList) : "无配饰";
            
            // 安全获取各个字段，提供默认值
            String top = clothingInfo.getTop() != null ? clothingInfo.getTop() : "白色T恤";
            String bottom = clothingInfo.getBottom() != null ? clothingInfo.getBottom() : "牛仔裤";
            String shoes = clothingInfo.getShoes() != null ? clothingInfo.getShoes() : "运动鞋";
            String background = clothingInfo.getBackground() != null ? clothingInfo.getBackground() : "城市街道";
            
            // 简化的图片提示词
            String imagePrompt = String.format(
                    "A full-body photo of a person. Character: %s, %s, %s, %s. " +
                            "Wearing: %s, %s, %s. Accessories: %s. Background: %s. " +
                            "Style: %s, realistic, high quality.",
                    potrait.getGender(),
                    potrait.getAgeGroup(),
                    potrait.getEthnicity(),
                    potrait.getClothingStyle(),
                    top,
                    bottom,
                    shoes,
                    accessories,
                    background,
                    potrait.getClothingStyle()
            );
            
            logger.info("[GiteeImageGen] 开始生成图片，prompt长度: {}", imagePrompt.length());
            
            String giteeApiUrl = "https://ai.gitee.com/v1/images/generations";
            String giteeApiKey = "DESZ72NMD0JGIB4ZHSUKNLDY4GJIRSSVULODHC1X";
            HttpURLConnection giteeConnection = (HttpURLConnection) new URL(giteeApiUrl).openConnection();
            giteeConnection.setRequestMethod("POST");
            giteeConnection.setRequestProperty("Authorization", "Bearer " + giteeApiKey);
            giteeConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            giteeConnection.setDoOutput(true);
            
            // 设置图片生成超时
            giteeConnection.setConnectTimeout(10000); // 10秒连接超时
            giteeConnection.setReadTimeout(30000);    // 30秒读取超时
            
            Map<String, Object> giteePayload = new HashMap<>();
            giteePayload.put("model", "flux-1-schnell");
            giteePayload.put("prompt", imagePrompt);
            giteePayload.put("size", "1024x1024");
            giteePayload.put("n", 1);
            giteePayload.put("response_format", "url");
            
            String giteeJsonInputString = JSON.toJSONString(giteePayload);
            try (OutputStream os = giteeConnection.getOutputStream()) {
                byte[] inputBytes = giteeJsonInputString.getBytes("utf-8");
                os.write(inputBytes, 0, inputBytes.length);
            }
            
            int giteeResponseCode = giteeConnection.getResponseCode();
            if (giteeResponseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader giteeIn = new BufferedReader(new InputStreamReader(giteeConnection.getInputStream(), "utf-8"))) {
                    StringBuilder giteeResponse = new StringBuilder();
                    String line;
                    while ((line = giteeIn.readLine()) != null) {
                        giteeResponse.append(line.trim());
                    }
                    String giteeResponseBody = giteeResponse.toString();
                    JSONObject imageResponse = JSON.parseObject(giteeResponseBody);
                    return imageResponse.getJSONArray("data").getJSONObject(0).getString("url");
                }
            } else {
                try (BufferedReader errorStream = new BufferedReader(new InputStreamReader(giteeConnection.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorStream.readLine()) != null) {
                        errorResponse.append(line.trim());
                    }
                    logger.error("[GiteeImageGen] API请求失败，状态码: {}, 错误: {}", giteeResponseCode, errorResponse.toString());
                }
            }
            giteeConnection.disconnect();
        } catch (Exception e) {
            logger.error("generateClothingImage error", e);
        }
        return null;
    }

    private String getFidByCityId(String cityId) {
        try {
            return cityMappingService.getFidsByDivisionCode(Integer.valueOf(cityId)).get(0).toString();
        } catch (Exception e) {
            logger.error("getFidByCityId error", e);
            return null;
        }
    }
}
