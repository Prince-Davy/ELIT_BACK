package com.elit.outildedecision.config;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {
    /**
     * Configuration for azure email service.
     */
    @Value("${azure.communication.connection-string}")
    private String azureConnectionString;

    /**
     * Configures and provides a bean for
     * EmailClient using Azure Communication Service.
     *
     * @return Configured EmailClient bean.
     */
    @Bean
    public EmailClient emailClient() {
        return new EmailClientBuilder().connectionString(azureConnectionString)
                .buildClient();
    }
}

