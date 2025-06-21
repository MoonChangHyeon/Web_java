package com.fortify.analyzer.service;

import com.fortify.analyzer.dto.XmlComparisonResultDto;
import com.fortify.analyzer.repository.RulePackRepository;
import com.fortify.analyzer.repository.RuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyzerService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyzerService.class);

    // RulePack 관련 필드들은 현재 사용되지 않지만, 다른 기능을 위해 유지합니다.
    private final RuleRepository ruleRepository;
    private final RulePackRepository rulePackRepository;

    @Value("${crawler.results.path}")
    private String resultsBasePath;

    public AnalyzerService(RuleRepository ruleRepository, RulePackRepository rulePackRepository) {
        this.ruleRepository = ruleRepository;
        this.rulePackRepository = rulePackRepository;
    }

    /**
     * 두 개의 externalmetadata.xml 파일을 비교합니다.
     * @param fileA 원본 파일
     * @param fileB 새로운 파일
     * @return 비교 결과 DTO
     */
    public XmlComparisonResultDto compareExternalMetadata(MultipartFile fileA, MultipartFile fileB) throws Exception {
        // 1. 각 XML 파일 파싱하여 규칙 Map 생성
        Map<String, Map<String, String>> rulesA = parseXmlFile(fileA.getInputStream());
        Map<String, Map<String, String>> rulesB = parseXmlFile(fileB.getInputStream());

        // 2. 비교 결과를 담을 리스트 초기화
        List<Map<String, String>> commonRules = new ArrayList<>();
        List<Map<String, String>> onlyInA = new ArrayList<>();
        List<Map<String, String>> onlyInB = new ArrayList<>();

        // 3. A 파일 기준으로 비교
        for (String ruleIdA : rulesA.keySet()) {
            if (rulesB.containsKey(ruleIdA)) {
                // B에도 있으면 공통 규칙
                commonRules.add(rulesA.get(ruleIdA));
            } else {
                // B에 없으면 A에만 있는 규칙
                onlyInA.add(rulesA.get(ruleIdA));
            }
        }

        // 4. B 파일 기준으로 B에만 있는 규칙 찾기
        for (String ruleIdB : rulesB.keySet()) {
            if (!rulesA.containsKey(ruleIdB)) {
                onlyInB.add(rulesB.get(ruleIdB));
            }
        }

        logger.info("Comparison complete. Common: {}, Only in A: {}, Only in B: {}", commonRules.size(), onlyInA.size(), onlyInB.size());
        return new XmlComparisonResultDto(commonRules, onlyInA, onlyInB);
    }

    /**
     * XML 파일 스트림을 파싱하여 규칙 정보를 Map 형태로 반환합니다.
     * @param inputStream XML 파일의 InputStream
     * @return Key: InternalCategory, Value: 규칙 정보(Map)
     */
    private Map<String, Map<String, String>> parseXmlFile(InputStream inputStream) throws Exception {
        Map<String, Map<String, String>> ruleMap = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        doc.getDocumentElement().normalize();

        // <Mapping> 태그를 모두 가져옵니다.
        NodeList mappingList = doc.getElementsByTagName("Mapping");

        for (int i = 0; i < mappingList.getLength(); i++) {
            Node mappingNode = mappingList.item(i);
            if (mappingNode.getNodeType() == Node.ELEMENT_NODE) {
                Element mappingElement = (Element) mappingNode;
                
                String internalCategory = getTagValue("InternalCategory", mappingElement);
                String externalCategory = getTagValue("ExternalCategory", mappingElement);
                
                // InternalCategory를 고유 ID로 사용합니다.
                if (internalCategory != null && !internalCategory.isEmpty()) {
                    Map<String, String> ruleDetails = new HashMap<>();
                    ruleDetails.put("RuleID", internalCategory); // 화면 표시에 사용할 데이터
                    ruleDetails.put("Name", externalCategory);   // 화면 표시에 사용할 데이터
                    ruleMap.put(internalCategory, ruleDetails);
                }
            }
        }
        return ruleMap;
    }

    /**
     * 특정 부모 엘리먼트 아래에서 태그 이름으로 자식 엘리먼트의 텍스트 값을 가져오는 헬퍼 메서드
     */
    private String getTagValue(String tagName, Element parentElement) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0 && nodeList.item(0).getChildNodes().getLength() > 0) {
            return nodeList.item(0).getChildNodes().item(0).getNodeValue();
        }
        return ""; // 값이 없는 경우 빈 문자열 반환
    }

    /**
     * 크롤러의 분석 결과 JSON 파일을 읽어 문자열로 반환합니다.
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