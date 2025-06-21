package com.fortify.analyzer.controller;

import com.fortify.analyzer.service.LogService;
import com.fortify.analyzer.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    // ✨ 로그 서비스를 컨트롤러에 주입합니다.
    private final LogService logService;

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    // 단일 파일 업로드 처리
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "오류: 비어있는 파일입니다. 파일을 선택해주세요.");
            return "redirect:/upload";
        }
        
        // ✨ Controller에서 예외를 처리하도록 변경
        try {
            String message = uploadService.processAndSaveFile(file);
            redirectAttributes.addFlashAttribute("message", message);
            logService.log("Upload", message); // 성공 로그 기록
        } catch (Exception e) {
            String errorMessage = "❌ (처리 실패) '" + file.getOriginalFilename() + "' 파일 처리 중 오류 발생.";
            redirectAttributes.addFlashAttribute("message", errorMessage + " 자세한 내용은 시스템 로그를 확인하세요.");
            logService.logError("Upload", errorMessage, e); // 실패 로그 기록
        }

        return "redirect:/upload";
    }

    // ZIP 파일 업로드 처리
    @PostMapping("/upload-zip")
    public String handleZipFileUpload(@RequestParam("zipfile") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("zip_message", "오류: 비어있는 파일입니다. ZIP 파일을 선택해주세요.");
            return "redirect:/upload";
        }
        if (!file.getOriginalFilename().toLowerCase().endsWith(".zip")) {
            redirectAttributes.addFlashAttribute("zip_message", "오류: ZIP 파일만 업로드할 수 있습니다.");
            return "redirect:/upload";
        }

        try {
            List<String> messages = uploadService.processAndSaveZipFile(file);
            redirectAttributes.addFlashAttribute("zip_messages", messages);
        } catch (Exception e) { // ✨ ZIP 처리 중 발생하는 예외도 Controller에서 처리
            String errorMessage = "❌ (처리 실패) '" + file.getOriginalFilename() + "' ZIP 파일 처리 중 심각한 오류 발생.";
            redirectAttributes.addFlashAttribute("zip_message", errorMessage + " 자세한 내용은 시스템 로그를 확인하세요.");
            logService.logError("Upload-Zip", errorMessage, e);
        }

        return "redirect:/upload";
    }
}