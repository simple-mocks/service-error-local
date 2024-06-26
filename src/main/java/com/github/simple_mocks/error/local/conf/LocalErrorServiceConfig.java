package com.github.simple_mocks.error.local.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.simple_mocks.error.local.EnableLocalErrorService;
import com.github.simple_mocks.error.local.service.LocalErrorService;
import com.github.simple_mocks.error_service.ErrorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class LocalErrorServiceConfig implements ImportAware {
    private String configLocation;
    private Locale defaultLocale;

    /**
     * Construct local error service bean
     *
     * @param objectMapper   object mapper
     * @param resourceLoader resource loader
     * @return instance of local error service
     * @throws IOException resource loading exception
     */
    @Bean
    public ErrorService localErrorService(ObjectMapper objectMapper,
                                          DefaultResourceLoader resourceLoader) throws IOException {
        var patternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
        var errorsConfigs = Arrays.stream(patternResolver.getResources(configLocation))
                .filter(it -> Objects.requireNonNull(it.getFilename()).endsWith(".json"))
                .toList();
        return new LocalErrorService(defaultLocale, errorsConfigs, objectMapper);
    }

    @Override
    public void setImportMetadata(AnnotationMetadata metadata) {
        var attributes = metadata.getAnnotationAttributes(EnableLocalErrorService.class.getName());
        if (attributes == null) {
            throw new IllegalStateException("No attributes in annotation @EnableLocalErrorService");
        }
        var resourcePath = (String) attributes.get("value");
        if (StringUtils.isBlank(resourcePath)) {
            throw new IllegalArgumentException("Value of @EnableLocalErrorService should not be null or blank");
        }
        this.configLocation = resourcePath;
        var defaultLocaleCode = (String) attributes.get("defaultLocale");
        if (StringUtils.isBlank(defaultLocaleCode)) {
            throw new IllegalArgumentException("Default locale of @EnableLocalErrorService should not be null or blank");
        }
        this.defaultLocale = Locale.forLanguageTag(defaultLocaleCode);
    }

}
