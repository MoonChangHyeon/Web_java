package com.fortify.analyzer.controller;

import com.fortify.analyzer.service.AnalyzerService;
import com.fortify.analyzer.service.CrawlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/crawler") // 이 컨트롤러의 모든 요청은 /crawler 로 시작합니다.
public class CrawlController {

    private final CrawlService crawlService;
    private final AnalyzerService analyzerService;

    public CrawlController(CrawlService crawlService, AnalyzerService analyzerService) {
        this.crawlService = crawlService;
        this.analyzerService = analyzerService;
    }

    // 1. 크롤러 페이지를 보여주는 메서드
    @GetMapping("")
    public String crawlerPage() {
        return "crawler"; // templates/crawler.html 파일을 의미
    }

    // 2. 크롤러 실행을 시작하는 메서드
    @PostMapping("/execute")
    @ResponseBody
    public ResponseEntity<String> startCrawling() {
        crawlService.updatePagesAndExecuteCrawler();
        return ResponseEntity.ok("Page update and crawling process has been started in the background.");
    }

    // 3. 분석 결과 (요약) API
    @GetMapping("/api/analysis/summary")
    @ResponseBody
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

    // 4. 분석 결과 (상세) API
    @GetMapping("/api/analysis/detail")
    @ResponseBody
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