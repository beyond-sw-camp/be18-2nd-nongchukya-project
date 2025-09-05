package com.beyond.match.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
// Post 테이블의 createAt, updateAT에서 @CreateDate와 @LastModifiedDate 어노테이션을 설정하기 위한 설정
public class JpaConfig {
}
