package com.beyond.sportsmatch.domain.mypage.model.service;

import com.beyond.sportsmatch.domain.mypage.model.dto.ProfileResponseDto;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.entity.UserLevel;
import com.beyond.sportsmatch.domain.user.model.repository.UserLevelRepository;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserLevelRepository userLevelRepository;

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 관심 운동만 가져오기 (Sport fetch join 포함)
        List<UserLevel> interestedLevels = userLevelRepository.findInterestedByUser(user);

        return ProfileResponseDto.from(user, interestedLevels);
    }
}