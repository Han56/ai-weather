package com.han56.weather.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.ImmutableMap;
import com.han56.weather.models.response.*;
import com.han56.weather.service.WeatherForecastService;
import com.han56.weather.utils.ServiceResult;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class WeatherForecastServiceImpl implements WeatherForecastService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherForecastServiceImpl.class);

    //墨迹天气API
    private static final String MOJI_GATE_WAY_URL = "http://aliv18.mojicb.com";

    //APP-Code
    private static final String APP_CODE = "92918db90a2b4a00b63e0485cf803f7e";

    /**
     * 调用创建任务检测单接口
     * */
    private static WebClient buildBaseWebClient(){
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000,TimeUnit.MILLISECONDS)));
        return WebClient.builder()
                .baseUrl(MOJI_GATE_WAY_URL)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Override
    public ServiceResult<AQIMojiResponse> aqiForeCast5Days(String cityId) {
        WebClient webClient = buildBaseWebClient();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/aqiforecast5days?cityId={0}&token={1}"
                        ,cityId,"0418c1f4e5e66405d33556418189d2d0")
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
        WebClient webClient = buildBaseWebClient();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/condition?cityId={0}&token={1}"
                        ,cityId,"50b53ff8dd7d9fa320d3d3ca32cf8ed1")
                .header("Authorization","APPCODE "+APP_CODE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e->{
                    logger.error("[Moji],call realTime Weather condition  error!",e);
                })
                .onErrorResume(e-> Mono.empty());

        RealTimeMojiWeatherResponse response = JSON.parseObject(resultString.block(),
                RealTimeMojiWeatherResponse.class);

        return ServiceResult.success(response);
    }

    @Override
    public ServiceResult<Forecast15DaysMojiResponse> forecast15DaysWeather(String cityId) {
        WebClient webClient = buildBaseWebClient();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/forecast15days?cityId={0}&token={1}"
                        ,cityId,"f9f212e1996e79e0e602b08ea297ffb0")
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
        WebClient webClient = buildBaseWebClient();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/forecast24hours?cityId={0}&token={1}"
                        ,cityId,"008d2ad9197090c5dddc76f583616606")
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
        WebClient webClient = buildBaseWebClient();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/alert?cityId={0}&token={1}"
                        ,cityId,"7ebe966ee2e04bbd8cdbc0b84f7f3bc7")
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
        WebClient webClient = buildBaseWebClient();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/index?cityId={0}&token={1}"
                        ,cityId,"5944a84ec4a071359cc4f6928b797f91")
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
        WebClient webClient = buildBaseWebClient();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/aqi?cityId={0}&token={1}"
                        ,cityId,"8b36edf8e3444047812be3a59d27bab9")
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
        WebClient webClient = buildBaseWebClient();
        Mono<String> resultString = webClient.post()
                .uri("/whapi/json/alicityweather/limit?cityId={0}&token={1}"
                        ,cityId,"27200005b3475f8b0e26428f9bfb13e9")
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
