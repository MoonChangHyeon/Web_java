package com.fortify.analyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category_info")
@Getter
@Setter
@NoArgsConstructor // 기본 생성자 추가
public class CategoryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String kingdomName;

    @Column(nullable = false)
    private int lastPage;

    public CategoryInfo(String kingdomName, int lastPage) {
        this.kingdomName = kingdomName;
        this.lastPage = lastPage;
    }
}