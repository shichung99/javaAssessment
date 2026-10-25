package com.example.javaassessment.repository;

import com.example.javaassessment.entity.SalesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SalesRepository extends JpaRepository<SalesEntity, UUID> {
    public List<SalesEntity> findBySalesDate(LocalDate salesDate);
}
