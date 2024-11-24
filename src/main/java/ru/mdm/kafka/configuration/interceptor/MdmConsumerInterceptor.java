package ru.mdm.kafka.configuration.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Перехватчик сообщений из Kafka перед отправкой потребителю.
 */
@Slf4j
@Component
public class MdmConsumerInterceptor implements ConsumerInterceptor<String, Object> {

    /**
     * {@inheritDoc}
     *
     * @param consumerRecords записи, полученные из Kafka.
     * @return отправляем данные на дальнейшую обработку
     */
    @Override
    public ConsumerRecords<String, Object> onConsume(ConsumerRecords<String, Object> consumerRecords) {
        consumerRecords.iterator()
                .forEachRemaining(consumerRecord -> {
                    UUID correlationId = null;
                    Iterator<Header> iterator = consumerRecord.headers().iterator();
                    while (correlationId == null && iterator.hasNext()) {
                        Header next = iterator.next();
                        if (next.key().equals(KafkaHeaders.CORRELATION_ID)) {
                            correlationId = UUID.nameUUIDFromBytes(next.value());
                        }
                    }

                    log.debug(
                            "Received from topic={}, partitionsId={}, correlationId={}, messageKey={}, data={}, offset={}",
                            consumerRecord.topic(),
                            consumerRecord.partition(),
                            correlationId,
                            consumerRecord.key(),
                            consumerRecord.value(),
                            consumerRecord.offset()
                    );
                });
        return consumerRecords;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {
        // not implemented
    }

    @Override
    public void close() {
        // not implemented
    }

    @Override
    public void configure(Map<String, ?> map) {
        // not implemented
    }
}
