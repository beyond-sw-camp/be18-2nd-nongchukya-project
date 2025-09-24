package com.beyond.sportsmatch.domain.match.model.scheduler;

import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.repository.MatchCompletedRepository;
import com.beyond.sportsmatch.domain.notification.model.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultRequestScheduler {

    private MatchCompletedRepository matchCompletedRepository;
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *")
    public void scheduleDailyResultRegistrationRequest() {
        System.out.println("어제 경기 결과 미등록 확인 스케줄러 실행...");

        // 어제 날짜를 계산합니다.
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Repository에서 어제 날짜의 경기만 조회합니다.
        List<MatchCompleted> matchesToNotify = matchCompletedRepository.findMatchesByDateWithoutResult(yesterday);

        if (matchesToNotify.isEmpty()) {
            System.out.println("알림을 보낼 어제 경기 없음.");
            return;
        }

        for (MatchCompleted match : matchesToNotify) {
            System.out.println("결과 등록 요청 알림 발송: matchId=" + match.getMatchId() + ", matchDate=" + match.getMatchDate());
            notificationService.sendMatchEnded(match);
        }
    }
}
