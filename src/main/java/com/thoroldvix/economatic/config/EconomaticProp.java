package com.thoroldvix.economatic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "economatic")
public record EconomaticProp(
    boolean updateOnStartup
) {
}
