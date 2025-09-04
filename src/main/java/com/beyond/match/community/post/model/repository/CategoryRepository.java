package com.beyond.match.community.post.model.repository;

import com.beyond.match.community.post.model.vo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
