package ru.mdm.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Класс доменного события.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Event<T> {

    private String eventType;
    private String subject;
    private LocalDateTime dateTime;
    private String requestId;
    private String ipAddress;
    private String serviceName;
    private String messageKey;
    private T data;

    public Event(String eventType, T data) {
        this.eventType = eventType;
        this.data = data;
        this.dateTime = LocalDateTime.now();
    }

    public Event(String eventType) {
        this.eventType = eventType;
        this.dateTime = LocalDateTime.now();
    }

    protected static <T> Event<T> of(String eventType, T data) {
        return new Event<>(eventType, data);
    }

    protected static <T> Event<T> of(String eventType) {
        return new Event<>(eventType);
    }
}
