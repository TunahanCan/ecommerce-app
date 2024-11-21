package com.example.ecommerceapp.events;


import com.example.ecommerceapp.model.dto.InvoiceRejectedDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Service
public class KafkaInvoiceRejectedService {

    Logger log = LoggerFactory.getLogger(KafkaInvoiceRejectedService.class);
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.rejected.topic}")
    private String topicName;

    public KafkaInvoiceRejectedService(ObjectMapper objectMapper,
                                       KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRejectedInvoiceEvent(InvoiceRejectedDTO invoiceRejectedDTO) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(invoiceRejectedDTO);
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, jsonMessage);
            record.headers().add(new RecordHeader("messageId", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));
            record.headers().add(new RecordHeader("timestamp", Instant.now().toString().getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Sent message: {} to topic: {}", jsonMessage, topicName);
                        } else {
                            log.error("Failed to send message: {} due to {}", jsonMessage, ex.getMessage(), ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Error while serializing message to JSON: {}", e.getMessage(), e);
        }
    }
}
