package com.example.ssesample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    // 알람 받는 유저 정보
    private String receiver;
    // 알람 타입
    private String notificationType;
    // 알람 내용
    private String msg;
    // 알람 클릭 시 이동할 mapping url
    private String url;
    // 알람 읽음 여부
    @Builder.Default
    private Boolean isRead = false;

    @Override
    public String toString() {
        return "Notification{" +
                "receiver='" + receiver + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", msg='" + msg + '\'' +
                ", url='" + url + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
