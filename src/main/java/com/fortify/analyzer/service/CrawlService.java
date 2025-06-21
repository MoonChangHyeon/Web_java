package com.fortify.analyzer.service;

import com.fortify.analyzer.entity.CategoryInfo;
import com.fortify.analyzer.repository.CategoryInfoRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CrawlService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);
    private final String resultsDirectoryPath = new File("crawled-results").getAbsolutePath();

    private final CategoryInfoRepository categoryInfoRepository;
    private final RestTemplate restTemplate; // RestTemplate 주입

    // 생성자를 통한 의존성 주입
    public CrawlService(CategoryInfoRepository categoryInfoRepository, RestTemplate restTemplate) {
        this.categoryInfoRepository = categoryInfoRepository;
        this.restTemplate = restTemplate;
    }

    // ... (initializeCategories 메서드는 그대로 유지) ...
    @PostConstruct
    public void initializeCategories() {
        // ... (이전과 동일)
    }

    /**
     * DB의 페이지 정보를 최신화하고, 최신화된 정보를 바탕으로 크롤러를 실행하는 새로운 메인 메서드.
     */
    @Async
    @Transactional
    public void updatePagesAndExecuteCrawler() {
        logger.info("====== Starting Page Update Process ======");

        List<CategoryInfo> categories = categoryInfoRepository.findAll();
        for (CategoryInfo category : categories) {
            String kingdomName = category.getKingdomName();
            logger.info("Checking for new pages in category: {}", kingdomName);

            while (true) {
                int pageToCheck = category.getLastPage() + 1;
                boolean nextPageExists = checkPageExists(kingdomName, pageToCheck);

                if (nextPageExists) {
                    logger.info("New page found for {}: page {}", kingdomName, pageToCheck);
                    category.setLastPage(pageToCheck);
                    categoryInfoRepository.save(category); // DB 업데이트
                } else {
                    logger.info("No more new pages for {}. Last known page is {}", kingdomName, category.getLastPage());
                    break; // 다음 페이지가 없으면 루프 종료
                }
            }
        }
        logger.info("====== Page Update Process Finished ======");
        logger.info("====== Starting Crawling Process based on updated data ======");

        // 업데이트된 최신 정보로 크롤러 실행
        List<CategoryInfo> updatedCategories = categoryInfoRepository.findAll();
        for (CategoryInfo category : updatedCategories) {
            logger.info("Executing crawler for {}: {} pages", category.getKingdomName(), category.getLastPage() + 1);
            executeCrawlerForKingdom(category);
        }

        // 크롤링 완료 후, 분석 스크립트 실행
        logger.info("====== Analysis Process Finished ======");
        executeScript("scripts/languge.py", "--base-dir", resultsDirectoryPath);
        logger.info("====== Analysis Process Finished ======");
    }

    /**
     * 특정 카테고리의 특정 페이지가 웹에 존재하는지 확인합니다.
     * @param kingdomName 카테고리 이름
     * @param pageNumber 확인할 페이지 번호
     * @return 페이지 존재 여부
     */
    private boolean checkPageExists(String kingdomName, int pageNumber) {
        String url = "https://vulncat.fortify.com/ko/weakness?kingdom={kingdom}&po={page}";
        try {
            // exchange 메서드는 GET 요청을 보내고 응답을 ResponseEntity로 받습니다.
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class, kingdomName, pageNumber);
            
            // 200 OK 응답이고, 페이지 내용에 취약점 항목(.weaknessCell)이 있으면 페이지가 존재하는 것으로 간주
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().contains("weaknessCell")) {
                return true;
            }
        } catch (Exception e) {
            logger.warn("Could not check page {} for {}: {}", pageNumber, kingdomName, e.getMessage());
        }
        return false;
    }

    /**
     * 단일 카테고리에 대해 Python 크롤러를 실행합니다.
     */
    private void executeCrawlerForKingdom(CategoryInfo category) {
        String kingdomName = category.getKingdomName();
        // 페이지 수는 0부터 시작하므로 +1
        String totalPages = String.valueOf(category.getLastPage() + 1);

        executeScript("scripts/crawler.py",
                "--output-dir", resultsDirectoryPath,
                "--kingdom", kingdomName,
                "--pages", totalPages);
    }
    
    // ... (executeScript 메서드는 기존과 동일하게 유지) ...
    private void executeScript(String scriptPath, String... args) {
        // ... (이전과 동일)
    }

    @Value("${crawler.results.path}")
    private String resultsDirectoryPath;
}