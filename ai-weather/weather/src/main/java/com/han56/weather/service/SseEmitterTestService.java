package com.han56.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public interface SseEmitterTestService {

    SseEmitter getConnect(String clientId);

    void send(String clientId) throws IOException;

    void closeConn(String clientId);

    SseEmitter recieveSSE();

}
