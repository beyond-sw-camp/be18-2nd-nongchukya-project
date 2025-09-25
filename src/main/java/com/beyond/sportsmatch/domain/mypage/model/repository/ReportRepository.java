package com.beyond.sportsmatch.domain.mypage.model.repository;//package com.beyond.sportsmatch.domain.mypage.model.repository;
//
//
//
//import com.beyond.sportsmatch.domain.mypage.model.entity.Report;
//import com.beyond.sportsmatch.domain.user.model.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface ReportRepository extends JpaRepository<Report, Integer> {
//    // 필요하면 추가적인 쿼리 메서드 작성 가능
//    List<Report> findByReporter(User reporter);
//
//    List<Report> findByTargetUser(User targetUser);
//}
import com.beyond.sportsmatch.domain.mypage.model.entity.Report;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query("SELECT r FROM Report r JOIN FETCH r.reporter JOIN FETCH r.targetUser WHERE r.reporter = :reporter")
    List<Report> findByReporter(@Param("reporter") User reporter);

    @Query("SELECT r FROM Report r JOIN FETCH r.reporter JOIN FETCH r.targetUser WHERE r.targetUser = :targetUser")
    List<Report> findByTargetUser(@Param("targetUser") User targetUser);
}