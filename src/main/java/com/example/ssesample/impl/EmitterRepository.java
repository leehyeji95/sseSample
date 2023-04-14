package com.example.ssesample.impl;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    // Emitter 저장
    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    //Event 저장
    void saveEventCache(String eventCacheId, Object event);
    //해당 회원(userName)과 관련된 모든 Emitter를 찾는다
    Map<String, SseEmitter> findAllEmittersByUserName(String userName);
    //해당 회원(userName)과 관련된 모든 Event를 찾는다
    Map<String, Object> findAllEventsByUserName(String userName);
    void deleteById(String emitterId);
    void deleteAllEmittersByUserName(String userName);
    void deleteAllEventsByUserName(String userName);
}
