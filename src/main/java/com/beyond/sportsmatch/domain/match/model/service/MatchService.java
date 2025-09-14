package com.beyond.sportsmatch.domain.match.model.service;



import com.beyond.sportsmatch.domain.match.model.dto.CompletedMatchResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationRequestDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResponseDto;
import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface MatchService {
    void saveMatch(MatchApplicationRequestDto requestDto, User user);

    MatchApplication getMatch(int applicationId);

    void deleteMatch(int applicationId);

    List<MatchApplicationResponseDto> getMatchApplications(int page, int numOfRows, User applicantId);

//    List<MatchResponseDto> getMatches(int page, int numOfRows);

    Set<String> getImminentMatches();

    List<CompletedMatchResponseDto> getCompletedMatches(User user);

    List<MatchApplication> getMatchesByDate(LocalDate date);

    int getTotalCount();

    int getTotalCountForUser(User applicantId);

    Page<MatchResponseDto> getMatchesByUser(User user, Pageable pageable);
}
