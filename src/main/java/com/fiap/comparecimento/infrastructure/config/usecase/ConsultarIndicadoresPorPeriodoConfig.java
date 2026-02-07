package com.fiap.comparecimento.infrastructure.config.usecase;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.relatorios.implementation.ConsultarIndicadoresPorPeriodoUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsultarIndicadoresPorPeriodoConfig {

    @Bean
    public ConsultarIndicadoresPorPeriodoUseCaseImpl consultarIndicadoresPorPeriodoUseCase(PacienteGateway pacienteGateway) {
        return new ConsultarIndicadoresPorPeriodoUseCaseImpl(pacienteGateway);
    }
}
