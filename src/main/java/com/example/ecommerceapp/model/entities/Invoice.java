package com.example.ecommerceapp.model.entities;


import com.example.ecommerceapp.model.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String billNo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String productName;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    // Default constructor
    public Invoice() {
    }

    // Parameterized constructor
    public Invoice(Double amount, String billNo, User user, String productName, InvoiceStatus status) {
        this.amount = amount;
        this.billNo = billNo;
        this.user = user;
        this.productName = productName;
        this.status = status;
    }
}
