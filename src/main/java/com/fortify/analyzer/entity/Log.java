// /src/main/java/com/fortify/analyzer/entity/Log.java

package com.fortify.analyzer.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "logs")
@Getter
@Setter
@NoArgsConstructor
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String status; // 'Upload', 'Search', 'Analyzer', 'System' 등

    @Column(nullable = false, length = 1024)
    private String message;

    @CreationTimestamp // 엔티티가 생성될 때의 시간이 자동으로 저장됩니다.
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Lob // 매우 긴 텍스트를 저장할 수 있는 타입 (TEXT)
    @Column(columnDefinition = "TEXT")
    private String traceback;
}