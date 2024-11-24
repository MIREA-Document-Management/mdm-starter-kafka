package ru.mdm.kafka;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.mdm.kafka.util.YamlPropertySourceFactory;

/**
 * Автоконфигурация для стартера Kafka.
 */
@Configuration
@ComponentScan(basePackages = {"ru.mdm.kafka"})
@PropertySource(value = "classpath:kafka.yml", factory = YamlPropertySourceFactory.class)
public class MdmKafkaAutoConfiguration {
}
