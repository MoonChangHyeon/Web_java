package com.fortify.analyzer.service;

import com.fortify.analyzer.repository.RulePackRepository;
import com.fortify.analyzer.repository.RuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AnalyzerService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyzerService.class);

    private final RuleRepository ruleRepository;
    private final RulePackRepository rulePackRepository;

    @Value("${upload.path.fpr}")
    private String uploadPath;

    @Value("${crawler.results.path}")
    private String resultsBasePath;

    public AnalyzerService(RuleRepository ruleRepository, RulePackRepository rulePackRepository) {
        this.ruleRepository = ruleRepository;
        this.rulePackRepository = rulePackRepository;
    }
    
    // 참고: 기존의 FPR 분석/비교 관련 메서드들은 현재 DTO와 호환되지 않아 삭제했습니다.
    // 이 기능들이 필요하시면, 현재 DTO 구조에 맞춰 새로 작성해야 합니다.

    /**
     * 분석 결과 JSON 파일을 읽어 문자열로 반환합니다.
     * @param fileName 읽어올 파일명 (e.g., "summary_by_language.json")
     * @return 파일의 내용 (JSON 문자열)
     */
    public String getAnalysisResultJson(String fileName) throws IOException {
        Path filePath = Paths.get(resultsBasePath, "analysis", fileName);

        if (!Files.exists(filePath)) {
            logger.warn("Analysis file not found: {}", filePath);
            return String.format("{ \"error\": \"File not found: %s. Please run the crawler first.\" }", fileName);
        }
        
        return Files.readString(filePath, StandardCharsets.UTF_8);
    }
}