package ru.mdm.kafka.service;

import reactor.core.publisher.Mono;
import ru.mdm.kafka.model.Event;

/**
 * Интерфейс для взаимодействия с Kafka.
 */
public interface KafkaService {

    /**
     * Заполняет метаданные события и отправляет его в Kafka в базовый топик сервиса.
     *
     * @param event событие
     */
    Mono<Void> sendEvent(Event<?> event);

    /**
     * Заполняет метаданные события и отправляет его в Kafka в указанный топик.
     *
     * @param topic нужный топик
     * @param event событие
     */
    Mono<Void> sendEvent(String topic, Event<?> event);
}
