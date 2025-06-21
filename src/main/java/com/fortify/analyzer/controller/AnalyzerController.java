// src/main/java/com/fortify/analyzer/controller/AnalyzerController.java
package com.fortify.analyzer.controller;

import com.fortify.analyzer.dto.AnalysisResult;
import com.fortify.analyzer.entity.RulePack;
import com.fortify.analyzer.repository.RulePackRepository;
import com.fortify.analyzer.service.AnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.MediaType; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.RequestMapping; 

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AnalyzerController {

    private final RulePackRepository rulePackRepository;

    private final AnalyzerService analyzerService;

    public AnalyzerController(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }
    
    // GET /analyzer 요청을 처리합니다. (페이지 최초 로딩)
    @GetMapping("/analyzer")
    public String showAnalyzerPage(Model model) {
        // 모든 룰팩 목록을 버전 내림차순으로 정렬하여 조회합니다.
        List<RulePack> allPacks = rulePackRepository.findAll(Sort.by(Sort.Direction.DESC, "packVersion"));
        // Model에 담아 View(HTML)로 전달합니다.
        model.addAttribute("rule_packs", allPacks);
        return "analyzer"; // templates/analyzer.html 파일을 렌더링
    }

    // POST /analyzer 요청을 처리합니다. (사용자가 '비교 분석 시작' 버튼 클릭 시)
    @PostMapping("/analyzer")
    public String handleAnalysisRequest(@RequestParam("packs_to_compare") List<Long> packIds,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        
        // 유효성 검사: 2개가 선택되었는지 확인
        if (packIds.size() != 2) {
            redirectAttributes.addFlashAttribute("error", "오류: 비교하려면 룰팩을 정확히 2개 선택해야 합니다.");
            return "redirect:/analyzer"; // GET 요청으로 리다이렉트
        }

        // 서비스에 분석을 위임하고, 결과를 받습니다.
        AnalysisResult results = analyzerService.analyzeRulePacks(packIds);
        
        // 분석 후에도 전체 룰팩 목록은 화면에 계속 보여줘야 하므로 다시 조회해서 모델에 추가합니다.
        List<RulePack> allPacks = rulePackRepository.findAll(Sort.by(Sort.Direction.DESC, "packVersion"));
        model.addAttribute("rule_packs", allPacks);
        
        // 분석 결과(results)도 모델에 추가합니다.
        model.addAttribute("results", results);

        return "analyzer"; // templates/analyzer.html 파일을 다시 렌더링
    }

    @GetMapping("/api/analysis/summary")
    public ResponseEntity<String> getAnalysisSummary() {
        try {
            String jsonContent = analyzerService.getAnalysisResultJson("summary_by_language.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(jsonContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Could not read summary file.\"}");
        }
    }

    @GetMapping("/api/analysis/detail")
    public ResponseEntity<String> getAnalysisDetail() {
        try {
            String jsonContent = analyzerService.getAnalysisResultJson("detail_by_language.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(jsonContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Could not read detail file.\"}");
        }
    }
}