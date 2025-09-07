package com.beyond.match.chat.model.service;

import com.beyond.match.chat.model.repository.ChatRoomRepository;
import com.beyond.match.chat.model.repository.VoteRepository;
import com.beyond.match.chat.model.repository.VoteResultRepository;
import com.beyond.match.chat.model.vo.ChatRoom;
import com.beyond.match.chat.model.vo.Vote;
import com.beyond.match.chat.model.vo.VoteResult;
import com.beyond.match.jwt.auth.model.security.UserDetailsImpl;
import com.beyond.match.user.model.repository.UserRepository;
import com.google.gson.Gson;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final VoteRepository voteRepository;
    private final VoteResultRepository voteResultRepository;

    public Vote createVote(int chatRoomId, String title, List<String> options) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() ->
                new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        Vote vote = Vote.builder()
                .chatRoom(chatRoom)
                .title(title)
                .options(new Gson().toJson(options))
                .build();
        return  voteRepository.save(vote);
    }

    public void castVote(int voteId, String selectedOption) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(()->
                new EntityNotFoundException("투표를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        boolean alreadyVoted = voteResultRepository.existsByVoteAndUser(vote, userDetails.getUser());
        if (alreadyVoted) {
            throw new IllegalStateException("이미 투표를 완료했습니다.");
        }

        VoteResult voteResult = VoteResult.builder()
                .vote(vote)
                .user(userDetails.getUser())
                .selectedOption(selectedOption)
                .build();

        voteResultRepository.save(voteResult);

    }

    public Object getResults(int voteId) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(()->
                new EntityNotFoundException("투표를 찾을 수 없습니다."));
        List<VoteResult> results = voteResultRepository.findByVote(vote);

        return results.stream().collect(Collectors.groupingBy(VoteResult::getSelectedOption, Collectors.counting()));
    }

    public List<Vote> listByChatRoom(int chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()->
                new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        return voteRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom);
    }
}
