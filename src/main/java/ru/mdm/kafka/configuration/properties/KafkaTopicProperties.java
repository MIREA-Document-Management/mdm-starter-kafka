package ru.mdm.kafka.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Свойства для конфигурации топиков.
 */
@Data
@NoArgsConstructor
@Validated
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaTopicProperties {

    /**
     * Стандартные топики.
     */
    @Valid
    private List<TopicSetting> settingsDefaultTopics;

    /**
     * Дополнительные топики и их настройки.
     */
    @Valid
    private List<TopicSetting> settingsCustomTopics;

    /**
     * Настройки топика.
     */
    @Data
    @Validated
    public static class TopicSetting {

        /**
         * Наименование топика.
         */
        @NotEmpty
        private String name;

        /**
         * Число разделов в топике.
         */
        private int partitionsCount = 3;

        /**
         * Число реплик топика.
         */
        private int replicationFactor = 1;
    }
}
