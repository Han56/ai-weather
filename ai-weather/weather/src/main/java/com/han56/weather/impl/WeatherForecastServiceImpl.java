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
import java.util.HashMap;
import java.util.Map;
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
        return null;
    }

    @Override
    public ServiceResult<ForecastHourlyMojiResponse> forecastHourlyWeather(String cityId) {
        return null;
    }

    @Override
    public ServiceResult<WeatherAlertMojiResponse> weatherAlert(String cityId) {
        return null;
    }

    @Override
    public ServiceResult<LiveMojiResponse> liveIndex(String cityId) {
        return null;
    }

    @Override
    public ServiceResult<AQIRealTimeMojiResponse> aqiRealTime(String cityId) {
        return null;
    }

    @Override
    public ServiceResult<LimitInfoMojiResponse> limitInfo(String cityId) {
        return null;
    }
}
