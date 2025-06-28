package com.han56.weather.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * HTTP客户端配置
 * 优化AI API调用的性能
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public WebClient optimizedWebClient() {
        // 连接池配置
        ConnectionProvider connectionProvider = ConnectionProvider.builder("optimized-connection-pool")
                .maxConnections(200)
                .maxIdleTime(Duration.ofSeconds(60))
                .maxLifeTime(Duration.ofMinutes(5))
                .pendingAcquireTimeout(Duration.ofSeconds(30))
                .build();

        // HTTP客户端配置
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 连接超时10秒
                .responseTimeout(Duration.ofSeconds(30)) // 响应超时30秒
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public WebClient aiWebClient() {
        // 专门用于AI API的客户端配置
        ConnectionProvider aiConnectionProvider = ConnectionProvider.builder("ai-connection-pool")
                .maxConnections(50)
                .maxIdleTime(Duration.ofSeconds(120))
                .maxLifeTime(Duration.ofMinutes(10))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .build();

        HttpClient aiHttpClient = HttpClient.create(aiConnectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000) // AI API连接超时15秒
                .responseTimeout(Duration.ofSeconds(60)) // AI API响应超时60秒
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(aiHttpClient))
                .build();
    }
} 