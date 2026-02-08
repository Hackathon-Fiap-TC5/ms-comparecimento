package com.fiap.comparecimento.application.usecase.calcula.comparecimento.implementation;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.CalculaComparecimentoUseCase;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.ProcessarComparecimentoUseCase;
import com.fiap.comparecimento.application.usecase.historico.AdicionaItemHistoricoUseCase;
import com.fiap.comparecimento.domain.enuns.ClassificacaoPacienteEnum;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.domain.model.HistoricoDomain;
import com.fiap.comparecimento.domain.model.PacienteDomain;

import java.time.OffsetDateTime;

public class ProcessarComparecimentoUseCaseImpl implements ProcessarComparecimentoUseCase {

    private final PacienteGateway pacienteGateway;
    private final AdicionaItemHistoricoUseCase adicionaItemHistoricoUseCase;
    private final CalculaComparecimentoUseCase calculaComparecimentoUseCase;

    public ProcessarComparecimentoUseCaseImpl(PacienteGateway pacienteGateway,
                                              AdicionaItemHistoricoUseCase adicionaItemHistoricoUseCase,
                                              CalculaComparecimentoUseCase calculaComparecimentoUseCase) {
        this.pacienteGateway = pacienteGateway;
        this.adicionaItemHistoricoUseCase = adicionaItemHistoricoUseCase;
        this.calculaComparecimentoUseCase = calculaComparecimentoUseCase;
    }

    @Override
    public void processaComparecimento(EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain) {

        if(pacienteGateway.buscarPorCns(eventoAgendamentoMessageDomain.getCns()).isEmpty()){
            PacienteDomain pacienteDomain = new PacienteDomain(eventoAgendamentoMessageDomain.getCns(), 100,
                    ClassificacaoPacienteEnum.MUITO_CONFIAVEL.toString(),
                    0,0,0,0,0 ,
                    OffsetDateTime.now()
            );
            pacienteGateway.atualizarInformacoesPaciente(pacienteDomain);
        }

        PacienteDomain pacienteDomain = pacienteGateway.consultar(eventoAgendamentoMessageDomain.getCns());
        calculaComparecimentoUseCase.calculaComparecimento(pacienteDomain, eventoAgendamentoMessageDomain);
        adicionaItemHistoricoUseCase.adicionaItemHistorico(buildHistorico(eventoAgendamentoMessageDomain));
    }

    private HistoricoDomain buildHistorico(EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain){
        HistoricoDomain historicoDomain = new HistoricoDomain(
                eventoAgendamentoMessageDomain.getCns(),
                eventoAgendamentoMessageDomain.getIdAgendamento(),
                eventoAgendamentoMessageDomain.getStatusConsulta().toString(),
                eventoAgendamentoMessageDomain.getStatusNotificacao().toString(),
                eventoAgendamentoMessageDomain.getDataEvento()
        );
        return historicoDomain;
    }
}
