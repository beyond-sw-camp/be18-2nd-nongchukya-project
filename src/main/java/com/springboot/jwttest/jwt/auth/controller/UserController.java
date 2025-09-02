package com.springboot.jwttest.jwt.auth.controller;

import com.springboot.jwttest.jwt.auth.dto.ListResDto;
import com.springboot.jwttest.jwt.auth.dto.MeResonseDto;
import com.springboot.jwttest.user.model.repository.UserRepository;
import com.springboot.jwttest.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<MeResonseDto> me(Authentication authentication) {
        if(authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer userId = (Integer) authentication.getPrincipal();
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(MeResonseDto.from(user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/list")
    public ResponseEntity<?> list(Authentication authentication) {
        List<ListResDto> dto = userService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
