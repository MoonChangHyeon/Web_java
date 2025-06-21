package com.fortify.analyzer.dto;

/**
 * REST API에서 룰팩 정보를 전달할 때 사용하는 DTO입니다.
 */
public record RulePackInfoDto(
        Long id,
        String packName,
        String packVersion,
        String location
) {}
