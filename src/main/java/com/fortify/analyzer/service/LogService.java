// src/main/java/com/fortify/analyzer/service/LogService.java
package com.fortify.analyzer.service;

import com.fortify.analyzer.entity.Log;
import com.fortify.analyzer.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    /**
     * 일반 정보 로그를 기록합니다.
     * @param status 로그의 상태/카테고리 (e.g., "Upload", "Analyzer")
     * @param message 로그 메시지
     */
    @Transactional
    public void log(String status, String message) {
        Log logEntry = new Log();
        logEntry.setStatus(status);
        logEntry.setMessage(message);
        logRepository.save(logEntry);
    }

    /**
     * 예외가 발생했을 때 상세 정보와 함께 로그를 기록합니다.
     * @param status 로그의 상태/카테고리
     * @param message 에러 메시지
     * @param ex 발생한 예외 객체
     */
    @Transactional
    public void logError(String status, String message, Throwable ex) {
        Log logEntry = new Log();
        logEntry.setStatus(status);
        logEntry.setMessage(message);

        // 예외의 전체 스택 트레이스를 문자열로 변환하여 저장합니다.
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        logEntry.setTraceback(sw.toString());
        
        logRepository.save(logEntry);
    }
}