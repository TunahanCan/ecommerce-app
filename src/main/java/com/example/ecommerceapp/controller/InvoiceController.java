package com.example.ecommerceapp.controller;


import com.example.ecommerceapp.model.dto.ApiResponseDTO;
import com.example.ecommerceapp.model.dto.BaseInvoiceDTO;
import com.example.ecommerceapp.model.dto.InvoiceRejectedDTO;
import com.example.ecommerceapp.model.dto.InvoicedApprovedDTO;
import com.example.ecommerceapp.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponseDTO<Void>> submitInvoice(@RequestBody BaseInvoiceDTO request) {
        ApiResponseDTO<Void> response = invoiceService.processInvoice(request);
        HttpStatus status = response.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponseDTO<List<InvoicedApprovedDTO>>> getApprovedInvoices() {
        List<InvoicedApprovedDTO> invoicesDto = invoiceService.getApprovedInvoices();
        return ResponseEntity.ok(
                ApiResponseDTO.<List<InvoicedApprovedDTO>>builder()
                        .success(true)
                        .message("Approved invoices retrieved.")
                        .data(invoicesDto)
                        .build()
        );
    }

    @GetMapping("/rejected")
    public ResponseEntity<ApiResponseDTO<List<InvoiceRejectedDTO>>> getRejectedInvoices() {
        List<InvoiceRejectedDTO> invoices = invoiceService.getRejectedInvoices();
        return ResponseEntity.ok(
                ApiResponseDTO.<List<InvoiceRejectedDTO>>builder()
                        .success(true)
                        .message("Rejected invoices retrieved.")
                        .data(invoices)
                        .build()
        );
    }
}
