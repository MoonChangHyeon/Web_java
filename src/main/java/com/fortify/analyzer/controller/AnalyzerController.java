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

@Controller
@RequestMapping("/analyzer")
public class AnalyzerController {

    private final AnalyzerService analyzerService;
    private final CrawlService crawlService;

    public AnalyzerController(AnalyzerService analyzerService, CrawlService crawlService) {
        this.analyzerService = analyzerService;
        this.crawlService = crawlService;
    }

    @GetMapping("")
    public String analyzerPage() {
        return "analyzer";
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

    @PostMapping("/crawl/execute")
    public ResponseEntity<String> startCrawling() {
        crawlService.updatePagesAndExecuteCrawler();
        return ResponseEntity.ok("Page update and crawling process has been started in the background.");
    }
}