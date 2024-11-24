package ru.mdm.kafka.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.mdm.kafka.model.Event;
import ru.mdm.kafka.model.MdmKafkaHeaders;
import ru.mdm.kafka.model.MdmKafkaMessage;
import ru.mdm.kafka.service.KafkaService;
import ru.mdm.kafka.util.ReactiveContextHolder;

import java.util.UUID;

import static ru.mdm.kafka.configuration.serializer.LocalDateTimeSerializer.FORMATTER;

/**
 * Реализация сервиса для взаимодействия с Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    /**
     * Шаблон для отправки сообщений в Kafka.
     */
    private final KafkaTemplate<String, MdmKafkaMessage<Object>> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    /**
     * Топки для отправки доменных событий сервиса в Kafka.
     */
    @Value("${kafka.general-topic-name}")
    private String generalTopicName;

    @Value("${spring.application.name:Не определено}")
    private String systemName;

    @Override
    public Mono<Void> sendEvent(Event<?> event) {
        return fillEvent(event)
                .doOnNext(filledEvent -> send(generalTopicName, filledEvent))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<Void> sendEvent(String topic, Event<?> event) {
        return fillEvent(event)
                .doOnNext(filledEvent -> send(topic, filledEvent))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private static <T> Mono<Event<T>> fillEvent(Event<T> event) {
        return ReactiveContextHolder.getUserName()
                .map(userName -> {
                    if (event.getSubject() == null) {
                        return event.toBuilder().subject(userName).build();
                    }
                    return event;
                })
                .zipWith(ReactiveContextHolder.getRemoteIp(),
                        (eventModel, ip) -> eventModel.toBuilder().ipAddress(ip).build())
                .zipWith(ReactiveContextHolder.getRequestId(),
                        (eventModel, requestId) -> eventModel.toBuilder().requestId(requestId).build());
    }

    private <T> void  send(String topic, Event<T> event) {
        MessageBuilder<MdmKafkaMessage<T>> builder = MessageBuilder
                .withPayload(new MdmKafkaMessage<>(event.getEventType(), event.getData()))
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                .setHeader(KafkaHeaders.TIMESTAMP, System.currentTimeMillis())
                .setHeader(MdmKafkaHeaders.SERVICE_NAME, systemName)
                .setHeader(MdmKafkaHeaders.REQUEST_ID, event.getRequestId())
                .setHeader(MdmKafkaHeaders.SUBJECT, event.getSubject())
                .setHeader(MdmKafkaHeaders.DATE_TIME, event.getDateTime().format(FORMATTER))
                .setHeader(MdmKafkaHeaders.IP_ADDRESS, event.getIpAddress())
                .setHeader(MdmKafkaHeaders.EVENT_TYPE, event.getEventType());

        if (StringUtils.hasText(event.getMessageKey())) {
            builder.setHeader(KafkaHeaders.KEY, event.getMessageKey());
        }

        Message<MdmKafkaMessage<T>> message = builder.build();
        log.debug("KafkaServiceImpl#send: Message={}", message);
        kafkaTemplate.send(message);
    }
}
