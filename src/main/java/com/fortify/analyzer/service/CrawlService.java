package com.fortify.analyzer.service;

import com.fortify.analyzer.entity.CategoryInfo;
import com.fortify.analyzer.repository.CategoryInfoRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Service
public class CrawlService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    // 작업 상수를 정의하여 오타 방지
    private static final String CRAWLING_TASK = "CRAWLING";
    private static final String ANALYSIS_TASK = "ANALYSIS";

    @Value("${crawler.results.path}")
    private String resultsDirectoryPath;

    private final CategoryInfoRepository categoryInfoRepository;
    private final RestTemplate restTemplate;
    private final CrawlService self; // AOP 프록시를 통한 호출을 위해 자신을 주입받습니다.
    
    // 실행 중인 비동기 작업을 저장하는 Map
    private final Map<String, Future<?>> runningTasks = new ConcurrentHashMap<>();
    // 작업별 상태 메시지를 저장하는 Map
    private final Map<String, String> taskStatusMessages = new ConcurrentHashMap<>();

    public CrawlService(CategoryInfoRepository categoryInfoRepository, RestTemplate restTemplate, @Lazy CrawlService self) {
        this.categoryInfoRepository = categoryInfoRepository;
        this.restTemplate = restTemplate;
        this.self = self;
    }

    @PostConstruct
    public void initializeCategories() {
        // ... (이전과 동일) ...
    }

    // --- 크롤링 프로세스 관리 ---
    public void startCrawlingProcess() {
        if (isTaskRunning(CRAWLING_TASK)) {
            logger.warn("Crawling process is already running.");
            return;
        }
        taskStatusMessages.put(CRAWLING_TASK, "Crawling process started...");
        // 프록시를 통해 호출하여 @Async와 @Transactional이 적용되도록 합니다.
        Future<?> future = self.runCrawlingProcess();
        runningTasks.put(CRAWLING_TASK, future);
    }

    public void stopCrawlingProcess() {
        stopTask(CRAWLING_TASK);
    }

    // --- 분석 프로세스 관리 ---
    public void startAnalysisProcess() {
        if (isTaskRunning(ANALYSIS_TASK)) {
            logger.warn("Analysis process is already running.");
            return;
        }
        taskStatusMessages.put(ANALYSIS_TASK, "Analysis process started...");
        // 프록시를 통해 호출하여 @Async가 적용되도록 합니다.
        Future<?> future = self.runAnalysisProcess();
        runningTasks.put(ANALYSIS_TASK, future);
    }

    public void stopAnalysisProcess() {
        stopTask(ANALYSIS_TASK);
    }

    // --- 강제 중지 프로세스 ---
    public void forceStopCrawlingProcess() {
        forceStopTask(CRAWLING_TASK);
    }

    public void forceStopAnalysisProcess() {
        forceStopTask(ANALYSIS_TASK);
    }

    // --- 상태 조회 ---
    public Map<String, String> getTasksStatus() {
        Map<String, String> statuses = new HashMap<>();
        statuses.put(CRAWLING_TASK, getTaskStatus(CRAWLING_TASK));
        statuses.put(ANALYSIS_TASK, getTaskStatus(ANALYSIS_TASK));
        return statuses;
    }

    // --- 비동기 실행 메서드 (실제 로직) ---
    @Async("taskExecutor")
    @Transactional
    public Future<Void> runCrawlingProcess() {
        try {
            updateStatusMessage(CRAWLING_TASK, "Updating page numbers...");
            List<CategoryInfo> categories = categoryInfoRepository.findAll();
            for (CategoryInfo category : categories) {
                Thread.sleep(100); // 작업 중지 시 인터럽트를 받을 수 있도록 sleep 추가
                String kingdomName = category.getKingdomName();
                updateStatusMessage(CRAWLING_TASK, "Checking for new pages in category: " + kingdomName);
                while (true) {
                    Thread.sleep(100);
                    int pageToCheck = category.getLastPage() + 1;
                    if (checkPageExists(kingdomName, pageToCheck)) {
                        category.setLastPage(pageToCheck);
                        categoryInfoRepository.save(category);
                    } else {
                        break;
                    }
                }
            }

            updateStatusMessage(CRAWLING_TASK, "Executing crawlers...");
            List<CategoryInfo> updatedCategories = categoryInfoRepository.findAll();
            for (CategoryInfo category : updatedCategories) {
                Thread.sleep(100);
                updateStatusMessage(CRAWLING_TASK, "Crawling " + category.getKingdomName() + "...");
                executeCrawlerForKingdom(category);
            }
            updateStatusMessage(CRAWLING_TASK, "Crawling Finished Successfully.");
        } catch (InterruptedException e) {
            updateStatusMessage(CRAWLING_TASK, "Crawling process was interrupted by user.");
            // ✨ 트랜잭션을 명시적으로 롤백하도록 설정합니다.
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            updateStatusMessage(CRAWLING_TASK, "Error during crawling: " + e.getMessage());
            logger.error("Exception in crawling process", e);
        } finally {
            runningTasks.remove(CRAWLING_TASK);
            // 작업이 종료되었으므로, 최종 IDLE 상태를 모든 클라이언트에게 알립니다.
            updateStatusMessage(CRAWLING_TASK, taskStatusMessages.getOrDefault(CRAWLING_TASK, "Process finished."));
        }
        return new AsyncResult<>(null);
    }

    @Async("taskExecutor")
    public Future<Void> runAnalysisProcess() {
        try {
            updateStatusMessage(ANALYSIS_TASK, "Executing language analysis script...");
            Thread.sleep(1000); // 인터럽트를 받을 수 있도록 sleep 추가
            executeScript("scripts/languge.py", "--base-dir", resultsDirectoryPath);
            updateStatusMessage(ANALYSIS_TASK, "Analysis Finished Successfully.");
        } catch (InterruptedException e) {
            updateStatusMessage(ANALYSIS_TASK, "Analysis process was interrupted by user.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            updateStatusMessage(ANALYSIS_TASK, "Error during analysis: " + e.getMessage());
            logger.error("Exception in analysis process", e);
        } finally {
            runningTasks.remove(ANALYSIS_TASK);
            // 작업이 종료되었으므로, 최종 IDLE 상태를 모든 클라이언트에게 알립니다.
            updateStatusMessage(ANALYSIS_TASK, taskStatusMessages.getOrDefault(ANALYSIS_TASK, "Process finished."));
        }
        return new AsyncResult<>(null);
    }


    // --- 헬퍼 메서드 ---
    private boolean isTaskRunning(String taskName) {
        return runningTasks.containsKey(taskName) && !runningTasks.get(taskName).isDone();
    }
    
    private void stopTask(String taskName) {
        Future<?> future = runningTasks.get(taskName);
        if (future != null) {
            future.cancel(true); // true는 인터럽트를 통해 작업을 중지시킴
            logger.info("{} process cancellation requested.", taskName);
        }
    }

    private void forceStopTask(String taskName) {
        logger.warn("Force stopping task: {}", taskName);
        // 1. 먼저 정상적인 중지를 시도합니다.
        stopTask(taskName);

        // 2. 작업 맵에서 강제로 제거하여 상태를 초기화합니다.
        runningTasks.remove(taskName);

        // 3. 상태 메시지를 강제 종료로 업데이트합니다.
        updateStatusMessage(taskName, "Forcefully stopped by user.");
    }

    private String getTaskStatus(String taskName) {
        if (isTaskRunning(taskName)) {
            return "RUNNING: " + taskStatusMessages.getOrDefault(taskName, "Initializing...");
        }
        return "IDLE - " + taskStatusMessages.getOrDefault(taskName, "No recent activity.");
    }

    private void updateStatusMessage(String taskName, String message) {
        logger.info("[{}] {}", taskName, message);
        taskStatusMessages.put(taskName, message);
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