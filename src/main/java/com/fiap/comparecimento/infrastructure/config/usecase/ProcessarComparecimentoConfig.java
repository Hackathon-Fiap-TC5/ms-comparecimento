package com.fiap.comparecimento.infrastructure.config.usecase;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.CalculaComparecimentoUseCase;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.implementation.ProcessarComparecimentoUseCaseImpl;
import com.fiap.comparecimento.application.usecase.historico.AdicionaItemHistoricoUseCase;
import com.fiap.comparecimento.infrastructure.producer.ComparecimentoProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessarComparecimentoConfig {

    @Bean
    public ProcessarComparecimentoUseCaseImpl processarComparecimentoUseCase(PacienteGateway pacienteGateway,
                                                                             AdicionaItemHistoricoUseCase adicionaItemHistoricoUseCase,
                                                                             CalculaComparecimentoUseCase calculaComparecimentoUseCase,
                                                                             ComparecimentoProducer comparecimentoProducer){

        return new ProcessarComparecimentoUseCaseImpl(pacienteGateway, adicionaItemHistoricoUseCase,
                calculaComparecimentoUseCase, comparecimentoProducer);
    }
}
