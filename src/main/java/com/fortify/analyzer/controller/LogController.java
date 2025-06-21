// src/main/java/com/fortify/analyzer/controller/LogController.java
package com.fortify.analyzer.controller;

import com.fortify.analyzer.entity.Log;
import com.fortify.analyzer.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LogController {

    private final LogRepository logRepository;

    @GetMapping("/logs")
    public String showLogs(Model model) {
        // DB에 저장된 모든 로그를 최신순으로 정렬하여 조회합니다.
        List<Log> logs = logRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        
        // 조회된 로그 목록을 'model'에 담아 View(HTML)로 전달합니다.
        model.addAttribute("logs", logs);
        
        // 'templates/logs.html' 파일을 사용자에게 보여줍니다.
        return "logs";
    }
}