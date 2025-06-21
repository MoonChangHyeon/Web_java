// /src/main/java/com/fortify/analyzer/entity/ExternalMapping.java

package com.fortify.analyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "external_mappings")
@Getter
@Setter
@NoArgsConstructor
public class ExternalMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String standardInfo;

    // '다대일(Many-to-One)' 관계: '많은' ExternalMapping들이 '하나'의 Rule에 속합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false) // external_mappings 테이블에 생성될 외래 키(FK)
    private Rule rule;
}