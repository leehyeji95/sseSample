package com.example.ssesample.controller;

import com.example.ssesample.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe/{userName}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("userName") String userName, @RequestHeader(value="Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        // Last-Event-ID : 마지막으로 받은 EventId, 유실 데이터 체크 위함
        // required=false : 항상 Header 값 존재하는 것 아님
        // defaultValue="" : 값이 담겨있지 않은 경우 isEmpty 로 구분하기 위해 빈 데이터 설정
        return notificationService.subscribe(userName, lastEventId);
    }

}
