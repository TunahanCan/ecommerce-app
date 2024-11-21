package com.example.ecommerceapp.service;

import com.example.ecommerceapp.events.KafkaInvoiceRejectedService;
import com.example.ecommerceapp.model.dto.ApiResponseDTO;
import com.example.ecommerceapp.model.dto.BaseInvoiceDTO;
import com.example.ecommerceapp.model.dto.InvoiceRejectedEventDTO;
import com.example.ecommerceapp.model.entities.Invoice;
import com.example.ecommerceapp.model.entities.User;
import com.example.ecommerceapp.model.enums.InvoiceStatus;
import com.example.ecommerceapp.repositories.InvoiceRepository;
import com.example.ecommerceapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final KafkaInvoiceRejectedService kafkaInvoiceRejectedService;

    @Value("${app.credit-limit}")
    private Double invoiceLimit;

    private String generateRejectedUid(){
        return UUID.randomUUID().toString();
    }

    public InvoiceService(InvoiceRepository invoiceRepository,
                          UserRepository userRepository,
                          KafkaInvoiceRejectedService kafkaInvoiceRejectedService) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.kafkaInvoiceRejectedService = kafkaInvoiceRejectedService;
    }

    public ApiResponseDTO<Void> processInvoice(BaseInvoiceDTO request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            return ApiResponseDTO.<Void>builder()
                    .success(false)
                    .message("User not found.")
                    .build();
        }

        List<Invoice> approvedInvoices = invoiceRepository.findByUserAndStatus(user.get(), InvoiceStatus.APPROVED);
        double totalApprovedAmount = approvedInvoices.stream()
                .mapToDouble(Invoice::getAmount)
                .sum();
        double newTotal = totalApprovedAmount + request.getAmount();
        boolean isApproved = newTotal <= invoiceLimit;
        InvoiceStatus status = isApproved ? InvoiceStatus.APPROVED : InvoiceStatus.REJECTED;

        Invoice invoice = new Invoice(
                request.getAmount(),
                request.getBillNo(),
                user.get(),
                request.getProductName(),
                status
        );
        invoiceRepository.save(invoice);

        if (!isApproved) {
            InvoiceRejectedEventDTO event = new InvoiceRejectedEventDTO(
                    user.get().getFirstName(),
                    user.get().getLastName(),
                    user.get().getEmail(),
                    request.getAmount(),
                    request.getProductName(),
                    request.getBillNo(),
                    "Invoice exceeds the limit.",
                    generateRejectedUid()
            );

            kafkaInvoiceRejectedService.sendRejectedInvoiceEvent(event);
        }

        String message = isApproved ? "Invoice accepted." : "Invoice rejected.";
        return new ApiResponseDTO<>(isApproved, message, null);
    }


    /**
     * Belirli bir duruma sahip faturaları getirir.
     *
     * @param status Fatura durumu (APPROVED veya REJECTED)
     * @return Faturaların listesi
     */
    public List<Invoice> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }
}
