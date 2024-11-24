package ru.mdm.kafka.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.kafka.support.KafkaHeaders;

/**
 * Дополнительные заголовки для Kafka.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MdmKafkaHeaders {

    /**
     * Заголовок для имени сервиса.
     */
    public static final String SERVICE_NAME = KafkaHeaders.PREFIX + "serviceName";

    /**
     * Заголовок для ip адреса.
     */
    public static final String IP_ADDRESS = KafkaHeaders.PREFIX + "ipAddress";

    /**
     * Заголовок для имени пользователя.
     */
    public static final String SUBJECT = KafkaHeaders.PREFIX + "subject";

    /**
     * Заголовок для времени создания события.
     */
    public static final String DATE_TIME = KafkaHeaders.PREFIX + "dateTime";

    /**
     * Заголовок для идентификатора запроса.
     */
    public static final String REQUEST_ID = KafkaHeaders.PREFIX + "requestId";

    /**
     * Заголовок для типа события в Kafka.
     */
    public static final String EVENT_TYPE = KafkaHeaders.PREFIX + "eventType";
}
