package com.example.ecommerceapp.repositories;

import com.example.ecommerceapp.model.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
