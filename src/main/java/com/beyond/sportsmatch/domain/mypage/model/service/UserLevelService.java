package com.beyond.sportsmatch.domain.mypage.model.service;

import com.beyond.sportsmatch.domain.match.model.entity.Sport;
import com.beyond.sportsmatch.domain.mypage.model.dto.UserLevelResponseDto;
import com.beyond.sportsmatch.domain.user.model.entity.Level;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.entity.UserLevel;
import com.beyond.sportsmatch.domain.user.model.entity.UserLevelId;
import com.beyond.sportsmatch.domain.user.model.repository.SportRepository;
import com.beyond.sportsmatch.domain.user.model.repository.UserLevelRepository;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLevelService {

    private final UserLevelRepository userLevelRepository;
    private final SportRepository sportRepository;
    private final UserRepository userRepository;

    // 조회
    @Transactional(readOnly = true)
    public List<UserLevelResponseDto> getUserLevelDtosByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userLevelRepository.findByUser(user).stream()
                .map(UserLevelResponseDto::from)
                .toList();
    }

    // 수정/등록
    @Transactional
    public UserLevelResponseDto updateUserLevel(User user, String sportName, Integer levelId, Boolean interest) {
        Sport sport = sportRepository.findByName(sportName)
                .orElseThrow(() -> new EntityNotFoundException("해당 운동이 존재하지 않습니다."));

        UserLevel userLevel = userLevelRepository.findByUserAndSport(user, sport)
                .orElseGet(() -> {
                    UserLevel ul = new UserLevel();
                    ul.setId(new UserLevelId(user.getUserId(), sport.getId()));
                    ul.setUser(user);
                    ul.setSport(sport);
                    return ul;
                });

        Level level = new Level();
        level.setId(levelId);
        userLevel.setLevel(level);

        userLevel.setInterest(interest != null ? interest : false);

        // 저장 후 flush로 updatedAt 자동 반영
        UserLevel saved = userLevelRepository.saveAndFlush(userLevel);

        return UserLevelResponseDto.from(saved);
    }
}