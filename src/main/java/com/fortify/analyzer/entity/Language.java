// /src/main/java/com/fortify/analyzer/entity/Language.java

package com.fortify.analyzer.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "languages")
@Getter
@Setter
@NoArgsConstructor
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    // '다대다(Many-to-Many)' 관계: 이 관계의 주인은 Rule 엔티티의 'languages' 필드임을 명시합니다.
    @ManyToMany(mappedBy = "languages")
    private Set<Rule> rules = new HashSet<>();
}