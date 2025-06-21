package com.fortify.analyzer.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CrawlService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    // 결과를 저장할 디렉터리 경로를 정의합니다. 프로젝트 루트에 'crawled-results' 폴더가 생성됩니다.
    private final String resultsDirectoryPath = new File("crawled-results").getAbsolutePath();

    /**
     * 서비스가 시작될 때 결과 폴더가 있는지 확인하고 없으면 생성합니다.
     */
    @PostConstruct
    public void init() {
        File resultsDir = new File(resultsDirectoryPath);
        if (!resultsDir.exists()) {
            if (resultsDir.mkdirs()) {
                logger.info("Successfully created results directory at: {}", resultsDirectoryPath);
            } else {
                logger.error("Failed to create results directory at: {}", resultsDirectoryPath);
            }
        }
    }
    
    /**
     * 크롤링과 분석 스크립트를 순차적으로 실행하는 전체 프로세스입니다.
     */
    @Async
    public void runCrawlingAndAnalysis() {
        try {
            logger.info("====== Starting Crawling Process ======");
            // ★★★ crawler.py 실행 시 --output-dir 인자와 경로를 전달합니다. ★★★
            executeScript("scripts/crawler.py", "--output-dir", resultsDirectoryPath);
            logger.info("====== Crawling Process Finished ======");

            logger.info("====== Starting Analysis Process ======");
            // ★★★ languge.py 실행 시 --base-dir 인자와 경로를 전달합니다. ★★★
            executeScript("scripts/languge.py", "--base-dir", resultsDirectoryPath);
            logger.info("====== Analysis Process Finished ======");

        } catch (RuntimeException e) {
            logger.error("An error occurred during the script execution process.", e);
            // 비동기 작업에서 발생한 예외는 전역 예외 핸들러로 처리하거나 여기서 로깅/알림 처리를 할 수 있습니다.
        }
    }

    /**
     * 지정된 파이썬 스크립트를 인자와 함께 실행하는 내부 메서드
     * @param scriptPath resources/scripts/ 폴더 아래의 스크립트 경로
     * @param args 스크립트에 전달할 인자들
     */
    private void executeScript(String scriptPath, String... args) {
        try {
            URL scriptUrl = getClass().getClassLoader().getResource(scriptPath);
            if (scriptUrl == null) {
                throw new IOException("Script not found: " + scriptPath);
            }

            File scriptFile = new File(scriptUrl.toURI());
            
            // 명령어 리스트 생성 (python3, 스크립트 경로, 인자들 순서)
            List<String> command = new ArrayList<>();
            command.add("python3"); // 또는 "python"
            command.add(scriptFile.getAbsolutePath());
            command.addAll(Arrays.asList(args)); // ★★★ 전달받은 인자를 명령어 리스트에 추가합니다. ★★★

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // 에러 출력을 표준 출력으로 합쳐서 함께 읽습니다.

            logger.info("Executing command: {}", String.join(" ", processBuilder.command()));
            Process process = processBuilder.start();

            // 스크립트의 출력을 실시간으로 로깅
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("[Python] " + line);
                }
            }

            int exitCode = process.waitFor();
            logger.info("Script {} executed with exit code: {}", scriptPath, exitCode);

            if (exitCode != 0) {
                // exitCode가 0이 아니면 스크립트 실패로 간주하고 예외를 발생시킵니다.
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