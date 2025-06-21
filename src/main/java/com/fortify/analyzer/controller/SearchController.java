package com.fortify.analyzer.controller;

import com.fortify.analyzer.dto.SearchResultDto; // DTO import
import com.fortify.analyzer.entity.Rule;
import com.fortify.analyzer.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/")
    public String searchPage(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "ruleId", required = false) Long ruleId,
            Model model) {

        // 키워드가 있을 경우, 검색 및 분석을 수행하고 그 결과를 모델에 추가
        if (keyword != null && !keyword.trim().isEmpty()) {
            SearchResultDto searchResult = searchService.searchRulesAndAnalyze(keyword);
            model.addAttribute("searchResult", searchResult);
        }

        // 상세 보기 요청(ruleId)이 있을 경우, 해당 규칙 정보를 모델에 추가
        if (ruleId != null) {
            Optional<Rule> ruleOptional = searchService.getRuleDetails(ruleId);
            ruleOptional.ifPresent(rule -> model.addAttribute("selectedRule", rule));
        }
        
        model.addAttribute("searchKeyword", keyword);
        return "index";
    }
}