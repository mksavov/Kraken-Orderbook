package com.kraken.orderbook.messaging;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ReactiveKafkaSender {
    private final KafkaSender<String, String> sender;

    public ReactiveKafkaSender() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        SenderOptions<String, String> senderOptions = SenderOptions.create(props);
        this.sender = KafkaSender.create(senderOptions);
    }

    public void send(String topic, String key, String data) {
        sender.send(Mono.just(SenderRecord.create(new ProducerRecord<>(topic, key, data), key)))
                .doOnError(e -> log.info("Send failed, " + e.getMessage()))
                .subscribe(
                        this::handleResult,
                        error -> log.error("Error sending message: " + error.getMessage()),
                        () -> log.info("Send stream completed.")
                );
    }

    private void handleResult(SenderResult<String> result) {
        RecordMetadata metadata = result.recordMetadata();
        if (metadata != null) {
            log.info("Message sent successfully to topic " + metadata.topic() +
                    " partition " + metadata.partition() +
                    " at offset " + metadata.offset() +
                    " timestamp " + metadata.timestamp());
        } else {
            Throwable ex = result.exception();
            if (ex != null) {
                log.error("Error sending message: " + ex.getMessage());
            } else {
                log.error("Message send failed without exception.");
            }
        }
    }
}
