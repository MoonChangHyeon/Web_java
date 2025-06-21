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
        // 새로 만든 메인 서비스 메서드 호출
        crawlService.updatePagesAndExecuteCrawler(); 
        return ResponseEntity.ok("Page update and crawling process has been started in the background.");
    }
}