package com.fortify.analyzer.controller;

import com.fortify.analyzer.dto.XmlComparisonResultDto;
import com.fortify.analyzer.service.AnalyzerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/analyzer")
public class AnalyzerController {

    // Logger 선언 추가
    private static final Logger logger = LoggerFactory.getLogger(AnalyzerController.class);

    private final AnalyzerService analyzerService;

    public AnalyzerController(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @GetMapping("")
    public String analyzerPage() {
        return "analyzer";
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
            // 이제 logger가 선언되었으므로 이 코드가 정상적으로 동작합니다.
            logger.error("XML comparison failed", e);
            model.addAttribute("error", "XML 파일을 비교하는 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "analyzer-results";
    }
}