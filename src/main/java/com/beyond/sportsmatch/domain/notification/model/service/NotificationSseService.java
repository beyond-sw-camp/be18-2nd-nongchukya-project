package com.beyond.sportsmatch.domain.notification.model.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificationSseService {

    private static final long TIMEOUT = 60L * 60 * 1000; // 1h
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> eventCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> seqMap = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String loginId, String lastEventId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        // 새로고침 시 기존 연결 정리 (메모리 누수 방지)
        CopyOnWriteArrayList<SseEmitter> userEmitters = emitters.computeIfAbsent(loginId, k -> new CopyOnWriteArrayList<>());

        // 기존 연결들을 모두 complete 시키고 제거
        for (SseEmitter old : userEmitters) {
            try {
                old.complete();
            } catch (Exception ignored) {
                // 이미 종료된 연결은 무시
            }
        }
        userEmitters.clear();

        // 새 연결만 추가
        userEmitters.add(emitter);

        Runnable cleanup = () -> removeEmitter(loginId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        if(lastEventId != null && !lastEventId.isBlank()) {
            Map<String, Object> cached = eventCache.getOrDefault(loginId, Collections.emptyMap());

            cached.entrySet().stream()
                    .filter(e -> e.getKey().compareTo(lastEventId) > 0)
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> {
                        try {
                            emitter.send(SseEmitter.event()
                                    .id(e.getKey())
                                    .name("notification")
                                    .data(e.getValue()));
                        } catch (IOException ignored) {

                        }
                    });
        }

        // 첫 이벤트 (연결 확인)
        try { emitter.send(SseEmitter.event().name("connected").data("ok")); } catch (IOException ignore) {}
        return emitter;
    }

    public void send(String loginId, String eventName, Object payload) {
        String evenId = createEventId(loginId);

        eventCache.computeIfAbsent(loginId, k -> new ConcurrentHashMap<>()).put(evenId, payload);

        CopyOnWriteArrayList<SseEmitter> list = emitters.getOrDefault(loginId, new CopyOnWriteArrayList<>());

        List<SseEmitter> dead = new ArrayList<>();

        for(SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event()
                        .id(evenId)
                        .name("notification")
                        .data(payload));
            } catch (Exception e) {
                dead.add(emitter);
            }
        }

        if (!dead.isEmpty()) {
            list.removeAll(dead);
        }

        Map<String, Object> cache = eventCache.get(loginId);
        if (cache != null && cache.size() > 500) {
            cache.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .limit(cache.size() - 400)
                    .forEach(e -> cache.remove(e.getKey()));
        }
    }
    private String createEventId(String userId) {
        long now = System.currentTimeMillis();
        long seq = seqMap.computeIfAbsent(userId, k -> new AtomicLong()).incrementAndGet();
        // seq를 6자리 0패딩해서 문자열 비교해도 숫자 순서랑 같게
        return String.format("%d-%06d", now, seq); // 예: 1733738670000-000001
    }

    private void removeEmitter(String loginId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = emitters.get(loginId);
        if(list != null) {
            list.remove(emitter);
        }
    }
    @Scheduled(fixedRate = 25_000) // 25초마다
    public void heartbeat() {
        emitters.forEach((userId, list) -> {
            for (SseEmitter emitter : list) {
                try {
                    // comment는 클라이언트 이벤트로 안 잡히고, 연결 유지용으로만 사용 가능
                    emitter.send(SseEmitter.event().comment("hb"));
                } catch (IOException e) {
                    removeEmitter(userId, emitter);
                }
            }
        });
    }
}
