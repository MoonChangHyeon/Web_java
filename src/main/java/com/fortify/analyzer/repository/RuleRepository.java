package com.fortify.analyzer.repository;

import com.fortify.analyzer.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RuleRepository extends JpaRepository<Rule, Long> {
    
    List<Rule> findByRuleNameContainingIgnoreCase(String keyword);
    
    // findById는 JpaRepository에 이미 있으므로 아래 라인은 삭제해도 무방합니다.
    // Optional<Rule> findById(Long id);
}