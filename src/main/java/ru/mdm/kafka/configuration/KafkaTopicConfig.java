package ru.mdm.kafka.configuration;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import ru.mdm.kafka.configuration.properties.KafkaTopicProperties;

import java.util.stream.Stream;

/**
 * Конфигурация топиков.
 */
@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    /**
     * Информация о создаваемых топиках.
     */
    @Valid
    private final KafkaTopicProperties topicProperties;

    /**
     * Создание топиков из настроек.
     */
    @Bean
    public KafkaAdmin.NewTopics mdmNewTopics() {
        return new KafkaAdmin.NewTopics(
                Stream.concat(
                                topicProperties.getSettingsDefaultTopics().stream(),
                                topicProperties.getSettingsCustomTopics() != null
                                        ? topicProperties.getSettingsCustomTopics().stream() : Stream.empty()
                        )
                        .map(topicSetting -> TopicBuilder.name(topicSetting.getName())
                                .partitions(topicSetting.getPartitionsCount())
                                .replicas(topicSetting.getReplicationFactor())
                                .build()
                        )
                        .toArray(NewTopic[]::new)
        );
    }
}
