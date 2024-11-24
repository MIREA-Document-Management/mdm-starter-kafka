package ru.mdm.kafka.configuration.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.LocalDateTime;

/**
 * Дополнительные настройки для сериализации объектов в JSON.
 */
public class MdmJsonSerializer extends JsonSerializer<Object> {

    public MdmJsonSerializer() {
        super(customizedObjectMapper());
    }

    private static ObjectMapper customizedObjectMapper() {
        ObjectMapper mapper = JacksonUtils.enhancedObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        SimpleModule localDateTimeSerialization = new SimpleModule();
        localDateTimeSerialization.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        mapper.registerModule(localDateTimeSerialization);
        return mapper;
    }
}
