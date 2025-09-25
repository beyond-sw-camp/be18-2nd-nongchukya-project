package com.beyond.sportsmatch.domain.mypage.model.service;

import com.beyond.sportsmatch.domain.mypage.model.dto.ProfileResponseDto;
import com.beyond.sportsmatch.domain.mypage.model.dto.UpdateProfileRequestDto;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.entity.UserLevel;
import com.beyond.sportsmatch.domain.user.model.repository.UserLevelRepository;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
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
    @Transactional
    public ProfileResponseDto updateProfile(String loginId, UpdateProfileRequestDto dto) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());  // 추가
        if (dto.getAge() != null) user.setAge(dto.getAge());        // 추가


        userRepository.save(user);

        List<UserLevel> interestedLevels = userLevelRepository.findInterestedByUser(user);
        return ProfileResponseDto.from(user, interestedLevels);
    }

    @Transactional
    public ProfileResponseDto updateProfileImage(String loginId, MultipartFile profileImage) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String imagePath = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads";
                File dir = new File(uploadDir);
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new RuntimeException("업로드 디렉토리 생성 실패: " + uploadDir);
                }

                String filename = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
                File dest = new File(dir, filename);
                profileImage.transferTo(dest);

                imagePath = "/uploads/" + filename;
                log.info("프로필 이미지 저장 성공: {}", dest.getAbsolutePath());
            } catch (IOException e) {
                log.error("프로필 이미지 저장 실패", e);
                throw new RuntimeException("프로필 이미지 저장 실패", e);
            }
        }

        // DB에 경로 저장 (null이면 기본 이미지로 처리)
        user.setProfileImage(imagePath);
        userRepository.save(user);

        List<UserLevel> interestedLevels = userLevelRepository.findInterestedByUser(user);
        return ProfileResponseDto.from(user, interestedLevels);
    }



    @Transactional
    public ProfileResponseDto resetProfileImage(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // DB에서 기존 경로 제거 (null로 설정)
        user.setProfileImage(null);
        userRepository.save(user);

        // 관심 운동 불러오기
        List<UserLevel> interestedLevels = userLevelRepository.findInterestedByUser(user);

        // 프로필 응답 (profileImage=null 이 내려가면,
        // 프론트 resolveProfileImage()에서 기본 이미지로 치환)
        return ProfileResponseDto.from(user, interestedLevels);
    }


}