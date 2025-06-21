// /src/main/java/com/fortify/analyzer/entity/Rule.java

package com.fortify.analyzer.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rules", uniqueConstraints = {
    // (룰 이름, 룰팩 ID) 조합이 유일하도록 복합 고유 키 제약조건을 설정합니다.
    @UniqueConstraint(columnNames = {"rule_name", "rule_pack_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    // '다대일(Many-to-One)' 관계: '많은' Rule들이 '하나'의 RulePack에 속합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_pack_id", nullable = false) // rules 테이블에 생성될 외래 키(FK) 컬럼
    private RulePack rulePack;

    // '일대다(One-to-Many)' 관계: '하나'의 Rule은 '많은' ExternalMapping을 가집니다.
    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExternalMapping> mappings = new ArrayList<>();

    // '다대다(Many-to-Many)' 관계: '많은' Rule들은 '많은' Language를 가질 수 있습니다.
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "rule_language_association", // 중간 연결 테이블의 이름
        joinColumns = @JoinColumn(name = "rule_id"), // 이 엔티티(Rule)를 가리키는 컬럼
        inverseJoinColumns = @JoinColumn(name = "language_id") // 반대편 엔티티(Language)를 가리키는 컬럼
    )
    private Set<Language> languages = new HashSet<>();
}