package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.po.Test;
import com.example.backend.service.TestService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/all-data")
    public List<Test> getAllData() {
        return testService.getAllData();
    }
}
