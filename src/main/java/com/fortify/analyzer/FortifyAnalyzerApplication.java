package com.fortify.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class FortifyAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FortifyAnalyzerApplication.class, args);
    }

}