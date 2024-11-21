package com.example.ecommerceapp.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_messages")
@Getter
@Setter
public class OutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;
    private String payload;
    private LocalDateTime createdAt;

    public OutboxMessage(String eventType, String payload, LocalDateTime createdAt) {
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    public OutboxMessage() {
    }
}
