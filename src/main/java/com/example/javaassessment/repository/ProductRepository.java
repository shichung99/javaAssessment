package com.example.javaassessment.repository;

import com.example.javaassessment.entity.CustomerEntity;
import com.example.javaassessment.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

}
