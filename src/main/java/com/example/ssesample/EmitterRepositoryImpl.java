package com.example.ssesample;

import com.example.ssesample.impl.EmitterRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@NoArgsConstructor
@Slf4j
public class EmitterRepositoryImpl implements EmitterRepository {
    // SseEmitter의 콜백함수는 관리 쓰레드에서 실행되기 때문에 thread-safe 한 자료구조 사용할 필요가 있다.
    // 그렇지 않으면 ConcurrentModificationException 발생
    // ConcurrentHashMap, 또는 CopyOnWriteArrayList
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCaches = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        log.info("새로운 Emitter 저장! EmitterMap" + emitters);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        eventCaches.put(eventCacheId, event);
        log.info("새로운 이벤트 생성!");
    }

    @Override
    public Map<String, SseEmitter> findAllEmittersByUserName(String userName) {
        //EmitterId는 UserName_CurrentTimeMillis() 형식으로 만들어짐
        //Emitters Map에서 userName으로 시작하는 Key 값 있으면(여러개도 가능, 뒤에 시간 값이 다름) 그걸로 다시 Map 만들어서 return
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userName))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventsByUserName(String userName) {
        return eventCaches.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userName))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
        log.info("Emitter 삭제!"+emitterId);
    }

    @Override
    public void deleteAllEmittersByUserName(String userName) {
        emitters.forEach((key, emitter) -> {
            if(key.startsWith(userName)) {
                emitters.remove(key);
            }
        });

    }

    @Override
    public void deleteAllEventsByUserName(String userName) {
        eventCaches.entrySet()
                .forEach(entry-> eventCaches.remove(entry.getKey().startsWith(userName)));

    }

}
