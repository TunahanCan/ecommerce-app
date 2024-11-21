package com.example.ecommerceapp.service;

import com.example.ecommerceapp.events.KafkaInvoiceRejectedService;
import com.example.ecommerceapp.model.dto.*;
import com.example.ecommerceapp.model.entities.Invoice;
import com.example.ecommerceapp.model.entities.User;
import com.example.ecommerceapp.model.enums.InvoiceStatus;
import com.example.ecommerceapp.repositories.InvoiceRepository;
import com.example.ecommerceapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final KafkaInvoiceRejectedService kafkaInvoiceRejectedService;

    @Value("${app.credit-limit}")
    private Double invoiceLimit;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          UserRepository userRepository,
                          KafkaInvoiceRejectedService kafkaInvoiceRejectedService) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.kafkaInvoiceRejectedService = kafkaInvoiceRejectedService;
    }

    private String generateRejectedUid() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public ApiResponseDTO<Void> processInvoice(BaseInvoiceDTO request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getUser().getEmail());
        if (userOpt.isEmpty()) {
            return ApiResponseDTO.<Void>builder()
                    .success(false)
                    .message("User not found.")
                    .build();
        }

        User user = userOpt.get();
        List<Invoice> approvedInvoices = invoiceRepository.findByUserAndStatus(user, InvoiceStatus.APPROVED);
        double totalApprovedAmount = approvedInvoices.stream()
                .mapToDouble(Invoice::getAmount)
                .sum();
        double newTotal = totalApprovedAmount + request.getAmount();
        boolean isApproved = newTotal <= invoiceLimit;
        InvoiceStatus status = isApproved ? InvoiceStatus.APPROVED : InvoiceStatus.REJECTED;

        Invoice invoice = new Invoice(
                request.getAmount(),
                request.getBillNo(),
                user,
                request.getProductName(),
                status
        );
        invoiceRepository.save(invoice);

        if (!isApproved) {
            InvoiceRejectedDTO rejected = new InvoiceRejectedDTO();
            rejected.setRejectedUid(generateRejectedUid());
            rejected.setUser(new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail()));
            rejected.setReason("Invoice exceeds the limit.");
            rejected.setAmount(invoice.getAmount());
            rejected.setBillNo(invoice.getBillNo());
            rejected.setProductName(invoice.getProductName());
            kafkaInvoiceRejectedService.sendRejectedInvoiceEvent(rejected);
        }

        String message = isApproved ? "Invoice accepted." : "Invoice rejected.";
        return new ApiResponseDTO<>(isApproved, message, null);
    }

    public InvoicedApprovedDTO convertToApprovedDTO(Invoice invoice) {
        User user = invoice.getUser();
        if (user != null) {
            InvoicedApprovedDTO approved = new InvoicedApprovedDTO();
            approved.setUser(new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail()));
            approved.setAmount(invoice.getAmount());
            approved.setBillNo(invoice.getBillNo());
            approved.setProductName(invoice.getProductName());
            return approved;
        } else {
            throw new IllegalArgumentException("Invoice contains a null user, cannot convert to DTO.");
        }
    }

    public List<InvoicedApprovedDTO> convertToApprovedDTOList(List<Invoice> invoices) {
        return invoices.stream()
                .map(this::convertToApprovedDTO)
                .collect(Collectors.toList());
    }

    public InvoiceRejectedDTO convertToRejectedDTO(Invoice invoice) {
        User user = invoice.getUser();
        if (user != null) {
            InvoiceRejectedDTO rejected = new InvoiceRejectedDTO();
            rejected.setRejectedUid(generateRejectedUid());
            rejected.setUser(new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail()));
            rejected.setReason("Invoice rejected.");
            rejected.setAmount(invoice.getAmount());
            rejected.setBillNo(invoice.getBillNo());
            rejected.setProductName(invoice.getProductName());
            return rejected;
        } else {
            throw new IllegalArgumentException("Invoice contains a null user, cannot convert to DTO.");
        }
    }

    public List<InvoiceRejectedDTO> convertToRejectedDTOList(List<Invoice> invoices) {
        return invoices.stream()
                .map(this::convertToRejectedDTO)
                .collect(Collectors.toList());
    }

    public List<InvoicedApprovedDTO> getApprovedInvoices() {
        List<Invoice> invoices = invoiceRepository.findByStatus(InvoiceStatus.APPROVED);
        return convertToApprovedDTOList(invoices);
    }

    public List<InvoiceRejectedDTO> getRejectedInvoices() {
        List<Invoice> invoices = invoiceRepository.findByStatus(InvoiceStatus.REJECTED);
        return convertToRejectedDTOList(invoices);
    }
}
