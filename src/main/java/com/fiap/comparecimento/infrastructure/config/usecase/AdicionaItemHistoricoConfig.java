package com.fiap.comparecimento.infrastructure.config.usecase;

import com.fiap.comparecimento.application.gateway.HistoricoGateway;
import com.fiap.comparecimento.application.usecase.historico.implementation.AdicionaItemHistoricoUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdicionaItemHistoricoConfig {

    @Bean
    public AdicionaItemHistoricoUseCaseImpl adicionaItemHistoricoUseCase(HistoricoGateway historicoGateway){
        return new AdicionaItemHistoricoUseCaseImpl(historicoGateway);
    }
}
