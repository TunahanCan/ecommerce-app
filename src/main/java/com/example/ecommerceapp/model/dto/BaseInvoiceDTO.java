package com.example.ecommerceapp.model.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;


public class BaseInvoiceDTO implements Serializable {

    private UserDTO user;
    private Double amount;
    private String productName;
    private String billNo;

    public BaseInvoiceDTO() {
    }

    @JsonCreator
    public BaseInvoiceDTO(
            @JsonProperty("user") UserDTO user,
            @JsonProperty("amount") Double amount,
            @JsonProperty("productName") String productName,
            @JsonProperty("billNo") String billNo) {
        this.user = user;
        this.amount = amount;
        this.productName = productName;
        this.billNo = billNo;
    }

    // Getters and Setters
    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseInvoiceDTO that = (BaseInvoiceDTO) obj;
        return (Objects.equals(user, that.user)) &&
                (Objects.equals(amount, that.amount)) &&
                (Objects.equals(productName, that.productName)) &&
                (Objects.equals(billNo, that.billNo));
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, amount, productName, billNo);
    }
}