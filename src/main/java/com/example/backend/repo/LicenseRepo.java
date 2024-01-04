package com.example.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.po.License;

@Repository
public interface LicenseRepo extends JpaRepository<License, Integer> {
    @Query(value = "select lic_num from license where lic_id = ?1", nativeQuery = true)
    List<String> getLicenseList(Integer lic_id);
}
