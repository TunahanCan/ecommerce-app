package com.example.ecommerceapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class BaseInvoiceDTO {
    private String firstName;
    private String lastName;
    private String email;
    private Double amount;
    private String productName;
    private String billNo;
}
