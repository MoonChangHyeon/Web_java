package com.fortify.analyzer.repository;

import com.fortify.analyzer.entity.CategoryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryInfoRepository extends JpaRepository<CategoryInfo, Long> {

    // 카테고리 이름으로 정보를 찾기 위한 메서드
    Optional<CategoryInfo> findByKingdomName(String kingdomName);
}