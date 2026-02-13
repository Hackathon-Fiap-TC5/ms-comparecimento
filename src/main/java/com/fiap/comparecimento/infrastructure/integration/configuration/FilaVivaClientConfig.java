package com.fiap.comparecimento.infrastructure.integration.configuration;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FilaVivaClientConfig {

    @Bean
    public Request.Options feignRequestOptions() {
        return new Request.Options(5000, TimeUnit.MILLISECONDS,
                5000, TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Feign.Builder feignBuilder(Request.Options options) {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .options(options);
    }
}
