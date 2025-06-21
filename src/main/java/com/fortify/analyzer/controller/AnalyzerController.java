package com.fortify.analyzer.controller;

import com.fortify.analyzer.service.AnalyzerService;
import com.fortify.analyzer.service.CrawlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping("/analyzer")
public class AnalyzerController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyzerController.class);
    private final AnalyzerService analyzerService;
    private final CrawlService crawlService; // CrawlService 주입

    public AnalyzerController(AnalyzerService analyzerService, CrawlService crawlService) {
        this.analyzerService = analyzerService;
        this.crawlService = crawlService;
    }

    @GetMapping("")
    public String analyzerPage() {
        return "analyzer";
    }

    // 참고: 에러를 유발하는 /analyze 엔드포인트는 삭제했습니다.

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
    
    // crawl/execute 엔드포인트는 CrawlController로 분리하는 것이 더 구조적으로 좋습니다.
    // 하지만 우선은 여기에 두겠습니다.
    @PostMapping("/crawl/execute")
    public ResponseEntity<String> startCrawling() {
        crawlService.updatePagesAndExecuteCrawler();
        return ResponseEntity.ok("Page update and crawling process has been started in the background.");
    }
}