package com.beyond.sportsmatch.domain.community.post.model.repository;


import com.beyond.sportsmatch.domain.community.post.model.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment,Integer> {
}
