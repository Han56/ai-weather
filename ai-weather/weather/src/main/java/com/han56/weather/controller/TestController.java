package com.han56.weather.controller;

import com.han56.weather.annotation.ResultFormat;
import com.han56.weather.models.response.AQIMojiResponse;
import com.han56.weather.service.SseEmitterTestService;
import com.han56.weather.service.WeatherForecastService;
import com.han56.weather.utils.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private WeatherForecastService weatherForecastService;

    @Autowired
    private SseEmitterTestService sseEmitterTestService;

    @GetMapping("/test")
    @ResultFormat
    public ServiceResult<String> test(){
        return new ServiceResult<>("返回测试数据");
    }

    @GetMapping("/sse/stream")
    public Flux<ServerSentEvent<String>> streamSse() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent.<String>builder()
                        .id(String.valueOf(sequence))
                        .event("periodic-event")
                        .data("Current time: " + LocalTime.now())
                        .build());
    }

    @GetMapping("/sse/stocks")
    public Flux<ServerSentEvent<String>> streamStockPrices() {

        return Flux.interval(Duration.ofSeconds(1))

                .map(sequence -> ServerSentEvent.<String>builder()

                        .id(String.valueOf(sequence))

                        .event("stock-update")

                        .data("Stock price: $" + ThreadLocalRandom.current().nextInt(100, 200))

                        .build());

    }

    @GetMapping("/sse/stream-with-ping")
    public Flux<ServerSentEvent<String>> streamWithPing() {

        return Flux.interval(Duration.ofSeconds(1))

                .map(sequence -> {

                    if (sequence % 5 == 0) {  // 每5秒发送一次心跳

                        return ServerSentEvent.<String>builder()

                                .comment("ping")

                                .build();

                    } else {

                        return ServerSentEvent.<String>builder()

                                .data("Current time: " + LocalTime.now())

                                .build();

                    }

                });

    }

    @GetMapping(value = "/sse_test/",produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter sse_test(@RequestParam String clientId){
        final SseEmitter sseEmitter = sseEmitterTestService.getConnect(clientId);
        CompletableFuture.runAsync(()->{
            try {
                sseEmitterTestService.send(clientId);
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        });
        return sseEmitter;
    }

    @GetMapping("/closeConn/")
    public String closeConn(String clientId){
        sseEmitterTestService.closeConn(clientId);
        return "连接已关闭";
    }


    @PostMapping(value = "/streamChatAnswer",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChatAnswer(){
        return sseEmitterTestService.recieveSSE();
    }

}
