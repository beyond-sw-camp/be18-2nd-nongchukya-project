package com.beyond.sportsmatch.domain.mypage.model.entity;


import com.beyond.sportsmatch.common.entity.BaseTimeEntity;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Report extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;  // 신고자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser; // 신고 대상자

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "evidence_url", length = 500)
    private String evidenceUrl;  // 첨부 파일 경로 (URL)

    @Column(nullable = false)
    private String status = "PENDING"; // 기본값


}