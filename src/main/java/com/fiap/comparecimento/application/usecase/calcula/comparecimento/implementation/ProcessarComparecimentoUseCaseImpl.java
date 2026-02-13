package com.fiap.comparecimento.application.usecase.calcula.comparecimento.implementation;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.CalculaComparecimentoUseCase;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.ProcessarComparecimentoUseCase;
import com.fiap.comparecimento.application.usecase.historico.AdicionaItemHistoricoUseCase;
import com.fiap.comparecimento.domain.domainService.FilaVivaAsyncDomainService;
import com.fiap.comparecimento.domain.enuns.ClassificacaoPacienteEnum;
import com.fiap.comparecimento.domain.enuns.SugestaoCondutaEnum;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.domain.model.EventoComparecimentoMessageDomain;
import com.fiap.comparecimento.domain.model.HistoricoDomain;
import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.infrastructure.producer.ComparecimentoProducer;
import com.fiap.comparecimento.infrastructure.producer.mappers.ComparecimentoProducerMapper;
import com.fiap.comparecimentoDomain.gen.model.EventoComparecimentoMessageDto;

import java.time.OffsetDateTime;

public class ProcessarComparecimentoUseCaseImpl implements ProcessarComparecimentoUseCase {

    private final PacienteGateway pacienteGateway;
    private final AdicionaItemHistoricoUseCase adicionaItemHistoricoUseCase;
    private final CalculaComparecimentoUseCase calculaComparecimentoUseCase;
    private final ComparecimentoProducer comparecimentoProducer;
    private final FilaVivaAsyncDomainService filaVivaAsyncDomainService;

    public ProcessarComparecimentoUseCaseImpl(
            PacienteGateway pacienteGateway,
            AdicionaItemHistoricoUseCase adicionaItemHistoricoUseCase,
            CalculaComparecimentoUseCase calculaComparecimentoUseCase,
            ComparecimentoProducer comparecimentoProducer,
            FilaVivaAsyncDomainService filaVivaAsyncDomainService
    ) {
        this.pacienteGateway = pacienteGateway;
        this.adicionaItemHistoricoUseCase = adicionaItemHistoricoUseCase;
        this.calculaComparecimentoUseCase = calculaComparecimentoUseCase;
        this.comparecimentoProducer = comparecimentoProducer;
        this.filaVivaAsyncDomainService = filaVivaAsyncDomainService;
    }

    @Override
    public void processaComparecimento(EventoAgendamentoMessageDomain evento) {

        filaVivaAsyncDomainService.processarFilaViva(evento);

        PacienteDomain paciente = obterOuCriarPaciente(evento);

        processarComparecimentoPaciente(paciente, evento);
    }

    private PacienteDomain obterOuCriarPaciente(EventoAgendamentoMessageDomain evento) {

        PacienteDomain paciente = pacienteGateway.consultar(evento.getCns());

        if (paciente == null) {
            paciente = criarPacienteInicial(evento);
            pacienteGateway.criaOuAtualizarInformacoesPaciente(paciente);
        }

        return paciente;
    }

    private void processarComparecimentoPaciente(
            PacienteDomain paciente,
            EventoAgendamentoMessageDomain evento
    ) {

        calculaComparecimentoUseCase.calculaComparecimento(paciente, evento);

        adicionaItemHistoricoUseCase.adicionaItemHistorico(
                buildHistorico(evento)
        );

        comparecimentoProducer.sendSugestions(
                buildComparecimentoPayload(evento.getIdAgendamento(), paciente)
        );
    }

    private PacienteDomain criarPacienteInicial(EventoAgendamentoMessageDomain evento) {
        return new PacienteDomain(
                evento.getCns(),
                100,
                ClassificacaoPacienteEnum.MUITO_CONFIAVEL.name(),
                0, 0, 0, 0, 0,
                OffsetDateTime.now()
        );
    }

    private EventoComparecimentoMessageDto buildComparecimentoPayload(
            Long idAgendamento,
            PacienteDomain paciente
    ) {

        ClassificacaoPacienteEnum classificacao =
                ClassificacaoPacienteEnum.valueOf(paciente.getClassificacao());

        SugestaoCondutaEnum sugestaoConduta = sugerirConduta(classificacao);

        EventoComparecimentoMessageDomain evento =
                new EventoComparecimentoMessageDomain(
                        idAgendamento,
                        paciente.getCns(),
                        sugestaoConduta.name(),
                        paciente.getIcc(),
                        justificativaConduta(paciente)
                );

        return ComparecimentoProducerMapper.INSTANCE
                .toEventoComparecimentoMessageDto(evento);
    }

    private SugestaoCondutaEnum sugerirConduta(ClassificacaoPacienteEnum classificacao) {
        return switch (classificacao) {
            case MUITO_CONFIAVEL -> SugestaoCondutaEnum.MANTER_FLUXO;
            case CONFIAVEL, COMPARECIMENTO_PROVAVEL -> SugestaoCondutaEnum.MONITORAR;
            case COMPARECIMENTO_INCERTO -> SugestaoCondutaEnum.CONFIRMAR_AGENDAMENTO;
            case BAIXA_PROBABILIDADE_DE_COMPARECIMENTO ->
                    SugestaoCondutaEnum.REAGENDAMENTO_PREVENTIVO;
            case PROVAVEL_NAO_COMPARECIMENTO, CRITICO, REALOCACAO_POSSIVEL ->
                    SugestaoCondutaEnum.ALOCACAO_ALTERNATIVA;
            case REALOCACAO_IMEDIATA -> SugestaoCondutaEnum.REALOCACAO_IMEDIATA;
        };
    }

    private String justificativaConduta(PacienteDomain paciente) {

        int agendamentos = paciente.getTotalAgendamentos();
        int comparecimentos = paciente.getTotalComparecimentos();
        int faltas = paciente.getTotalFaltas();
        int icc = paciente.getIcc();

        ClassificacaoPacienteEnum classificacao =
                ClassificacaoPacienteEnum.valueOf(paciente.getClassificacao());

        return switch (classificacao) {
            case MUITO_CONFIAVEL ->
                    String.format(
                            "Histórico consistente: %d comparecimentos em %d agendamentos.",
                            comparecimentos, agendamentos
                    );
            case CONFIAVEL ->
                    String.format(
                            "Bom padrão de comparecimento: %d de %d consultas.",
                            comparecimentos, agendamentos
                    );
            case COMPARECIMENTO_PROVAVEL ->
                    String.format(
                            "Boa chance de comparecimento com %d presenças anteriores.",
                            comparecimentos
                    );
            case COMPARECIMENTO_INCERTO ->
                    String.format(
                            "Histórico irregular: %d presenças e %d faltas.",
                            comparecimentos, faltas
                    );
            case BAIXA_PROBABILIDADE_DE_COMPARECIMENTO ->
                    String.format(
                            "Ocorrência relevante de faltas (%d).",
                            faltas
                    );
            case PROVAVEL_NAO_COMPARECIMENTO ->
                    "Histórico recente indica dificuldade recorrente de comparecimento.";
            case CRITICO ->
                    String.format(
                            "ICC %d indica baixa previsibilidade de comparecimento.",
                            icc
                    );
            case REALOCACAO_POSSIVEL ->
                    String.format(
                            "ICC %d sugere possível ajuste preventivo de agenda.",
                            icc
                    );
            case REALOCACAO_IMEDIATA ->
                    String.format(
                            "ICC %d indica necessidade de realocação imediata.",
                            icc
                    );
        };
    }

    private HistoricoDomain buildHistorico(EventoAgendamentoMessageDomain evento) {
        return new HistoricoDomain(
                evento.getCns(),
                evento.getIdAgendamento(),
                evento.getStatusConsulta().name(),
                evento.getStatusNotificacao().name(),
                evento.getDataEvento()
        );
    }
}