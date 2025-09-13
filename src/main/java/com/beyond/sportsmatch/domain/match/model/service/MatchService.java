package com.beyond.sportsmatch.domain.match.model.service;



import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchRequestDto;
import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.user.model.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface MatchService {
    void saveMatch(MatchRequestDto requestDto, User user);

    MatchApplication getMatch(int applicationId);

    void deleteMatch(int applicationId);

    List<MatchApplicationResponseDto> getMatches(int page, int numOfRows, User applicantId);

    Set<String> getMatchingList();

    Set<String> getImminentMatches();

    List<MatchCompleted> getCompletedMatches();

    List<MatchApplication> getMatchesByDate(LocalDate date);

    int getTotalCount();

    int getTotalCountForUser(User applicantId);
}
