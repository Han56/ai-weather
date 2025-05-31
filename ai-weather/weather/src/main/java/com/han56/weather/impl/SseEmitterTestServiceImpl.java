package com.han56.weather.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.han56.weather.models.response.ChatCompletionResponse;
import com.han56.weather.service.SseEmitterTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterTestServiceImpl implements SseEmitterTestService {

    private static final Map<String,SseEmitter> SSE_CACHE = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(SseEmitterTestServiceImpl.class);

    @Override
    public SseEmitter getConnect(String clientId) {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter != null)
            return sseEmitter;
        else {
            final SseEmitter emitter = new SseEmitter(600_000L);

            // 超时回调
            emitter.onTimeout(()->{
                logger.info("连接已超时，正准备关闭，clientId = {}", clientId);
                SSE_CACHE.remove(clientId);
            });

            //注册完成回调
            emitter.onCompletion(()->{
                logger.info("连接已关闭，正准备释放，clientId = {}", clientId);
                SSE_CACHE.remove(clientId);
                logger.info("连接已释放，clientId = {}", clientId);
            });

            //注册异常回调
            emitter.onError(throwable -> {
                logger.error("连接已异常，正准备关闭，clientId = {}", clientId, throwable);
                SSE_CACHE.remove(clientId);
            });
            SSE_CACHE.put(clientId, emitter);

            return emitter;
        }
    }

    //模拟类似于 chatGPT 的流式推送回答
    @Override
    public void send(String clientId) throws IOException {
        final SseEmitter emitter = SSE_CACHE.get(clientId);
        // 推流内容到客户端
        emitter.send("此去经年", org.springframework.http.MediaType.APPLICATION_JSON);
        emitter.send("此去经年，应是良辰好景虚设");
        emitter.send("此去经年，应是良辰好景虚设，便纵有千种风情");
        emitter.send("此去经年，应是良辰好景虚设，便纵有千种风情，更与何人说");
        // 结束推流
        emitter.complete();

    }

    @Override
    public void closeConn(String clientId) {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter != null) {
            sseEmitter.complete();
        }
    }

    @Override
    public SseEmitter recieveSSE() {
        SseEmitter sseEmitter = new SseEmitter(300_000L);

        //Chat服务api
        WebClient webClient = WebClient.create("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
        Flux<String> sseStream = webClient.post()
                //填充请求头信息
                .header(HttpHeaders.AUTHORIZATION,"Bearer sk-83512bc1351648b2bd30015e02d8aa7c")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("{ \"model\": \"qwen-plus\", \"messages\": [ { \"role\": \"system\", \"content\": \"You are a helpful assistant.\" }, { \"role\": \"user\", \"content\": \"你是谁？\" } ],\"stream\":true }")
                .retrieve()
                .bodyToFlux(String.class);

        // 这里Flux.from()是异步的，该方法会先把SseEmitter对象返回出去
        Flux.from(sseStream)
                .filter(content -> !content.trim().isEmpty())
                .doOnNext(content ->{

                    if ("[DONE]".equals(content.trim())) {
                        logger.info("处理完成，服务器返回 DONE");
                        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                        data.put("sseId", "1");
                        data.put("event", "message");
                        data.put("content", "处理完成");
                        sendSSE(sseEmitter, data);
                        return;
                    }
                    try {
                        ChatCompletionResponse response = new ObjectMapper().readValue(content, ChatCompletionResponse.class);
                        String deltaContent = response.getChoices().get(0).getDelta().getContent();
                        if (deltaContent != null) {
                            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                            data.put("sseId", response.getId());
                            data.put("event", "message");
                            data.put("content", deltaContent);
                            sendSSE(sseEmitter, data);
                        }
                    } catch (IOException e) {
                        logger.error("解析 JSON 失败: {}", content, e);
                        throw new RuntimeException(e);
                    }

                })
                .doOnError(error -> {
                    logger.error("连接断开: {}", error.getMessage());
                    sseEmitter.completeWithError(error);
                })
                .doOnComplete(() -> {
                    logger.info("完成");
                    sseEmitter.complete();
                }).subscribe();
        return sseEmitter;
    }

    // 发送数据
    public void sendSSE(SseEmitter sseEmitter, LinkedHashMap<String, Object> data) {
        try {
            logger.info("推送消息: {}", data);
            sseEmitter.send(SseEmitter.event()
                    .reconnectTime(5000)
                    .id(data.get("sseId").toString())
                    .name(data.get("event").toString())
                    .data(data));

        } catch (IOException e) {
            logger.error("SSE推送消息失败, 失败消息: {}", data, e);
            sseEmitter.completeWithError(e);
        }
    }
}
