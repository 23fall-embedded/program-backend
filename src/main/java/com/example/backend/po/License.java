package com.example.backend.po;

import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "license")
public class License {
    @Id
    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "lic_id")
    private Integer licId;

    @Column(name = "lic_num")
    private String licNum;
}
