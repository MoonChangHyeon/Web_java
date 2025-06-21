package com.fortify.analyzer.dto;

import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
public class XmlComparisonResultDto {
    private final List<Map<String, String>> commonRules;
    private final List<Map<String, String>> onlyInA;
    private final List<Map<String, String>> onlyInB;

    public XmlComparisonResultDto(List<Map<String, String>> commonRules, List<Map<String, String>> onlyInA, List<Map<String, String>> onlyInB) {
        this.commonRules = commonRules;
        this.onlyInA = onlyInA;
        this.onlyInB = onlyInB;
    }
}