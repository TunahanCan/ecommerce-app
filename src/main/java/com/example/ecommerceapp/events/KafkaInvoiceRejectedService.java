package com.example.ecommerceapp.events;


import com.example.ecommerceapp.model.dto.InvoiceRejectedEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaInvoiceRejectedService {

    Logger log = LoggerFactory.getLogger(KafkaInvoiceRejectedService.class);

    private final KafkaTemplate<String, InvoiceRejectedEventDTO> kafkaTemplate;

    @Value("${app.kafka.rejected.topic}")
    private String topicName;

    public KafkaInvoiceRejectedService(KafkaTemplate<String, InvoiceRejectedEventDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRejectedInvoiceEvent(InvoiceRejectedEventDTO eventDTO) {
        kafkaTemplate.send(topicName, eventDTO);
        log.info("Sent rejected invoice event to Kafka: {}", eventDTO);
    }
}
