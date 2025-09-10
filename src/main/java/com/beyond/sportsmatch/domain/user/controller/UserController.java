package com.beyond.sportsmatch.domain.user.controller;

import com.beyond.sportsmatch.domain.user.model.dto.ListResponseDto;
import com.beyond.sportsmatch.auth.dto.MeResonseDto;
import com.beyond.sportsmatch.auth.service.UserDetailsServiceImpl;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import com.beyond.sportsmatch.domain.user.model.service.UserService;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<MeResonseDto> me(@AuthenticationPrincipal UserDetailsServiceImpl userDetails) {
        if(userDetails==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userDetails.getUser();
        return userRepository.findById(user.getUserId()).map(u -> ResponseEntity.ok(MeResonseDto.from(u)))
                .orElseGet(()-> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(Authentication authentication) {
        List<ListResponseDto> dto = userService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}