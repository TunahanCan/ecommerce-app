package com.example.ecommerceapp.events;


import com.example.ecommerceapp.model.entities.OutboxMessage;
import com.example.ecommerceapp.repositories.OutboxMessageRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Component
public class KafkaInvoiceRejectedProcessor {

    Logger log = LoggerFactory.getLogger(KafkaInvoiceRejectedProcessor.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxMessageRepository outboxMessageRepository;

    @Value("${app.kafka.rejected.topic}")
    private String topicName;

    /**
     * Constructs a new KafkaInvoiceRejectedProcessor.
     *
     * @param kafkaTemplate           The KafkaTemplate used for sending messages to Kafka.
     * @param outboxMessageRepository The repository for managing outbox messages.
     */
    public KafkaInvoiceRejectedProcessor(KafkaTemplate<String, String> kafkaTemplate, OutboxMessageRepository outboxMessageRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxMessageRepository = outboxMessageRepository;
    }

    /**
     * A scheduled task that processes outbox messages at a fixed rate.
     * It retrieves up to 10 outbox messages, sends them to the configured Kafka topic,
     * and removes them from the outbox if successfully sent.
     * <p>
     * This method is executed every 5 seconds.
     */
    @Scheduled(fixedRate = 5000)
    public void processOutboxMessages() {
        List<OutboxMessage> allMessages = outboxMessageRepository.findTop10ByOrderByCreatedAtAsc();
        for (OutboxMessage message : allMessages) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, message.getPayload());
            record.headers().add(new RecordHeader("messageId", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));
            record.headers().add(new RecordHeader("timestamp", Instant.now().toString().getBytes(StandardCharsets.UTF_8)));
            record.headers().add(new RecordHeader("event-type", message.getEventType().getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Sent message: {} to topic: {}", message.getPayload(), topicName);
                            outboxMessageRepository.delete(message);
                        } else {
                            log.error("Failed to send message: {} due to {}", message.getPayload(), ex.getMessage(), ex);
                        }
                    });
        }
    }
}
