package com.example.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.po.Test;

@Repository
public interface TestRepo extends JpaRepository<Test, Long> {
    
}
