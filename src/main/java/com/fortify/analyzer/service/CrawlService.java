package com.fortify.analyzer.service;

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
import java.nio.file.Paths;

@Service
public class CrawlService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    /**
     * 크롤링과 분석 스크립트를 순차적으로 실행하는 전체 프로세스입니다.
     * @Async 어노테이션을 통해 이 메서드는 별도의 스레드에서 비동기적으로 실행됩니다.
     */
    @Async
    public void runCrawlingAndAnalysis() {
        try {
            logger.info("====== Starting Crawling Process ======");
            executeScript("scripts/crawler.py");
            logger.info("====== Crawling Process Finished ======");

            logger.info("====== Starting Analysis Process ======");
            executeScript("scripts/languge.py"); // crawler.py가 끝나면 language.py 실행
            logger.info("====== Analysis Process Finished ======");

        } catch (RuntimeException e) {
            logger.error("An error occurred during the script execution process.", e);
        }
    }

    /**
     * 지정된 파이썬 스크립트를 실행하는 내부 메서드
     * @param scriptPath resources/scripts/ 폴더 아래의 스크립트 경로
     */
    private void executeScript(String scriptPath) {
        try {
            URL scriptUrl = getClass().getClassLoader().getResource(scriptPath);
            if (scriptUrl == null) {
                throw new IOException("Script not found: " + scriptPath);
            }

            File scriptFile = new File(scriptUrl.toURI());
            // 시스템에 'python3'가 설치되어 있지 않다면 'python'으로 변경해야 할 수 있습니다.
            ProcessBuilder processBuilder = new ProcessBuilder("python3", scriptFile.getAbsolutePath());
            processBuilder.redirectErrorStream(true);

            logger.info("Executing python script: {}", scriptFile.getAbsolutePath());
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
            // InterruptedException 발생 시 스레드 인터럽트 상태를 다시 설정합니다.
            if (e instanceof InterruptedException) {
                 Thread.currentThread().interrupt();
            }
            // RuntimeException으로 예외를 감싸서 호출자에게 전파합니다.
            throw new RuntimeException("Failed to execute python script: " + scriptPath, e);
        }
    }
}