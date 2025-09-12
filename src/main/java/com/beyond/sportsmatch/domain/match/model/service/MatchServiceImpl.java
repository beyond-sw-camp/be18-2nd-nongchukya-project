package com.beyond.sportsmatch.domain.match.model.service;


import com.beyond.sportsmatch.domain.chat.model.service.ChatService;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchRequestDto;
import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.user.model.entity.Sport;
import com.beyond.sportsmatch.domain.match.model.repository.MatchCompletedRepo;
import com.beyond.sportsmatch.domain.match.model.repository.MatchRepo;
import com.beyond.sportsmatch.domain.match.model.repository.SportRepo;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {
    private final MatchRepo matchRepo;
    private final MatchCompletedRepo matchCompletedRepo;
    private final MatchRedisService matchRedisService;
    private final SportRepo sportRepo;
    private final RedisTemplate<String, String> redisTemplate;
    private final ChatService chatService;

    @Override
    @Transactional
    public MatchApplicationResponseDto saveMatch(MatchRequestDto requestDto, User applicant) {
        Sport sport = sportRepo.findByName(requestDto.getSport())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sport name: " + requestDto.getSport()));

        MatchApplication matchApplication = new MatchApplication();
        matchApplication.setMatchApplication(requestDto, applicant, sport);

        MatchApplication savedMatch = matchRepo.save(matchApplication);

        addToMatchList(savedMatch);

        return new MatchApplicationResponseDto(savedMatch);
    }

    @Override
    public MatchApplication getMatch(int applicationId) {
        return matchRepo.findById(applicationId).orElse(null);
    }

    @Override
    @Transactional
    public void deleteMatch(int applicationId) {
        MatchApplication matchApplication = matchRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid application ID: " + applicationId));

        String key = getMatchKey(matchApplication);
        String value = String.valueOf(matchApplication.getApplicantId().getUserId());

        matchRedisService.removeFromZSet(key, value);
        matchRepo.deleteById(applicationId);
    }

    @Override
    public List<MatchApplication> getMatches() {
        return matchRepo.findAll();
    }

    @Override
    public Set<String> getMatchingList() {
        return matchRedisService.getAllKeys();
    }

    @Override
    public Set<String> getImminentMatches() {
        Set<String> allKeys = matchRedisService.getAllKeys();
        Set<String> imminentMatches = new HashSet<>();
        for (String key : allKeys) {
            long size = matchRedisService.getZSetSize(key);
            String[] keyParts = key.split(":");
            String sportName = keyParts[1];
            Sport sport = sportRepo.findByName(sportName)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid sport name: " + sportName));

            if (size >= sport.getRequiredPersonnel() - 2 && size < sport.getRequiredPersonnel()) {
                imminentMatches.add(key);
            }
        }
        return imminentMatches;
    }

    @Override
    public List<MatchCompleted> getCompletedMatches() {
        return matchCompletedRepo.findAll();
    }

    @Override
    public List<MatchApplication> getMatchesByDate(LocalDate date) {
        return matchRepo.findByMatchDate(date);
    }

    // Key : match:sportId:region:date:startTime:endTime
    private String getMatchKey(MatchApplication dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        String formattedStartTime = dto.getStartTime().format(formatter);
        String formattedEndTime = dto.getEndTime().format(formatter);

        return String.format("match:%s:%s:%s:%s-%s",
                dto.getSport().getId(),
                dto.getRegion(),
                dto.getMatchDate(),
                formattedStartTime,
                formattedEndTime);
    }

    // key : 매칭조건, value : userId
    public void addToMatchList(MatchApplication matchApplication) {
        String key = getMatchKey(matchApplication);
        String value = String.valueOf(matchApplication.getApplicantId().getUserId());
        long ttl = 7 * 24 * 60 * 60 * 1000L;
        long expireAt = System.currentTimeMillis() + ttl;
        matchRedisService.addToZSet(key, value, expireAt);
        checkCount(matchApplication);
    }

    @Transactional
    public void checkCount(MatchApplication matchApplication) {
        String key = getMatchKey(matchApplication);
        long currentSize = matchRedisService.getZSetSize(key);
        int requiredPersonnel = matchApplication.getSport().getRequiredPersonnel();

        if (currentSize >= requiredPersonnel) {
            Set<String> memberIds = matchRedisService.getZSetMembers(key);

            MatchCompleted completed = new MatchCompleted();
            completed.setSport(matchApplication.getSport());
            completed.setRegion(matchApplication.getRegion());
            completed.setMatchDate(matchApplication.getMatchDate());
            String matchTime = matchApplication.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                    " - " +
                    matchApplication.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            completed.setMatchTime(matchTime);
            completed.setGenderOption(matchApplication.getGenderOption());
            completed.setCreatedAt(LocalDateTime.now());

            Set<User> participants = new HashSet<>();
            for (String memberId : memberIds) {
                User user = new User();
                user.setUserId(Integer.parseInt(memberId));
                participants.add(user);
            }
            completed.setParticipants(participants);

            MatchCompleted saved = matchCompletedRepo.save(completed);
            int matchPk = saved.getMatchId();

            List<Integer> userIdList = memberIds.stream().map(Integer::valueOf).toList();

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    String roomName = "%s %s".formatted(
                            saved.getSport().getName(),
                            saved.getRegion(),
                            saved.getMatchDate()
                    );
                    chatService.createRoomForMath(matchPk, roomName, userIdList);
                }
            });

            redisTemplate.delete(key);
        }
    }
}