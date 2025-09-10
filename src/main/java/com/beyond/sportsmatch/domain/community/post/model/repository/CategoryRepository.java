package com.beyond.sportsmatch.domain.community.post.model.repository;

import com.beyond.sportsmatch.domain.community.post.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
