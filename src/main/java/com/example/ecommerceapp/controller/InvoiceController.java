package com.example.ecommerceapp.controller;


import com.example.ecommerceapp.model.dto.ApiResponseDTO;
import com.example.ecommerceapp.model.dto.BaseInvoiceDTO;
import com.example.ecommerceapp.model.dto.InvoiceRejectedDTO;
import com.example.ecommerceapp.model.dto.InvoicedApprovedDTO;
import com.example.ecommerceapp.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /**
     * Submits a new invoice for processing.
     *
     * @param request The invoice details to be submitted.
     * @return A response indicating whether the invoice was accepted or rejected.
     */
    @Operation(summary = "Submit a new invoice")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice accepted"),
            @ApiResponse(responseCode = "400", description = "Invoice rejected")
    })
    @PostMapping("/submit")
    public ResponseEntity<ApiResponseDTO<Void>> submitInvoice(@RequestBody BaseInvoiceDTO request) {
        ApiResponseDTO<Void> response = invoiceService.processInvoice(request);
        HttpStatus status = response.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    /**
     * Retrieves all approved invoices.
     *
     * @return A list of approved invoices.
     */
    @Operation(summary = "Get all approved invoices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Approved invoices retrieved successfully")
    })
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

    /**
     * Retrieves all rejected invoices.
     *
     * @return A list of rejected invoices.
     */
    @Operation(summary = "Get all rejected invoices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rejected invoices retrieved successfully")
    })
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
