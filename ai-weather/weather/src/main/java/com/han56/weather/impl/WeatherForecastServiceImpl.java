package com.han56.weather.impl;

import com.alibaba.fastjson2.JSON;
import com.han56.weather.models.response.*;
import com.han56.weather.service.CityMappingService;
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
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
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
}
