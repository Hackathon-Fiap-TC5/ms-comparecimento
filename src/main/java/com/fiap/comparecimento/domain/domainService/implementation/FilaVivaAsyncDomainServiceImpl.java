package com.fiap.comparecimento.domain.domainService.implementation;

import com.fiap.comparecimento.application.gateway.FilaVivaGateway;
import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.domain.domainService.FilaVivaAsyncDomainService;
import com.fiap.comparecimento.domain.enuns.ClassificacaoPacienteEnum;
import com.fiap.comparecimento.domain.enuns.StatusConsultaEnum;
import com.fiap.comparecimento.domain.enuns.SugestaoCondutaEnum;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.domain.model.FilaVivaDomain;
import com.fiap.comparecimento.domain.model.PacienteDomain;

public class FilaVivaAsyncDomainServiceImpl implements FilaVivaAsyncDomainService {

    private final FilaVivaGateway filaVivaGateway;
    private final PacienteGateway pacienteGateway;

    public FilaVivaAsyncDomainServiceImpl(FilaVivaGateway filaVivaGateway,
                                          PacienteGateway pacienteGateway) {
        this.filaVivaGateway = filaVivaGateway;
        this.pacienteGateway = pacienteGateway;
    }

    @Override
    public void processarFilaViva(EventoAgendamentoMessageDomain evento) {

        if (!isCanceladoOuFalta(evento)) {
            return;
        }

        PacienteDomain paciente = pacienteGateway.consultaPacienteFilaViva();

        if (paciente == null) {
            return;
        }

        FilaVivaDomain filaViva = new FilaVivaDomain(
                evento.getIdAgendamento(),
                paciente.getCns(),
                SugestaoCondutaEnum.REALOCACAO_IMEDIATA.getDescricao(),
                ClassificacaoPacienteEnum.CONFIAVEL.getDescricao(),
                "Paciente elegível para reaproveitamento imediato da vaga"
        );

        filaVivaGateway.publicarFilaViva(filaViva);
    }

    private boolean isCanceladoOuFalta(EventoAgendamentoMessageDomain evento) {
        return evento.getStatusConsulta() == StatusConsultaEnum.CANCELADO
                || evento.getStatusConsulta() == StatusConsultaEnum.FALTA;
    }
}
