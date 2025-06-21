package com.fortify.analyzer.service;

import com.fortify.analyzer.entity.ExternalMapping;
import com.fortify.analyzer.entity.Rule;
import com.fortify.analyzer.entity.RulePack;
import com.fortify.analyzer.repository.RulePackRepository;
import com.fortify.analyzer.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 이 import는 유지됩니다.
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final LogService logService;

    private final RulePackRepository rulePackRepository;

    private final RuleRepository ruleRepository;

    // ✨ @Transactional 어노테이션 제거. 예외 처리는 Controller에서 담당.
    public String processAndSaveFile(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        return parseAndSaveSingleXml(new ByteArrayInputStream(bytes), file.getOriginalFilename());
    }

    // ✨ @Transactional 어노테이션 제거.
    public List<String> processAndSaveZipFile(MultipartFile zipFile) throws IOException {
        List<String> results = new ArrayList<>();
        Path tempDir = Files.createTempDirectory("unzip-");

        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            var zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                Path newPath = tempDir.resolve(zipEntry.getName());
                if (!zipEntry.isDirectory()) {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            
            List<File> xmlFiles = Files.walk(tempDir)
                    .filter(path -> path.getFileName().toString().equalsIgnoreCase("externalmetadata.xml"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            if (xmlFiles.isEmpty()) {
                results.add("경고: ZIP 파일 안에 'externalmetadata.xml' 파일이 없습니다.");
            } else {
                for (File xmlFile : xmlFiles) {
                    Path xmlPath = xmlFile.toPath();
                    String location = tempDir.relativize(xmlPath).toString(); 
                    try (InputStream is = Files.newInputStream(xmlPath)) {
                        // 개별 파일 처리 결과와 로그를 추가
                        String resultMessage = parseAndSaveSingleXml(is, location);
                        results.add(resultMessage);
                        logService.log("Upload-Zip", resultMessage);
                    } catch (Exception e) {
                        String errorMessage = "❌ (처리 실패) '" + location + "' 파일 처리 중 오류 발생.";
                        results.add(errorMessage);
                        logService.logError("Upload-Zip", errorMessage, e);
                    }
                }
            }
        } finally {
            Files.walk(tempDir)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
        return results;
    }
    
    // ✨ 실제 DB 작업이 일어나는 이 메소드에 @Transactional을 붙입니다.
    @Transactional
    public String parseAndSaveSingleXml(InputStream inputStream, String location) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        inputStream.transferTo(baos);
        byte[] bytes = baos.toByteArray();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(bytes));
        Element root = doc.getDocumentElement();

        NodeList packInfoNodes = root.getElementsByTagName("PackInfo");
        if (packInfoNodes.getLength() == 0) {
            throw new Exception("파일 '" + location + "'에서 <PackInfo>를 찾을 수 없습니다.");
        }
        Element packInfoElement = (Element) packInfoNodes.item(0);
        String packName = packInfoElement.getElementsByTagName("Name").item(0).getTextContent();
        String packId = packInfoElement.getElementsByTagName("PackID").item(0).getTextContent();
        String packVersion = packInfoElement.getElementsByTagName("Version").item(0).getTextContent();

        if (rulePackRepository.findByLocationAndPackVersion(location, packVersion).isPresent()) {
            String warningMessage = "경고: '" + location + "' 파일의 버전 '" + packVersion + "'은(는) 이미 DB에 존재하므로 건너뜁니다.";
            return warningMessage;
        }
        
        RulePack newRulePack = new RulePack();
        newRulePack.setPackName(packName);
        newRulePack.setPackId(packId);
        newRulePack.setPackVersion(packVersion);
        newRulePack.setLocation(location);
        rulePackRepository.save(newRulePack);

        Map<String, Rule> ruleCache = new HashMap<>();
        Set<String> addedMappings = new HashSet<>();
        NodeList mappingNodes = root.getElementsByTagName("Mapping");

        for (int i = 0; i < mappingNodes.getLength(); i++) {
            Element mappingElement = (Element) mappingNodes.item(i);
            String ruleName = mappingElement.getElementsByTagName("InternalCategory").item(0).getTextContent();
            String standardInfo = mappingElement.getElementsByTagName("ExternalCategory").item(0).getTextContent();

            String mappingKey = ruleName + "::" + standardInfo;
            if (addedMappings.contains(mappingKey)) continue;

            Rule currentRule = ruleCache.computeIfAbsent(ruleName, k -> {
                Rule newRule = new Rule();
                newRule.setRuleName(k);
                newRule.setRulePack(newRulePack);
                return ruleRepository.save(newRule);
            });
            
            ExternalMapping newMapping = new ExternalMapping();
            newMapping.setStandardInfo(standardInfo);
            newMapping.setRule(currentRule);
            currentRule.getMappings().add(newMapping);
            addedMappings.add(mappingKey);
        }
        ruleRepository.saveAll(ruleCache.values());
        
        return "✅ (처리 완료) '" + location + "' (버전: " + packVersion + ") 파일의 룰팩과 규칙들을 DB에 새로 추가했습니다.";
    }
}