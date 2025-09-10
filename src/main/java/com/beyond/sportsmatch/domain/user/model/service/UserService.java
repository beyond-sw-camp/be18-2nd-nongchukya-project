package com.beyond.sportsmatch.domain.user.model.service;

import com.beyond.sportsmatch.domain.user.model.dto.ListResponseDto;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public List<ListResponseDto> findAll() {
        List<User> users = userRepository.findAll();
        List<ListResponseDto> listdto = new ArrayList<>();
        for (User user : users) {
            ListResponseDto dto = new ListResponseDto();
            dto.setUserId(user.getUserId());
            dto.setUserName(user.getName());
            dto.setEmail(user.getEmail());
            listdto.add(dto);
        }
        return listdto;
    }
}