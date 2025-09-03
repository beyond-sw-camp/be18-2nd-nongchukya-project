package com.beyond.match.user.model.service;

import com.beyond.match.jwt.auth.dto.ListResDto;
import com.beyond.match.user.model.repository.UserRepository;
import com.beyond.match.user.model.vo.User;
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
    public List<ListResDto> findAll() {
        List<User> users = userRepository.findAll();
        List<ListResDto> listdto = new ArrayList<>();
        for (User user : users) {
            ListResDto dto = new ListResDto();
            dto.setUserId(user.getUserId());
            dto.setUserName(user.getName());
            dto.setEmail(user.getEmail());
            listdto.add(dto);
        }
        return listdto;
    }
}
