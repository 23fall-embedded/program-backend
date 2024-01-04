package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.po.Test;
import com.example.backend.repo.TestRepo;

import java.util.List;

@Service
public class TestService {
    @Autowired
    private TestRepo testRepo;

    public List<Test> getAllData() {
        return testRepo.findAll();
    }
}
