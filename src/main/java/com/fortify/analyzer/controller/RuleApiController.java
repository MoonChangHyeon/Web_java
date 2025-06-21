package com.fortify.analyzer.controller;

import com.fortify.analyzer.dto.SearchResultDto;
import com.fortify.analyzer.entity.Rule;
import com.fortify.analyzer.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Rules API", description = "룰 검색 및 조회 API")
@RestController
@RequestMapping("/api/rules")
public class RuleApiController {

    @Autowired
    private SearchService searchService;

    @Operation(summary = "키워드로 룰 목록 및 통계 검색", description = "키워드가 포함된 모든 룰의 목록과 통계 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search")
    public ResponseEntity<SearchResultDto> searchRules(
            @Parameter(description = "검색할 룰 이름 키워드", required = true) @RequestParam String keyword) {
        
        SearchResultDto results = searchService.searchRulesAndAnalyze(keyword);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "특정 룰 상세 정보 조회", description = "룰 ID를 사용하여 특정 룰의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 룰을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Rule> getRuleById(
            @Parameter(description = "조회할 룰의 ID") @PathVariable Long id) {
        Optional<Rule> rule = searchService.getRuleDetails(id);
        return rule.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
}