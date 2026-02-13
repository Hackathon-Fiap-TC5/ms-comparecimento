package com.fiap.comparecimento.infrastructure.config.domain.service;

import com.fiap.comparecimento.application.gateway.FilaVivaGateway;
import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.domain.domainService.implementation.FilaVivaAsyncDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilaVivaAsyncConfig {

    @Bean
    public FilaVivaAsyncDomainServiceImpl filaVivaAsyncDomainServiceImpl(FilaVivaGateway filaVivaGateway,
                                                                         PacienteGateway pacienteGateway) {
        return new FilaVivaAsyncDomainServiceImpl(filaVivaGateway, pacienteGateway);
    }
}
