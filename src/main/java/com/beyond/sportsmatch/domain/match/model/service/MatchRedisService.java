package com.beyond.sportsmatch.domain.match.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MatchRedisService {
    private final RedisTemplate<String, String> redisTemplate;

    // ZSet에 값 추가 (ZADD)
    public void addToZSet(String key, String value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    // ZSet에서 모든 값 가져오기 (ZRANGE)
    public Set<String> getZSetMembers(String key) {
        return redisTemplate.opsForZSet().range(key, 0, -1);
    }

    // ZSet에서 값 삭제 (ZREM)
    public void removeFromZSet(String key, String value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

    // ZSet의 원소 개수 가져오기 (ZCARD)
    public long getZSetSize(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    // ZSet의 일정 범위 값 가져오기 (ZREVRANGE)
    public Set<String> getZSetMembersByRange(String key, int start, int end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    // key 조회 (SCAN)
    public Set<String> getAllKeys() {
        Set<String> keys = new HashSet<>();
        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match("match:*").count(1000).build())) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            }
            return null;
        });
        return keys;
    }

}
