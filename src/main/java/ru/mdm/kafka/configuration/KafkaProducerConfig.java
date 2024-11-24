package ru.mdm.kafka.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.mdm.kafka.configuration.interceptor.MdmProducerInterceptor;
import ru.mdm.kafka.configuration.serializer.MdmJsonSerializer;
import ru.mdm.kafka.model.MdmKafkaMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация производителя сообщений Kafka.
 */
@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    /**
     * Spring свойства для Kafka.
     */
    private final KafkaProperties kafkaProperties;

    /**
     * Бин конфигурации сообщений для Kafka.
     *
     * @return бин для работы с Kafka.
     */
    @Bean
    public KafkaTemplate<String, MdmKafkaMessage<Object>> mdmKafkaTemplate() {
        return new KafkaTemplate<>(mdmDefaultProducerFactory());
    }

    /**
     * Заполнение свойств для конфигурации производителя Kafka.
     *
     * @return фабрика с параметрами.
     */
    @Bean
    public ProducerFactory<String, MdmKafkaMessage<Object>> mdmDefaultProducerFactory() {
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MdmJsonSerializer.class);
        configProps.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, MdmProducerInterceptor.class.getName());
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
