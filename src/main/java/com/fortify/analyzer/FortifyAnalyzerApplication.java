package com.fortify.analyzer; // <-- 이 부분이 정확해야 합니다.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FortifyAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FortifyAnalyzerApplication.class, args);
    }

}