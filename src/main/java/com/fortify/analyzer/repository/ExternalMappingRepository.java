package com.fortify.analyzer.repository;

import com.fortify.analyzer.entity.ExternalMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalMappingRepository extends JpaRepository<ExternalMapping, Long> {
    // Rule 엔티티의 ruleName 필드를 기준으로 검색하는 메소드
    List<ExternalMapping> findByRule_RuleNameContainingIgnoreCase(String keyword);
}