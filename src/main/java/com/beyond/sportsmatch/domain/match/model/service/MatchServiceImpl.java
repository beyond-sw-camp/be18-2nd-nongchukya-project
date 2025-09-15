package com.beyond.sportsmatch.domain.match.model.service;


import com.beyond.sportsmatch.domain.chat.model.service.ChatService;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchRequestDto;
import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.repository.MatchCompletedRepository;
import com.beyond.sportsmatch.domain.match.model.repository.MatchRepository;
import com.beyond.sportsmatch.domain.user.model.repository.SportRepository;
import com.beyond.sportsmatch.domain.user.model.entity.Sport;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
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
    private final MatchRepository matchRepository;
    private final MatchCompletedRepository matchCompletedRepository;
    private final MatchRedisService matchRedisService;
    private final SportRepository sportRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ChatService chatService;

    @Override
    @Transactional
    public MatchApplicationResponseDto saveMatch(MatchRequestDto requestDto, User applicant) {
        Sport sport = sportRepository.findByName(requestDto.getSport())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sport name: " + requestDto.getSport()));

        MatchApplication matchApplication = new MatchApplication();
        matchApplication.setMatchApplication(requestDto, applicant, sport);

        MatchApplication savedMatch = matchRepository.save(matchApplication);

        addToMatchList(savedMatch);

        return new MatchApplicationResponseDto(savedMatch);
    }

    @Override
    public MatchApplication getMatch(int applicationId) {
        return matchRepository.findById(applicationId).orElse(null);
    }

    @Override
    @Transactional
    public void deleteMatch(int applicationId) {
        MatchApplication matchApplication = matchRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid application ID: " + applicationId));

        String key = getMatchKey(matchApplication);
        String value = String.valueOf(matchApplication.getApplicantId().getUserId());

        matchRedisService.removeFromZSet(key, value);
        matchRepository.deleteById(applicationId);
    }

    @Override
    public List<MatchApplication> getMatches() {
        return matchRepository.findAll();
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
            Sport sport = sportRepository.findByName(sportName)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid sport name: " + sportName));

            if (size >= sport.getRequiredPersonnel() - 2 && size < sport.getRequiredPersonnel()) {
                imminentMatches.add(key);
            }
        }
        return imminentMatches;
    }

    @Override
    public List<MatchCompleted> getCompletedMatches() {
        return matchCompletedRepository.findAll();
    }

    @Override
    public List<MatchApplication> getMatchesByDate(LocalDate date) {
        return matchRepository.findByMatchDate(date);
    }

    // Key : match:sport:region:date:startTime:endTime
    private String getMatchKey(MatchApplication dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        String formattedStartTime = dto.getStartTime().format(formatter);
        String formattedEndTime = dto.getEndTime().format(formatter);

        return String.format("match:%s:%s:%s:%s-%s",
                dto.getSport().getName(),
                dto.getRegion(),
                dto.getMatchDate(),
                formattedStartTime,
                formattedEndTime);
    }


    public void addToMatchList(MatchApplication matchApplication) {
        String key = getMatchKey(matchApplication);
        String value = String.valueOf(matchApplication.getApplicantId().getUserId());
        double score = matchRedisService.getZSetSize(key) + 1;
        matchRedisService.addToZSet(key, value, score);
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

            MatchCompleted saved = matchCompletedRepository.save(completed);
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
                    chatService.createRoomForMatch(matchPk, roomName, userIdList);
                }
            });

            redisTemplate.delete(key);
        }
    }
}