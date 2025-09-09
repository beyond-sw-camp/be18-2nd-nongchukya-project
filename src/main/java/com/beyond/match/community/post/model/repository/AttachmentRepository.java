package com.beyond.match.community.post.model.repository;


import com.beyond.match.community.post.model.vo.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment,Integer> {
}
