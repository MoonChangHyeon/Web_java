package com.fortify.analyzer.service;

import com.fortify.analyzer.dto.AnalysisResult;
import com.fortify.analyzer.dto.ComparisonResultDto;
import com.fortify.analyzer.dto.RuleDto;
import com.fortify.analyzer.dto.RulePackInfoDto;
import com.fortify.analyzer.entity.Rule;
import com.fortify.analyzer.repository.RulePackRepository;
import com.fortify.analyzer.repository.RuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public AnalysisResult parseFpr(File fprFile, String rulePackInfo) {
        List<RuleDto> rules = getRulesByRulePackInfo(rulePackInfo);
        Map<String, RuleDto> ruleMap = rules.stream()
                .collect(Collectors.toMap(RuleDto::getRuleId, Function.identity()));

        Map<String, Integer> vulnerabilityCounts = new HashMap<>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fprFile);
            doc.getDocumentElement().normalize();

            NodeList vulnerabilityList = doc.getElementsByTagName("Vulnerability");

            for (int i = 0; i < vulnerabilityList.getLength(); i++) {
                Element vulnerability = (Element) vulnerabilityList.item(i);
                Element classInfo = (Element) vulnerability.getElementsByTagName("ClassInfo").item(0);
                String type = classInfo.getElementsByTagName("Type").item(0).getTextContent();

                if (ruleMap.containsKey(type)) {
                    vulnerabilityCounts.put(type, vulnerabilityCounts.getOrDefault(type, 0) + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new AnalysisResult(ruleMap, vulnerabilityCounts);
    }

    public RulePackInfoDto parseRulePackInfo(String xmlContent) {
        RulePackInfoDto dto = new RulePackInfoDto();
        try {
            File tempFile = new File(System.getProperty("java.io.tmpdir"), "rulepack.xml");
            Files.writeString(tempFile.toPath(), xmlContent);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(tempFile);
            doc.getDocumentElement().normalize();

            dto.setRulePackID(doc.getElementsByTagName("RulePackID").item(0).getTextContent());
            dto.setVersion(doc.getElementsByTagName("Version").item(0).getTextContent());
            dto.setSku(doc.getElementsByTagName("SKU").item(0).getTextContent());
            dto.setName(doc.getElementsByTagName("Name").item(0).getTextContent());
            dto.setDescription(doc.getElementsByTagName("Description").item(0).getTextContent());

            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }


    public List<RuleDto> getRulesByRulePackInfo(String rulePackInfo) {
        List<Rule> ruleEntities = ruleRepository.findByRulePackInfo(rulePackInfo);
        return ruleEntities.stream()
                .map(rule -> new RuleDto(
                        rule.getRuleId(),
                        rule.getName(),
                        rule.getGroupName(),
                        rule.getPrimaryCategory(),
                        rule.getSsdCategory(),
                        rule.getSsdSubCategory(),
                        rule.getNote()))
                .collect(Collectors.toList());
    }


    public ComparisonResultDto compareResults(AnalysisResult leftResult, AnalysisResult rightResult) {
        Map<String, RuleDto> leftRuleMap = leftResult.getRuleMap();
        Map<String, Integer> leftCounts = leftResult.getVulnerabilityCounts();
        Map<String, Integer> rightCounts = rightResult.getVulnerabilityCounts();

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(leftCounts.keySet());
        allKeys.addAll(rightCounts.keySet());

        List<ComparisonResultDto.ComparisonDetail> details = new ArrayList<>();
        for (String key : allKeys) {
            int leftCount = leftCounts.getOrDefault(key, 0);
            int rightCount = rightCounts.getOrDefault(key, 0);
            if (leftCount != rightCount) {
                RuleDto rule = leftRuleMap.getOrDefault(key, rightResult.getRuleMap().get(key));
                details.add(new ComparisonResultDto.ComparisonDetail(rule, leftCount, rightCount));
            }
        }

        return new ComparisonResultDto(details);
    }

    public String getAnalysisResultJson(String fileName) throws IOException {
        Path filePath = Paths.get(resultsBasePath, "analysis", fileName);

        if (!Files.exists(filePath)) {
            logger.warn("Analysis file not found: {}", filePath);
            return String.format("{ \"error\": \"File not found: %s. Please run the crawler first.\" }", fileName);
        }

        return Files.readString(filePath, StandardCharsets.UTF_8);
    }
}