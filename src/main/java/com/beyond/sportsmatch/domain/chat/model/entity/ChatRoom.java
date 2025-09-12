package com.beyond.sportsmatch.domain.chat.model.entity;

import com.beyond.sportsmatch.common.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "chat_room", uniqueConstraints = {
        @UniqueConstraint(name = "uk_chat_room_match", columnNames = "matchId")
})
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private int chatRoomId;

    @Column(name = "chat_room_name")
    private String chatRoomName;

    @Column(name = "is_group_chat")
    @Builder.Default
    private String isGroupChat = "N";

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<JoinedChatRoom> joinedChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    private int matchId;

}
