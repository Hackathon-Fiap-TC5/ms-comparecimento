package com.fiap.comparecimento.infrastructure.config.usecase;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.ConsultarIndiceComparecimentoPacienteUseCase;
import com.fiap.comparecimento.application.usecase.implementation.ConsultarIndiceComparecimentoPacienteUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsultarIndiceComparecimentoPacienteConfig {

    @Bean
    public ConsultarIndiceComparecimentoPacienteUseCaseImpl consultarIndiceComparecimentoPacienteUseCase(PacienteGateway pacienteGateway) {
        return new ConsultarIndiceComparecimentoPacienteUseCaseImpl(pacienteGateway);
    }
}
