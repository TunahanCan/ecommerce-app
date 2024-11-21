package com.example.ecommerceapp.repositories;

import com.example.ecommerceapp.model.entities.Invoice;
import com.example.ecommerceapp.model.entities.User;
import com.example.ecommerceapp.model.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUserAndStatus(User user, InvoiceStatus status);
    List<Invoice> findByStatus(InvoiceStatus status);
}
