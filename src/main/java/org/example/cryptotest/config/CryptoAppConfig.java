package org.example.cryptotest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "crypto")
@Data
public class CryptoAppConfig {
    private String fixedRate;
    private Integer maxUserPool;
}