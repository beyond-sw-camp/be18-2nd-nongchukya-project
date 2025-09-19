package com.beyond.sportsmatch.domain.match.scheduler;

import com.beyond.sportsmatch.domain.match.model.repository.MatchApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchAutoCancelScheduler {
    private final MatchApplicationRepository matchApplicationRepository;

    @Scheduled(cron = " 0 0 0 * * ?")
    public void cancelMatch() {

    }
}
