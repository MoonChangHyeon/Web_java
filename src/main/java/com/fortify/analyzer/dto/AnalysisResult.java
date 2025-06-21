// src/main/java/com/fortify/analyzer/dto/AnalysisResult.java
package com.fortify.analyzer.dto;

import com.fortify.analyzer.entity.RulePack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor // final 필드만 포함하는 생성자를 만들어줍니다.
public class AnalysisResult {

    private final RulePack packA;
    private final RulePack packB;
    private final Set<String> commonRules;
    private final Set<String> onlyInA;
    private final Set<String> onlyInB;

    // Thymeleaf에서 전체 룰 개수를 쉽게 가져올 수 있도록 getter를 추가합니다.
    public int getPackATotalRules() {
        return packA != null ? packA.getRules().size() : 0;
    }

    public int getPackBTotalRules() {
        return packB != null ? packB.getRules().size() : 0;
    }
}