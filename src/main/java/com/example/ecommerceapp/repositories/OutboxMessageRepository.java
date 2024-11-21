package com.example.ecommerceapp.repositories;

import com.example.ecommerceapp.model.entities.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, Long> {
    List<OutboxMessage> findTop10ByOrderByCreatedAtAsc();
}
