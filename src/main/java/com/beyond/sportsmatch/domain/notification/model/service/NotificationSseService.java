package com.beyond.sportsmatch.domain.notification.model.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationSseService {

    private static final long TIMEOUT = 60L * 60 * 1000; // 1h
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String loginId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.computeIfAbsent(loginId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        Runnable cleanup = () -> emitters.getOrDefault(loginId, new CopyOnWriteArrayList<>()).remove(emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        // 첫 이벤트 (연결 확인)
        try { emitter.send(SseEmitter.event().name("connected").data("ok")); } catch (IOException ignore) {}
        return emitter;
    }

    public void send(String loginId, String eventName, Object payload) {
        var list = emitters.getOrDefault(loginId, new CopyOnWriteArrayList<>());
        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter em : list) {
            try {
                em.send(SseEmitter.event()
                        .name(eventName)
                        .id(UUID.randomUUID().toString())
                        .data(payload));
            } catch (Exception e) {
                dead.add(em);
            }
        }
        if (!dead.isEmpty()) list.removeAll(dead);
    }
}
