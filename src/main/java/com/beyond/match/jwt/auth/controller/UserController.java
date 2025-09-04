package com.beyond.match.jwt.auth.controller;

import com.beyond.match.jwt.auth.dto.ListResDto;
import com.beyond.match.jwt.auth.dto.MeResonseDto;
import com.beyond.match.jwt.auth.model.UserDetailsImpl;
import com.beyond.match.user.model.repository.UserRepository;
import com.beyond.match.user.model.service.UserService;
import com.beyond.match.user.model.vo.User;
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
    public ResponseEntity<MeResonseDto> me(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userDetails.getUser();
        return userRepository.findById(user.getUserId()).map(u -> ResponseEntity.ok(MeResonseDto.from(u)))
                .orElseGet(()-> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(Authentication authentication) {
        List<ListResDto> dto = userService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}