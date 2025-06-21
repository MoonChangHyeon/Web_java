package com.fortify.analyzer.dto;

import java.util.List;

/**
 * 규칙 상세 정보를 JSON으로 제공하기 위한 DTO입니다.
 */
public record RuleDto(
        Long id,
        String ruleName,
        Long rulePackId,
        String rulePackName,
        List<String> mappings
) {}
