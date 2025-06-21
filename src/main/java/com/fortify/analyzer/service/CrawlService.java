package com.fortify.analyzer.service;

import com.fortify.analyzer.entity.CategoryInfo;
import com.fortify.analyzer.repository.CategoryInfoRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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

    @Value("${crawler.results.path}")
    private String resultsDirectoryPath;

    private final CategoryInfoRepository categoryInfoRepository;
    private final RestTemplate restTemplate;

    public CrawlService(CategoryInfoRepository categoryInfoRepository, RestTemplate restTemplate) {
        this.categoryInfoRepository = categoryInfoRepository;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void initializeCategories() {
        logger.info("Initializing category page data in the database...");
        Map<String, Integer> lastPages = Map.ofEntries(
                Map.entry("Input Validation and Representation", 10),
                Map.entry("API Abuse", 5),
                Map.entry("Security Features", 16),
                Map.entry("Time and State", 1),
                Map.entry("Errors", 1),
                Map.entry("Code Quality", 5),
                Map.entry("Encapsulation", 6),
                Map.entry("Environment", 33)
        );

        lastPages.forEach((kingdom, page) -> {
            if (categoryInfoRepository.findByKingdomName(kingdom).isEmpty()) {
                CategoryInfo newCategory = new CategoryInfo(kingdom, page);
                categoryInfoRepository.save(newCategory);
                logger.info("Saved initial data for category: {}", kingdom);
            }
        });
        logger.info("Category data initialization complete.");
    }

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
                    categoryInfoRepository.save(category);
                } else {
                    logger.info("No more new pages for {}. Last known page is {}", kingdomName, category.getLastPage());
                    break;
                }
            }
        }
        logger.info("====== Page Update Process Finished ======");
        logger.info("====== Starting Crawling Process based on updated data ======");

        List<CategoryInfo> updatedCategories = categoryInfoRepository.findAll();
        for (CategoryInfo category : updatedCategories) {
            logger.info("Executing crawler for {}: {} pages", category.getKingdomName(), category.getLastPage() + 1);
            executeCrawlerForKingdom(category);
        }

        logger.info("====== Starting Analysis Process ======");
        executeScript("scripts/languge.py", "--base-dir", resultsDirectoryPath);
        logger.info("====== Analysis Process Finished ======");
    }

    private boolean checkPageExists(String kingdomName, int pageNumber) {
        String url = "https://vulncat.fortify.com/ko/weakness?kingdom={kingdom}&po={page}";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class, kingdomName, pageNumber);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().contains("weaknessCell")) {
                return true;
            }
        } catch (Exception e) {
            logger.warn("Could not check page {} for {}: {}", pageNumber, kingdomName, e.getMessage());
        }
        return false;
    }

    private void executeCrawlerForKingdom(CategoryInfo category) {
        String kingdomName = category.getKingdomName();
        String totalPages = String.valueOf(category.getLastPage() + 1);
        executeScript("scripts/crawler.py",
                "--output-dir", resultsDirectoryPath,
                "--kingdom", kingdomName,
                "--pages", totalPages);
    }

    private void executeScript(String scriptPath, String... args) {
        try {
            URL scriptUrl = getClass().getClassLoader().getResource(scriptPath);
            if (scriptUrl == null) {
                throw new IOException("Script not found: " + scriptPath);
            }
            File scriptFile = new File(scriptUrl.toURI());
            List<String> command = new ArrayList<>();
            command.add("python3");
            command.add(scriptFile.getAbsolutePath());
            command.addAll(Arrays.asList(args));
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            logger.info("Executing command: {}", String.join(" ", processBuilder.command()));
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("[Python] " + line);
                }
            }
            int exitCode = process.waitFor();
            logger.info("Script {} executed with exit code: {}", scriptPath, exitCode);
            if (exitCode != 0) {
                throw new RuntimeException("Python script execution failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            if (e instanceof InterruptedException) {
                 Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to execute python script: " + scriptPath, e);
        }
    }
}