package com.fortify.analyzer.dto;

import com.fortify.analyzer.entity.Rule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class SearchResultDto {
    private final List<Rule> foundRules;
    private final String searchKeyword;
    private final Map<String, Long> externalStandardCounts;

    public int getUniqueRuleCount() {
        return foundRules.size();
    }

    public List<Map.Entry<String, Long>> getSortedStandardCounts() {
        if (this.externalStandardCounts == null) {
            return List.of();
        }
        return this.externalStandardCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}