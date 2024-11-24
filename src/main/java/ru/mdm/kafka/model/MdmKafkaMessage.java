package ru.mdm.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Модель сообщения для Kafka.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MdmKafkaMessage<T> {

    /**
     * Тип события, характеризующее сообщение.
     */
    private String eventType;

    /**
     * Сообщение для Kafka.
     */
    private T eventData;
}
