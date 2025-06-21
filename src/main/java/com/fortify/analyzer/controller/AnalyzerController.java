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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.fortify.analyzer.dto.XmlComparisonResultDto;
import java.util.Map;

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

    @PostMapping("/compare-xml")
    public String compareXmlFiles(@RequestParam("fileA") MultipartFile fileA,
                                  @RequestParam("fileB") MultipartFile fileB,
                                  Model model) {
        try {
            XmlComparisonResultDto result = analyzerService.compareExternalMetadata(fileA, fileB);
            model.addAttribute("result", result);
            model.addAttribute("fileAName", fileA.getOriginalFilename());
            model.addAttribute("fileBName", fileB.getOriginalFilename());
        } catch (Exception e) {
            // 예외 처리 로직 (예: 에러 페이지로 리디렉션 또는 메시지 전달)
            logger.error("XML comparison failed", e);
            model.addAttribute("error", "XML 파일을 비교하는 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "analyzer-results"; // 결과를 보여줄 새로운 HTML 페이지
    }
}