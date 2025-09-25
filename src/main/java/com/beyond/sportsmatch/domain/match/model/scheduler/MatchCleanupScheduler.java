package com.beyond.sportsmatch.domain.match.model.scheduler;

import com.beyond.sportsmatch.domain.match.model.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchCleanupScheduler {

    private final MatchService matchService;

    /**
     * 매일 자정에 실행되어 만료된 (매칭되지 않은) 매칭 신청을 정리합니다.
     * cron = "초 분 시 일 월 요일"
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupFailedMatches() {
        log.info("매칭 실패한 신청 건에 대한 정리를 시작합니다...");
        try {
            matchService.processFailedMatches();
            log.info("매칭 실패 건 정리를 성공적으로 완료했습니다.");
        } catch (Exception e) {
            log.error("매칭 실패 건 정리 중 오류가 발생했습니다.", e);
        }
    }
}