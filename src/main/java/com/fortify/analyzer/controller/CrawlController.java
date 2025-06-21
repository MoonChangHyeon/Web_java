package com.fortify.analyzer.controller;

import com.fortify.analyzer.service.CrawlService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/crawl")
public class CrawlController {

    private final CrawlService crawlService;

    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @PostMapping("/execute")
    @ResponseBody
    public ResponseEntity<String> startCrawling() {
        // 비동기 메서드 호출
        crawlService.runCrawlingAndAnalysis();
        // 사용자에게 즉시 응답 반환
        return ResponseEntity.ok("Crawling and analysis process has been started in the background.");
    }
}