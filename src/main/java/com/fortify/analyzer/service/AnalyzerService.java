// src/main/java/com/fortify/analyzer/service/AnalyzerService.java
package com.fortify.analyzer.service;

import com.fortify.analyzer.dto.AnalysisResult;
import com.fortify.analyzer.entity.Rule;
import com.fortify.analyzer.entity.RulePack;
import com.fortify.analyzer.repository.RulePackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyzerService {

    private final RulePackRepository rulePackRepository;

    @Transactional(readOnly = true) // DB에서 읽기만 하므로 readOnly=true 옵션으로 성능을 최적화합니다.
    public AnalysisResult analyzeRulePacks(List<Long> packIds) {
        // ID 목록으로 RulePack 두 개를 DB에서 조회합니다.
        List<RulePack> packs = rulePackRepository.findAllById(packIds);
        if (packs.size() < 2) {
            throw new IllegalArgumentException("비교를 위해 두 개의 룰팩을 찾을 수 없습니다.");
        }
        
        RulePack packA = packs.get(0);
        RulePack packB = packs.get(1);

        // 각 룰팩에 포함된 룰의 이름(ruleName)만 Set으로 추출합니다.
        Set<String> rulesA = packA.getRules().stream()
                                  .map(Rule::getRuleName)
                                  .collect(Collectors.toSet());

        Set<String> rulesB = packB.getRules().stream()
                                  .map(Rule::getRuleName)
                                  .collect(Collectors.toSet());

        // Set 자료구조의 특성을 이용해 비교 연산을 수행합니다.
        Set<String> commonRules = new HashSet<>(rulesA);
        commonRules.retainAll(rulesB); // 교집합 (공통 룰)

        Set<String> onlyInA = new HashSet<>(rulesA);
        onlyInA.removeAll(rulesB); // 차집합 (A에만 있는 룰)

        Set<String> onlyInB = new HashSet<>(rulesB);
        onlyInB.removeAll(rulesA); // 차집합 (B에만 있는 룰)

        // 분석 결과를 DTO 객체에 담아 반환합니다.
        return new AnalysisResult(packA, packB, commonRules, onlyInA, onlyInB);
    }
}