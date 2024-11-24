package ru.mdm.kafka.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.mdm.kafka.configuration.interceptor.MdmConsumerInterceptor;
import ru.mdm.kafka.model.MdmKafkaMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация потребителя сообщений Kafka.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    /**
     * Spring свойства для Kafka.
     */
    private final KafkaProperties kafkaProperties;

    /**
     * Заполнение свойств для конфигурации потребителя Kafka.
     *
     * @return фабрика с параметрами.
     */
    @Bean
    public ConsumerFactory<String, MdmKafkaMessage<Object>> mdmDefaultConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildConsumerProperties());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        configProps.put(
                ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, MdmConsumerInterceptor.class.getName()
        );
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Конфигурация контейнера слушателя Kafka.
     *
     * @return фабрика контейнеров Kafka с параметрами.
     */
    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MdmKafkaMessage<Object>> mdmFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, MdmKafkaMessage<Object>>();
        factory.setConsumerFactory(mdmDefaultConsumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler((rec, ex) ->
                log.error("kafkaListener: An exception happened during a message processing. " +
                        "Не удалось обработать полученное сообщение.", ex),
                new FixedBackOff(0L, 3L)));
        return factory;
    }
}
