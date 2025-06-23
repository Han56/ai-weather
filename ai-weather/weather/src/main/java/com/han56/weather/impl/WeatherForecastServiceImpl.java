package com.han56.weather.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.han56.weather.models.entity.ForecastHourly;
import com.han56.weather.models.entity.PotraitSettingInfo;
import com.han56.weather.models.response.*;
import com.han56.weather.service.CityMappingService;
import com.han56.weather.service.PotraitSettingInfoService;
import com.han56.weather.service.WeatherForecastService;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ServiceResult<AiClothingRecommendationsResponse> aiClothingRecommendations(String cityId, String openId) {
        //通过cituId获取fid
        String fid = cityMappingService.getFidsByDivisionCode(Integer.valueOf(cityId)).get(0).toString();

        //通过fid请求未来24小时天气情况
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

        ForecastHourlyMojiResponse forecastHourlyMojiResponse = JSON.parseObject(resultString.block(),
                ForecastHourlyMojiResponse.class);

        //拿到未来十小时天气情况
        List<ForecastHourly> forecastHourlyList = forecastHourlyMojiResponse.getForecastHourlyMojiData().getForecastHourlyList();
        int size = forecastHourlyList.size();
        List<ForecastHourly> firstTenHourList = size >= 10 ? forecastHourlyList.subList(0, 10) : forecastHourlyList;

        String firstTenHourListString = JSON.toJSONString(firstTenHourList);

        String futureTenHourDetailWeather = "未来10个小时的天气情况：" + firstTenHourListString;

        //拼接提示词
        String textSysPrompt = "# 角色\n" +
                "你是一个天气专家与穿衣搭配推荐专家。\n" +
                "## 任务\n" +
                "你要根据给到的当天未来10小时天气情况、人物画像以及所在城市给出最合理的穿衣搭配方案，并以JSON格式输出。\n" +
                "## 限制\n" +
                "- 只输出符合指定JSON结构的最终结果，不输出任何额外信息、注释或思考过程。\n" +
                "- 推荐的衣物要贴合日常生活和工作通勤场景。\n" +
                "- 禁止出现色情类型的衣物推荐。\n" +
                "- `detailed_recommendation.content` 的内容要尽量简短，不超过100个词，并且必须和`clothing_info`中提到的衣物保持一致。" +
                "#输出格式\n" +
                "{\n" +
                "  \"clothing_info\": {\n" +
                "    \"top\": \"白色T恤\",\n" +
                "    \"bottom\": \"牛仔裤\",\n" +
                "    \"shoes\": \"运动鞋\",\n" +
                "    \"accessories\": [\"太阳镜\", \"棒球帽\"],\n" +
                "    \"background\": \"城市街道\"\n" +
                "  },\n" +
                "  \"detailed_recommendation\": {\n" +
                "    \"title\": \"今日穿搭推荐\",\n" +
                "    \"content\": \"根据今天晴朗的天气，推荐穿着舒适简约风格。上身搭配白色T恤，下身穿着经典的蓝色牛仔裤，脚蹬一双白色运动鞋，既舒适又时尚。搭配太阳镜和棒球帽，为整体造型增添活力感，背景选择城市街道，展现都市休闲风格。\"\n" +
                "  }\n" +
                "}";

        //根据openid获取用户画像
        PotraitSettingInfo potraitSettingInfo = potraitSettingInfoService.selectByOpenId(openId);

        //组装 user 提示词
        String userPotraitSettingString = "性别：" + potraitSettingInfo.getGender() + "，年龄段：" + potraitSettingInfo.getAgeGroup() +
                "，国籍：" + potraitSettingInfo.getCountryRegion() + "，人种：" + potraitSettingInfo.getEthnicity() +
                "，体重范围：" + potraitSettingInfo.getWeightRange() + "，身高范围：" + potraitSettingInfo.getHeightRange() +
                "，穿衣风格：" + potraitSettingInfo.getClothingStyle() + "，配饰：" + potraitSettingInfo.getAccessoriesPreference() +
                "，发型：" + potraitSettingInfo.getHairstylePreference();

        String textUserPrompt = "根据长沙未来 10 小时的天气情况以及人物画像给出穿衣搭配方案。" +
                "人物画像：" + userPotraitSettingString +
                "未来10小时天气情况：" + futureTenHourDetailWeather;

        //请求文生文大模型
        try {
            String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

            String apiKey = "sk-83512bc1351648b2bd30015e02d8aa7c";

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoOutput(true);

            // 构造请求体
            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("model", "qwen2.5-32b-instruct");

            java.util.List<Map<String, String>> messages = new java.util.ArrayList<>();
            Map<String, String> systemMessage = new java.util.HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", textSysPrompt);
            messages.add(systemMessage);

            Map<String, String> userMessage = new java.util.HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", textUserPrompt);
            messages.add(userMessage);

            payload.put("messages", messages);
            
            Map<String, String> responseFormat = new java.util.HashMap<>();
            responseFormat.put("type", "json_object");
            payload.put("response_format", responseFormat);

            String jsonInputString = JSON.toJSONString(payload);


            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应码
            int responseCode = connection.getResponseCode();
            logger.info("LLM API Response Code: " + responseCode);

            // 处理响应
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    String responseBody = response.toString();
                    logger.info("[DashScope] LLM API Response Body: {}", responseBody);

                    JSONObject llmResponse = JSON.parseObject(responseBody);
                    // 兼容OpenAI格式的响应，直接从根对象获取 "choices"
                    String content = llmResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

                    AiClothingRecommendationsResponse recommendationsResponse = JSON.parseObject(content, AiClothingRecommendationsResponse.class);

                    //继续请求文生图模型
                    try {
                        // 1. 根据文生文结果构建图片生成prompt
                        AiClothingRecommendationsResponse.ClothingInfo clothingInfo = recommendationsResponse.getClothingInfo();
                        String accessories = String.join(", ", clothingInfo.getAccessories());
                        String imagePrompt = String.format(
                                "Help me generate a full-body photo of a character. It must be a complete full-body picture. " +
                                        "The characteristics of this character are as follows:" +
                                        "(Gender:%s,Age:%s,Race:%s,dressing style:%s," +
                                        "Ethnicity:%s,Height:%s,Weight:%s,Hairstyle:%s). " +
                                        "Wearing a  %s, %s,and %s. " +
                                        "Other accessories include:%s.Picture background:%s.",
                                potraitSettingInfo.getGender(),
                                potraitSettingInfo.getAgeGroup(),
                                potraitSettingInfo.getEthnicity(),
                                potraitSettingInfo.getClothingStyle(),
                                potraitSettingInfo.getCountryRegion(),
                                potraitSettingInfo.getHeightRange(),
                                potraitSettingInfo.getWeightRange(),
                                potraitSettingInfo.getHairstylePreference(),
                                clothingInfo.getTop(),
                                clothingInfo.getBottom(),
                                clothingInfo.getShoes(),
                                accessories,
                                clothingInfo.getBackground()
                        );
                        logger.info("[GiteeImageGen] Prompt: {}", imagePrompt);

                        // 2. 调用 Gitee 文生图 API
                        String giteeApiUrl = "https://ai.gitee.com/v1/images/generations";
                        String giteeApiKey = "DESZ72NMD0JGIB4ZHSUKNLDY4GJIRSSVULODHC1X";

                        HttpURLConnection giteeConnection = (HttpURLConnection) new URL(giteeApiUrl).openConnection();
                        giteeConnection.setRequestMethod("POST");
                        giteeConnection.setRequestProperty("Authorization", "Bearer " + giteeApiKey);
                        giteeConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                        giteeConnection.setDoOutput(true);

                        // 3. 构建请求体
                        Map<String, Object> giteePayload = new HashMap<>();
                        giteePayload.put("model", "flux-1-schnell");
                        giteePayload.put("prompt", imagePrompt);
                        giteePayload.put("size", "1024x1024");
                        giteePayload.put("user", null);
                        giteePayload.put("n", 1);
                        giteePayload.put("response_format", "url");

                        String giteeJsonInputString = JSON.toJSONString(giteePayload);

                        try (OutputStream os = giteeConnection.getOutputStream()) {
                            byte[] inputBytes = giteeJsonInputString.getBytes("utf-8");
                            os.write(inputBytes, 0, inputBytes.length);
                        }

                        // 4. 处理响应
                        int giteeResponseCode = giteeConnection.getResponseCode();
                        logger.info("[GiteeImageGen] API Response Code: {}", giteeResponseCode);
                        if (giteeResponseCode == HttpURLConnection.HTTP_OK) {
                            try (BufferedReader giteeIn = new BufferedReader(new InputStreamReader(giteeConnection.getInputStream(), "utf-8"))) {
                                StringBuilder giteeResponse = new StringBuilder();
                                String line;
                                while ((line = giteeIn.readLine()) != null) {
                                    giteeResponse.append(line.trim());
                                }
                                String giteeResponseBody = giteeResponse.toString();
                                logger.info("[GiteeImageGen] API Response Body: {}", giteeResponseBody);

                                JSONObject imageResponse = JSON.parseObject(giteeResponseBody);
                                String imageUrl = imageResponse.getJSONArray("data").getJSONObject(0).getString("url");
                                recommendationsResponse.setImgUrl(imageUrl);
                                logger.info("[GiteeImageGen] Image URL received: {}", imageUrl);
                            }
                        } else {
                            try (BufferedReader errorStream = new BufferedReader(new InputStreamReader(giteeConnection.getErrorStream(), "utf-8"))) {
                                StringBuilder errorResponse = new StringBuilder();
                                String line;
                                while ((line = errorStream.readLine()) != null) {
                                    errorResponse.append(line.trim());
                                }
                                logger.error("[GiteeImageGen] API request failed with code {}: {}", giteeResponseCode, errorResponse.toString());
                            }
                        }

                    } catch (IOException e) {
                        logger.error("[GiteeImageGen] Image generation process failed.", e);
                    }

                    return ServiceResult.success(recommendationsResponse);
                }
            } else {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    logger.error("[DashScope] AI recommendations failed with code {}: {}", responseCode, response.toString());
                }
            }

            // 关闭连接
            connection.disconnect();

        } catch (IOException e) {
            logger.error("[DashScope] AI recommendations request exception", e);
        }
        return null;
    }
}
