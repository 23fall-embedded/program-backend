package com.example.backend.po;

import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

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
@Table(name = "params")
public class Params {
    @Id
    @Column(name = "ts")
    private Timestamp ts;

    @Column(name = "lic_id")
    private Integer licId;

    @Column(name = "img")
    private String img;

    @Column(name = "light")
    private String light;

    @Column(name = "temperature")
    private String temperature;

    @Column(name = "fire")
    private String fire;

    @Column(name = "humidity")
    private String humidity;

    @Column(name = "mq3")
    private String mq3;
}
