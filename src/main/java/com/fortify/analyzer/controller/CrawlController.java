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
import java.util.Map;

@Controller
@RequestMapping("/crawler")
public class CrawlController {

    private final CrawlService crawlService;
    private final AnalyzerService analyzerService;

    public CrawlController(CrawlService crawlService, AnalyzerService analyzerService) {
        this.crawlService = crawlService;
        this.analyzerService = analyzerService;
    }

    @GetMapping("")
    public String crawlerPage() {
        return "crawler";
    }

    // --- 작업 시작 API ---
    @PostMapping("/execute-crawling")
    @ResponseBody
    public ResponseEntity<String> startCrawling() {
        crawlService.startCrawlingProcess();
        return ResponseEntity.ok("Crawling process start requested.");
    }

    @PostMapping("/execute-analysis")
    @ResponseBody
    public ResponseEntity<String> startAnalysis() {
        crawlService.startAnalysisProcess();
        return ResponseEntity.ok("Analysis process start requested.");
    }
    
    // --- 작업 중지 API ---
    @PostMapping("/stop-crawling")
    @ResponseBody
    public ResponseEntity<String> stopCrawling() {
        crawlService.stopCrawlingProcess();
        return ResponseEntity.ok("Crawling process stop requested.");
    }

    @PostMapping("/stop-analysis")
    @ResponseBody
    public ResponseEntity<String> stopAnalysis() {
        crawlService.stopAnalysisProcess();
        return ResponseEntity.ok("Analysis process stop requested.");
    }

    // --- 작업 강제 중지 API ---
    @PostMapping("/force-stop-crawling")
    @ResponseBody
    public ResponseEntity<String> forceStopCrawling() {
        crawlService.forceStopCrawlingProcess();
        return ResponseEntity.ok("Crawling process force-stop requested.");
    }

    @PostMapping("/force-stop-analysis")
    @ResponseBody
    public ResponseEntity<String> forceStopAnalysis() {
        crawlService.forceStopAnalysisProcess();
        return ResponseEntity.ok("Analysis process force-stop requested.");
    }

    // --- 상태 조회 API ---
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(crawlService.getTasksStatus());
    }

    /**
     * 요약 분석 결과 JSON을 반환하는 API입니다.
     * @return 요약 JSON 데이터를 담은 ResponseEntity
     */
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

    /**
     * 상세 분석 결과 JSON을 반환하는 API입니다.
     * @return 상세 JSON 데이터를 담은 ResponseEntity
     */
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