package com.beyond.sportsmatch.domain.match.model.service;



import com.beyond.sportsmatch.domain.match.model.dto.CompletedMatchResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationRequestDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResultRequestDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResultResponseDto;
import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.user.model.entity.Sport;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface MatchService {
    MatchApplication saveMatch(MatchApplicationRequestDto requestDto, User user);

    MatchApplication getMatch(int applicationId);

    MatchApplication deleteMatch(int applicationId);

    List<MatchApplicationResponseDto> getMatchApplications(int page, int numOfRows, User applicantId);

    List<MatchApplication> getMatchApplications();

    List<MatchResponseDto> getImminentMatches();

    List<CompletedMatchResponseDto> getCompletedMatches(User user, Pageable pageable);

    List<MatchResponseDto> getMatchesByDate(LocalDate date);

    int getTotalCount();

    int getTotalCountForUser(User applicantId);

    Set<String> getMatches();

    List<MatchResponseDto> getMatchesByUser(User user, Pageable pageable);

    MatchResultResponseDto saveMatchResult(int matchId, MatchResultRequestDto dto);

    List<MatchResultResponseDto> getMatchResults(User user);

    void processFailedMatches();

    List<Sport> getSports();

    int getWaitingCountForUser(User user);

    int getCompletedCountForUser(User user);

//    int getTotalCountMatchResult(User user);
}
