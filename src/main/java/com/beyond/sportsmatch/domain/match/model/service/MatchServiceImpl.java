package com.beyond.sportsmatch.domain.match.model.service;


import com.beyond.sportsmatch.domain.chat.model.service.ChatService;
import com.beyond.sportsmatch.domain.match.model.dto.CompletedMatchResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationRequestDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResultRequestDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResultResponseDto;
import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.entity.MatchResult;
import com.beyond.sportsmatch.domain.match.model.repository.MatchResultRepository;
import com.beyond.sportsmatch.domain.user.model.entity.Sport;
import com.beyond.sportsmatch.domain.match.model.repository.MatchCompletedRepository;
import com.beyond.sportsmatch.domain.match.model.repository.MatchApplicationRepository;
import com.beyond.sportsmatch.domain.user.model.repository.SportRepository;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {
    private final MatchApplicationRepository matchApplicationRepository;
    private final MatchCompletedRepository matchCompletedRepository;
    private final MatchResultRepository matchResultRepository;
    private final MatchRedisService matchRedisService;
    private final SportRepository sportRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ChatService chatService;

    @Override
    @Transactional
    public void saveMatch(MatchApplicationRequestDto requestDto, User applicant) {
        Sport sport = sportRepository.findByName(requestDto.getSport())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sport name: " + requestDto.getSport()));

        MatchApplication matchApplication = new MatchApplication();
        matchApplication.setMatchApplication(requestDto, applicant, sport);

        MatchApplication savedMatch = matchApplicationRepository.save(matchApplication);

        addToMatchList(savedMatch);
    }

    @Override
    public MatchApplication getMatch(int applicationId) {
        return matchApplicationRepository.findById(applicationId).orElse(null);
    }

    @Override
    @Transactional
    public void deleteMatch(int applicationId) {
        MatchApplication matchApplication = matchApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid application ID: " + applicationId));

        String key = getMatchKey(matchApplication);
        String value = String.valueOf(matchApplication.getApplicantId().getUserId());

        matchRedisService.removeFromZSet(key, value);
        matchApplicationRepository.deleteById(applicationId);
    }

    @Override
    public List<MatchApplicationResponseDto> getMatchApplications(int page, int numOfRows, User applicantId) {
        // 생성일 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page - 1, numOfRows, Sort.by("createdAt").descending());

        Page<MatchApplication> matchPage = matchApplicationRepository.findByApplicantId(applicantId, pageable);

        return matchPage.map(MatchApplicationResponseDto::new).getContent();
    }

    @Override
    public Page<MatchResponseDto> getMatchesByUser(User user, Pageable pageable) {
        Page<MatchApplication> applicationsPage = matchApplicationRepository.findByApplicantId(user, pageable);

        // Page 객체의 map 메소드를 사용
        return applicationsPage.map(application -> {
            // 3. 각 신청서(application) 정보로 Redis Key를 생성합니다.
            String poolKey = getMatchKey(application);

            Long currentCount = matchRedisService.getZSetSize(poolKey);

            return MatchResponseDto.fromEntity(application, currentCount);
        });
    }

    @Override
    public MatchResultResponseDto saveMatchResult(int matchId, MatchResultRequestDto dto) {
        // 1. 매칭 완료된 경기 찾기
        MatchCompleted matchCompleted = matchCompletedRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 경기를 찾을 수 없습니다. matchId=" + matchId));

        // 2. 이미 결과가 등록되어 있는지 확인
        if (matchResultRepository.existsByMatch(matchCompleted)) {
            throw new IllegalStateException("이미 결과가 등록된 경기입니다.");
        }

        // 3. 엔티티 생성
        MatchResult matchResult = new MatchResult();
        matchResult.setMatch(matchCompleted);
        matchResult.setScore(dto.getScore());
        matchResult.setWinner(dto.getWinner());
        matchResult.setResultNote(dto.getResultNote());
        matchResult.setCreatedAt(LocalDateTime.now());

        // 4. 저장
        MatchResult savedResult = matchResultRepository.save(matchResult);

        // 5. 응답 DTO 변환
        return new MatchResultResponseDto(
                savedResult.getScore(),
                savedResult.getWinner(),
                savedResult.getResultNote()
        );
    }

    @Override
    public List<MatchResultResponseDto> getMatchResults(User user) {
        List<MatchResult> matchResults = matchResultRepository.findAll();

        return matchResults.stream()
                .map(MatchResultResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public int getTotalCount() {
        return (int) matchApplicationRepository.count();
    }

    @Override
    public int getTotalCountForUser(User applicantId) {
        return matchApplicationRepository.countByApplicantId(applicantId);
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
    public List<CompletedMatchResponseDto> getCompletedMatches(User user) {
        List<MatchCompleted> completedMatches = matchCompletedRepository.findAllByUserId(user.getUserId());

        return completedMatches.stream()
                .map(CompletedMatchResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<MatchApplication> getMatchesByDate(LocalDate date) {
        return matchApplicationRepository.findByMatchDate(date);
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
                        " - " + matchApplication.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
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

            // 매칭완료 시, 채팅방 개설
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