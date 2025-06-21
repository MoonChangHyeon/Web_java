package com.fortify.analyzer.repository;

import com.fortify.analyzer.entity.RulePack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RulePackRepository extends JpaRepository<RulePack, Long> {

    // 기존 메소드
    Optional<RulePack> findByLocation(String location);

    // ✨ 버전 정보까지 함께 조회하는 메소드 추가
    Optional<RulePack> findByLocationAndPackVersion(String location, String packVersion);
}