package com.example.ecommerceapp.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class InvoiceRejectedDTO extends BaseInvoiceDTO implements Serializable {

    private String reason;
    private String rejectedUid;

    public InvoiceRejectedDTO() {
        super();
    }

    public InvoiceRejectedDTO(UserDTO user, Double amount, String productName, String billNo,
                              String reason, String rejectedUid) {
        super(user, amount, productName, billNo);
        this.reason = reason;
        this.rejectedUid = rejectedUid;
    }

    // Getters and Setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRejectedUid() {
        return rejectedUid;
    }

    public void setRejectedUid(String rejectedUid) {
        this.rejectedUid = rejectedUid;
    }

    // Equals and HashCode using java.util.Objects
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        InvoiceRejectedDTO that = (InvoiceRejectedDTO) obj;
        return Objects.equals(reason, that.reason) &&
                Objects.equals(rejectedUid, that.rejectedUid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reason, rejectedUid);
    }

}