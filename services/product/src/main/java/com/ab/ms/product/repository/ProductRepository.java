package com.ab.ms.product.repository;

import com.ab.ms.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);
}
