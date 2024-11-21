package com.example.ecommerceapp.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class InvoiceRejectedEventDTO extends BaseInvoiceDTO{
    private String reason;
    private String rejectedUid;

    public InvoiceRejectedEventDTO(String firstName, String lastName, String email,
                                   Double amount, String productName, String billNo,
                                   String reason, String rejectedUid) {
        super(firstName, lastName, email, amount, productName, billNo);
        this.reason = reason;
        this.rejectedUid = rejectedUid;
    }
}
