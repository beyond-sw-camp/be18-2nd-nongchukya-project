package com.beyond.sportsmatch.domain.chat.controller;

import com.beyond.sportsmatch.domain.chat.model.repository.VoteRepository;
import com.beyond.sportsmatch.domain.chat.model.service.VoteService;
import com.beyond.sportsmatch.domain.chat.model.entity.Vote;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/vote")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;
    private final VoteRepository voteRepository;

    // 투표 생성
    @PostMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<Vote> createVote(@PathVariable int chatRoomId,
                                           @RequestBody Map<String, Object> req) {
        String title = (String) req.get("title");
        List<String> options = (List<String>) req.get("options");
        Vote vote = voteService.createVote(chatRoomId, title, options);
        return  ResponseEntity.status(HttpStatus.CREATED).body(vote);
    }

    @PostMapping("/{voteId}/vote")
    public ResponseEntity<?> castVote(@PathVariable int voteId, @RequestBody Map<String, Object> req) {
        String selectedOption = (String) req.get("selectedOption");
        voteService.castVote(voteId, selectedOption);
        return ResponseEntity.ok("투표가 완료되었습니다.");
    }

    @GetMapping("/{voteId}/results")
    public ResponseEntity<?> getResultsByVoteId(@PathVariable int voteId) {
        return ResponseEntity.ok(voteService.getResults(voteId));
    }

    @GetMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<?> list(@PathVariable int chatRoomId) {
        List<Vote> votes = voteRepository.findByChatRoom_ChatRoomId(chatRoomId);

        List<Map<String, Object>> response = votes.stream().map(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("voteId", v.getVoteId());
            map.put("title", v.getTitle());
            map.put("options", v.getOptions());
            map.put("createdAt", v.getCreatedAt());
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }
}
