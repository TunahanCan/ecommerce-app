package com.example.ecommerceapp.model.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class InvoicedApprovedDTO extends BaseInvoiceDTO implements Serializable {

    public InvoicedApprovedDTO() {
        super();
    }

    @JsonCreator
    public InvoicedApprovedDTO(
            @JsonProperty("user") UserDTO user,
            @JsonProperty("amount") Double amount,
            @JsonProperty("productName") String productName,
            @JsonProperty("billNo") String billNo) {
        super(user, amount, productName, billNo);
    }
}
