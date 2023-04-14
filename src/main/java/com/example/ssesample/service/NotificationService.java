package com.example.ssesample.service;

import com.example.ssesample.EmitterRepositoryImpl;
import com.example.ssesample.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOError;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final EmitterRepositoryImpl emitterRepository;

    public SseEmitter subscribe(String userName, String lastEventId) {
        //lastEventId : 항상 있는 헤더값은 아니고, 연결이 끊어졌을 때 넘어오는 값
        String emitterId = makeId(userName);
        System.out.println("EmitterID: "+emitterId);
        SseEmitter emitter;

        if(emitterRepository.findAllEmittersByUserName(userName) != null) {
            emitterRepository.deleteAllEmittersByUserName(userName);
        }
        emitter = emitterRepository.save(emitterId, new SseEmitter(Long.MAX_VALUE));
        //콜백함수
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        // SSE 연결 후 하나의 데이터도 전송되지 않아서 유효시간 만료되면 503 에러
        // 더미데이터 전송
//        String eventId = makeId(userName);
//        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userName="+userName+"]");
        sendToClient(emitter, emitterId, "EventStream created.. [userName:" + userName +"]");

        // 클라이언트가 미수신한 알람에 대한 처리를 위해 마지막으로 받은 LastEventId
        // EventCaches 에 있는지 체크 후 재전송 (비교)
        if(!lastEventId.isEmpty()) {
            Map<String, Object> prevEventCaches = emitterRepository.findAllEventsByUserName(userName);
            prevEventCaches.entrySet().stream()
                    // compareTo 같다(0), 그 외에는 양/음수 반환
                    // 0보다 작은 값이면 entry.getKey() 가 lastEventId 보다 큰값만 필터링
                    .filter(entry->lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    private String makeId(String userName) {
        return userName+"_"+System.currentTimeMillis();
    }

    // 클라이언트에 알림 전송
    private void sendToClient(SseEmitter sseEmitter, String id, Object data) {
        try {
            System.out.println("## SendToClient() 호출 ##");
            sseEmitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    // data = Notification 객체
                    .data(data));

            System.out.println("SseEmitter ("+sseEmitter+")");
        } catch (IOException e) {
            emitterRepository.deleteById(id);
            sseEmitter.completeWithError(e);
        }
    }

    public void send(String userName, String notiType, String url) {
        System.out.println("## NotificationService.send() 호출 ##");
        Notification notification = createNotification(userName, notiType, url);
        Map<String, SseEmitter> sseEmitterMap = emitterRepository.findAllEmittersByUserName(userName);
        sseEmitterMap.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, notification);
            sendToClient(emitter, key, notification);
        });
    }

    // user 여러명

    private Notification createNotification(String userName, String notiType, String url) {
        Notification notification = new Notification();
        notification.setReceiver(userName);
        notification.setNotificationType(notiType);
        switch(notiType) {
            case "LIKE":
                notification.setMsg("좋아요 알림");
                notification.setUrl("/like/"+url);
                break;
//                return Notification.builder()
//                        .receiver(userName)
//                        .notificationType(notiType)
//                        .msg("좋아요 알림")
//                        .url("/like"+url)
//                        .build();

            case "COMMENT":
                notification.setMsg("새로운 댓글 알림");
                notification.setUrl("/comment"+url);
                break;

            case "FOLLOW":
                notification.setMsg("팔로우 알림");
                notification.setUrl("/follow"+url);
                break;

            default:
                notification.setMsg("올바르지 않은 알람");
                notification.setUrl("/like"+url);
                break;

        }
        System.out.println("Notification["+notification+"]");
        return notification;
    }
}
