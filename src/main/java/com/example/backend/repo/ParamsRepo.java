package com.example.backend.repo;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.po.Params;

@Repository
public interface ParamsRepo extends JpaRepository<Params, Timestamp> {
    @Query(value = "select * from (select * from params order by ts desc) where rownum = 1", nativeQuery = true)
    Params getLatestParams();

    @Query(value = "select count(1) from params", nativeQuery = true)
    Integer countByParamsRows();
}
