package com.beyond.sportsmatch.auth.controller;


import com.beyond.sportsmatch.auth.model.dto.request.ChangePasswordRequestDto;
import com.beyond.sportsmatch.auth.model.dto.request.DeleteUserRequestDto;
import com.beyond.sportsmatch.auth.model.dto.request.FindIdRequestDto;
import com.beyond.sportsmatch.auth.model.dto.request.PasswordVerifyRequestDto;
import com.beyond.sportsmatch.auth.model.dto.response.FindIdResponseDto;
import com.beyond.sportsmatch.auth.model.dto.response.PasswordVerifyResponseDto;
import com.beyond.sportsmatch.auth.model.dto.response.TokenResponseDto;
import com.beyond.sportsmatch.auth.model.service.UserService;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@Valid @RequestBody FindIdRequestDto dto) {
        FindIdResponseDto response = userService.findIdByEmail(dto.getEmail());
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "아이디 일부를 반환했습니다.",
                "data", response
        ));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(
            @Valid @RequestBody PasswordVerifyRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        PasswordVerifyResponseDto response =
                userService.verifyPassword(userDetails.getUsername(), dto.getPassword());

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "비밀번호가 일치합니다.",
                "data", response
        ));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletResponse response) {

        User user = userService.findByLoginId(userDetails.getUsername());

        TokenResponseDto tokenResponse = userService.changePassword(user, dto.getPassword(), response);

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "비밀번호가 성공적으로 변경되었습니다.",
                "data", Map.of(
                        "id", user.getLoginId(),
                        "accessToken", tokenResponse.getAccessToken(),
                        "refreshToken", tokenResponse.getRefreshToken()
                )
        ));
    }

    @PutMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @Valid @RequestBody DeleteUserRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 로그인한 사용자와 요청 id가 일치하는지 체크
        if (!userDetails.getUsername().equals(
                userService.findByLoginId(userDetails.getUsername()).getLoginId())) {
            throw new IllegalArgumentException("본인 계정만 탈퇴할 수 있습니다.");
        }

        User user = userService.findByLoginId(userDetails.getUsername());
        userService.deactivateUser(user, dto.getPassword());

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "회원 탈퇴가 완료되었습니다.",
                "data", Map.of(
                        "id", user.getLoginId(),
                        "status", user.getStatus()
                )
        ));
    }

}