package com.fortify.analyzer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

// ✨ 1. @Table 어노테이션을 추가하여 복합 유니크 제약조건을 설정합니다.
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"location", "packVersion"})
})
@Entity
@Getter
@Setter
public class RulePack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String packName;
    private String packId;
    private String packVersion;

    // ✨ 2. 기존 @Column(unique = true) 어노테이션은 삭제합니다.
    private String location;

    @OneToMany(mappedBy = "rulePack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rule> rules = new ArrayList<>();
}