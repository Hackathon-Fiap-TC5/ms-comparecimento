package com.fiap.comparecimento.infrastructure.config.usecase;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.implementation.CalculaComparecimentoUseCaseImpl;
import com.fiap.comparecimento.application.usecase.historico.AdicionaItemHistoricoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalculaComparecimentoConfig {

    @Bean
    public CalculaComparecimentoUseCaseImpl calculaComparecimentoUseCase(PacienteGateway pacienteGateway,
                                                                         AdicionaItemHistoricoUseCase adicionaItemHistoricoUseCase){
        return new CalculaComparecimentoUseCaseImpl(pacienteGateway, adicionaItemHistoricoUseCase);
    }
}
