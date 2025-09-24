package com.beyond.sportsmatch.domain.match.model.service;


import com.beyond.sportsmatch.common.exception.SportsMatchException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.chat.model.entity.ChatRoom;
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
import com.beyond.sportsmatch.domain.match.model.entity.MatchStatus;
import com.beyond.sportsmatch.domain.match.model.repository.MatchResultRepository;
import com.beyond.sportsmatch.domain.notification.model.service.NotificationService;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    private final NotificationService notificationService;

    @Override
    @Transactional
    public MatchApplication saveMatch(MatchApplicationRequestDto requestDto, User applicant) {
        LocalDateTime matchStartTime = LocalDateTime.of(requestDto.getMatchDate(), requestDto.getStartTime());
        if (LocalDateTime.now().isAfter(matchStartTime.minusHours(2))) {
            throw new SportsMatchException(ExceptionMessage.CANNOT_APPLY_MATCH);
        }
        Set<MatchStatus> activeStatuses = Set.of(MatchStatus.WAITING, MatchStatus.COMPLETED);
        if (matchApplicationRepository.existsByApplicantIdAndMatchDateAndStatusIn(applicant, requestDto.getMatchDate(), activeStatuses)) {
            throw new SportsMatchException(ExceptionMessage.DUPLICATE_MATCH_APPLICATION);
        }
        if (!requestDto.getStartTime().isBefore(requestDto.getEndTime())) {
            throw new SportsMatchException(ExceptionMessage.INVALID_MATCH_TIME);
        }
        String requestedOption = requestDto.getGenderOption();

        if (!(requestedOption.equals(applicant.getGender()) || requestedOption.equals("A"))) {
            throw new SportsMatchException(ExceptionMessage.INVALID_GENDER_OPTION);
        }

        Sport sport = sportRepository.findByName(requestDto.getSport())
                .orElseThrow(() -> new SportsMatchException(ExceptionMessage.SPORT_NOT_FOUND));

        MatchApplication matchApplication = new MatchApplication();
        matchApplication.setMatchApplication(requestDto, applicant, sport);

        MatchApplication savedMatch = matchApplicationRepository.save(matchApplication);

        addToMatchList(savedMatch);

        return savedMatch;
    }

    @Override
    public MatchApplication getMatch(int applicationId) {
        return matchApplicationRepository.findById(applicationId).orElse(null);
    }

    @Override
    @Transactional
    public MatchApplication deleteMatch(int applicationId) {
        MatchApplication matchApplication = matchApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new SportsMatchException(ExceptionMessage.MATCH_APPLICATION_NOT_FOUND));

        if (matchApplication.getStatus() != MatchStatus.WAITING) {
            throw new SportsMatchException(ExceptionMessage.CANNOT_CANCEL_MATCH_APPLICATION);
        }
        LocalDateTime matchStartTime = LocalDateTime.of(matchApplication.getMatchDate(), matchApplication.getStartTime());
        if (LocalDateTime.now().isAfter(matchStartTime.minusHours(2))) {
            throw new SportsMatchException(ExceptionMessage.CANNOT_CANCEL_MATCH_TWO_HOURS_LEFT);
        }

        String key = getMatchKey(matchApplication);
        String value = String.valueOf(matchApplication.getApplicantId().getUserId());

        matchRedisService.removeFromZSet(key, value);
        matchApplication.setStatus(MatchStatus.CANCELED);
        matchApplicationRepository.save(matchApplication);

        return matchApplication;
    }

    @Override
    public List<MatchApplicationResponseDto> getMatchApplications(int page, int numOfRows, User applicantId) {
        // 생성일 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page - 1, numOfRows, Sort.by("createdAt").descending());

        Set<MatchStatus> allStatuses = Set.of(MatchStatus.WAITING, MatchStatus.COMPLETED, MatchStatus.CANCELED, MatchStatus.EXPIRED);
        Page<MatchApplication> matchPage = matchApplicationRepository.findByApplicantIdAndStatusIn(applicantId, allStatuses, pageable);

        return matchPage.map(MatchApplicationResponseDto::new).getContent();
    }

    @Override
    public List<MatchApplication> getMatchApplications() {
        return matchApplicationRepository.findAll();
    }

    @Override
    public Set<String> getMatches() {
        return matchRedisService.getAllKeys();
    }

    @Override
    public List<MatchResponseDto> getMatchesByUser(User user, Pageable pageable) {
        Page<MatchApplication> applicationsPage = matchApplicationRepository.findByApplicantIdAndStatusIn(user, Set.of(MatchStatus.WAITING), pageable);

        return applicationsPage.stream()
                .filter(application -> {
                    String poolKey = getMatchKey(application);

                    return matchRedisService.getScore(poolKey, String.valueOf(user.getUserId())) != null;
                })
                .map(application -> {
                    String poolKey = getMatchKey(application);

                    long waitingCount = matchRedisService.getZSetSize(poolKey);

                    return MatchResponseDto.fromEntity(application, waitingCount);
                })
                .collect(Collectors.toList());
    }

    @Override
    public MatchResultResponseDto saveMatchResult(int matchId, MatchResultRequestDto dto) {
        MatchCompleted matchCompleted = matchCompletedRepository.findById(matchId)
                .orElseThrow(() -> new SportsMatchException(ExceptionMessage.MATCH_NOT_FOUND));

        if (matchResultRepository.existsByMatch(matchCompleted)) {
            throw new SportsMatchException(ExceptionMessage.MATCH_RESULT_ALREADY_EXISTS);
        }

        MatchResult matchResult = new MatchResult();
        matchResult.setMatch(matchCompleted);
        matchResult.setScore(dto.getScore());
        matchResult.setWinner(dto.getWinner());
        matchResult.setResultNote(dto.getResultNote());
        matchResult.setCreatedAt(LocalDateTime.now());

        MatchResult savedResult = matchResultRepository.save(matchResult);

        return MatchResultResponseDto.fromEntity(savedResult);
    }

    @Override
    public List<MatchResultResponseDto> getMatchResults(User user) {
        List<MatchResult> matchResults = matchResultRepository.findMatchResultsByUser(user);

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

    public int getWaitingCountForUser(User applicantId) {
        return matchApplicationRepository.countByApplicantIdAndStatus(applicantId, MatchStatus.WAITING);
    }

    @Override
    public int getCompletedCountForUser(User user) {
        return matchCompletedRepository.countByUserId(user.getUserId());
    }

    @Override
    public int getResultCountForUser(User user) {
        return matchResultRepository.countByUser(user);
    }

    @Override
    public List<MatchResponseDto> getImminentMatches() {
        Set<String> allKeys = matchRedisService.getAllKeys();
        if (allKeys.isEmpty()) {
            return Collections.emptyList();
        }

        return allKeys.stream()
                .map(this::parseMatchKeyToDto)
                .filter(Objects::nonNull)
                .filter(dto -> dto.getRequiredCount() - dto.getCurrentCount() > 0)
                .sorted(Comparator.comparingInt(dto -> dto.getRequiredCount() - (int) dto.getCurrentCount()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<MatchResponseDto> getMatchesByDate(LocalDate date) {
        Set<String> allKeys = matchRedisService.getAllKeys();
        if (allKeys.isEmpty()) {
            return Collections.emptyList();
        }

        return allKeys.stream()
                .map(this::parseMatchKeyToDto)
                .filter(Objects::nonNull)
                .filter(dto -> dto.getMatchDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<CompletedMatchResponseDto> getCompletedMatches(User user, Pageable pageable) {
        List<MatchCompleted> completedMatches =
                matchCompletedRepository.findAllByUserId(user.getUserId(), pageable);

        return completedMatches.stream()
                .map(match -> {
                    // matchId 기준으로 채팅방 조회
                    ChatRoom chatRoom = chatService.getChatRoomByMatchId(match.getMatchId());

                    // roomId도 포함된 DTO로 변환
                    return CompletedMatchResponseDto.fromEntityWithRoomId(match, chatRoom);
                })
                .collect(Collectors.toList());
    }

    // Key : match:sportId:region:date:startTime:endTime:genderOption
    public String getMatchKey(MatchApplication dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        String formattedStartTime = dto.getStartTime().format(formatter);
        String formattedEndTime = dto.getEndTime().format(formatter);

        return String.format("match:%s:%s:%s:%s-%s:%s",
                dto.getSport().getId(),
                dto.getRegion(),
                dto.getMatchDate(),
                formattedStartTime,
                formattedEndTime,
                dto.getGenderOption()
                );
    }

    // key를 다시 객체형태로 반환
    private MatchResponseDto parseMatchKeyToDto(String key) {
        try {
            String[] keyParts = key.split(":");

            int sportId = Integer.parseInt(keyParts[1]);
            Sport sport = sportRepository.findById(sportId).orElse(null);
            if (sport == null) return null;

            String region = keyParts[2];
            LocalDate matchDate = LocalDate.parse(keyParts[3]);
            String matchTime = formatMatchTime(keyParts[4]);
            String genderOption = keyParts[5];

            long currentSize = matchRedisService.getZSetSize(key);
            int requiredPersonnel = sport.getRequiredPersonnel();

            MatchResponseDto dto = new MatchResponseDto();
            dto.setSport(sport.getName());
            dto.setRegion(region);
            dto.setMatchDate(matchDate);
            dto.setMatchTime(matchTime);
            dto.setGenderOption(genderOption);
            dto.setCurrentCount(currentSize);
            dto.setRequiredCount(requiredPersonnel);

            return dto;
        } catch (Exception e) {
            return null;
        }
    }

    private String formatMatchTime(String timePart) {
        String[] times = timePart.split("-");
        String startTime = times[0];
        String endTime = times[1];
        return String.format("%s:%s - %s:%s",
                startTime.substring(0, 2), startTime.substring(2),
                endTime.substring(0, 2), endTime.substring(2));
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
            List<Integer> userIdList = memberIds.stream().map(Integer::valueOf).toList();

            List<MatchApplication> applicationsToUpdate = matchApplicationRepository.findByUserIdsAndStatus(userIdList, MatchStatus.WAITING);
            for(MatchApplication app : applicationsToUpdate) {
                // 키가 동일한, 즉 같은 경기를 신청한 경우에만 상태 변경
                if(getMatchKey(app).equals(key)){
                    app.setStatus(MatchStatus.COMPLETED);
                }
            }
            matchApplicationRepository.saveAll(applicationsToUpdate);

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

            // 매칭완료 시, 채팅방 개설
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    String roomName = "%s %s %s %s".formatted(
                            saved.getSport().getName(),
                            saved.getRegion(),
                            saved.getMatchDate(),
                            saved.getMatchTime()
                    );
                    Integer chatRoomId = chatService.createRoomForMatch(matchPk, roomName, userIdList);

                    notificationService.sendMatchConfirmed(
                            matchPk,
                            chatRoomId,
                            saved.getSport().getName(),
                            saved.getRegion(),
                            saved.getMatchDate(),
                            matchApplication.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            matchApplication.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            userIdList
                    );
                }
            });

            redisTemplate.delete(key);
        }
    }

    @Override
    @Transactional
    public void processFailedMatches() {
        List<MatchApplication> expiredApplications =
                matchApplicationRepository.findByMatchDateBeforeAndStatus(LocalDate.now(), MatchStatus.WAITING);

        List<MatchApplication> todayExpiredApplications = matchApplicationRepository.findByMatchDateAndStartTimeBeforeAndStatus(LocalDate.now(), LocalTime.now(), MatchStatus.WAITING);
        expiredApplications.addAll(todayExpiredApplications);

        for (MatchApplication application : expiredApplications) {
            // Redis에서 삭제
            String key = getMatchKey(application);
            redisTemplate.delete(key);

            // 데이터베이스에서 상태 변경
            application.setStatus(MatchStatus.EXPIRED);
            matchApplicationRepository.save(application);
        }
    }

    @Override
    public List<Sport> getSports() {
        return sportRepository.findAll();
    }

}