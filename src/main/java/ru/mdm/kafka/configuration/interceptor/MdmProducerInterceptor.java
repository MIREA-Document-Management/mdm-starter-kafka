package ru.mdm.kafka.configuration.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Перехватчик сообщений перед отправкой в Kafka.
 */
@Slf4j
@Component
public class MdmProducerInterceptor implements ProducerInterceptor<String, Object> {

    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> producerRecord) {
        log.debug(
                "Send kafka message into topic={}, messageKey={}, value={}",
                producerRecord.topic(),
                producerRecord.key(),
                producerRecord.value()
        );
        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        log.debug(
                "Acknowledge for topic={}, partitionsId={}, offset={}",
                metadata.topic(),
                metadata.partition(),
                metadata.offset()
        );
    }

    @Override
    public void close() {
        // not implemented
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // not implemented
    }
}
