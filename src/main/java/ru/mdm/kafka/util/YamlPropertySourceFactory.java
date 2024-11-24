package ru.mdm.kafka.util;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * Фабрика, позволяющая подключать конфигурационные файлы в формате YML/YAML.
 */
public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

    /**
     * Конфигурируем источник со свойствами из ресурса YML.
     *
     * @param name     наименование ресурса
     * @param resource сам ресурс
     * @return сконфигурируемый для работы объект со свойствами
     * @throws IOException ошибка с файлом настроек
     */
    @NonNull
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (!resource.getResource().exists()) {
            return super.createPropertySource(name, resource);
        }

        Properties properties = readPropertiesFromResource(resource);
        String resourceName = getResourceName(name, resource);
        return new PropertiesPropertySource(resourceName, properties);
    }

    /**
     * Извлечь свойства из ресурса.
     *
     * @param resource ресурс
     * @return конфигурационные свойства
     * @throws FileNotFoundException файл не найден
     */
    private static Properties readPropertiesFromResource(EncodedResource resource) throws FileNotFoundException {
        try {
            var factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            return Objects.requireNonNullElseGet(factory.getObject(), Properties::new);
        } catch (IllegalStateException e) {
            var cause = e.getCause();
            if (cause instanceof FileNotFoundException fileNotFoundException) {
                throw fileNotFoundException;
            }
            throw e;
        }
    }

    /**
     * Получить имя ресурса.
     *
     * @param name     имя реусурса
     * @param resource ресурс
     * @return имя ресурса
     */
    private static String getResourceName(String name, EncodedResource resource) {
        final String resourceName;
        if (StringUtils.hasText(name)) {
            resourceName = name;
        } else {
            resourceName = resource.getResource().getDescription();
        }
        return resourceName;
    }
}
