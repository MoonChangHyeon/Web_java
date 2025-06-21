// src/main/java/com/fortify/analyzer/repository/LogRepository.java
package com.fortify.analyzer.repository;

import com.fortify.analyzer.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}