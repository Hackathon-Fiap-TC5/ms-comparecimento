package com.fiap.comparecimento.infrastructure.config.usecase;

import com.fiap.comparecimento.application.gateway.HistoricoGateway;
import com.fiap.comparecimento.application.usecase.historico.implementation.ConsultaHistoricoUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsultaHistoricoConfig {

    @Bean
    public ConsultaHistoricoUseCaseImpl consultaHistoricoUseCase(HistoricoGateway historicoGateway){
        return new ConsultaHistoricoUseCaseImpl(historicoGateway);
    }
}
