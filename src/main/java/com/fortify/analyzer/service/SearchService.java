package com.fortify.analyzer.service;

import com.fortify.analyzer.dto.SearchResultDto;
import com.fortify.analyzer.entity.ExternalMapping;
import com.fortify.analyzer.entity.Rule;
import com.fortify.analyzer.repository.ExternalMappingRepository;
import com.fortify.analyzer.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private ExternalMappingRepository externalMappingRepository;
    
    @Autowired
    private RuleRepository ruleRepository;

    @Transactional(readOnly = true)
    public SearchResultDto searchRulesAndAnalyze(String keyword) {
        List<ExternalMapping> allMappings = externalMappingRepository.findByRule_RuleNameContainingIgnoreCase(keyword);

        Map<String, Rule> distinctRulesMap = new LinkedHashMap<>();
        for (ExternalMapping mapping : allMappings) {
            distinctRulesMap.putIfAbsent(mapping.getRule().getRuleName(), mapping.getRule());
        }
        List<Rule> distinctRules = new ArrayList<>(distinctRulesMap.values());

        Map<String, Long> externalStandardCounts = allMappings.stream()
                .collect(Collectors.groupingBy(ExternalMapping::getStandardInfo, Collectors.counting()));

        return new SearchResultDto(distinctRules, keyword, externalStandardCounts);
    }

    @Transactional(readOnly = true)
    public Optional<Rule> getRuleDetails(Long id) {
        return ruleRepository.findById(id);
    }
}