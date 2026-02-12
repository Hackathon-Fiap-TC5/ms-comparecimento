package com.fiap.comparecimento.application.usecase.calcula.comparecimento.implementation;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.CalculaComparecimentoUseCase;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.ProcessarComparecimentoUseCase;
import com.fiap.comparecimento.application.usecase.historico.AdicionaItemHistoricoUseCase;
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

    public ProcessarComparecimentoUseCaseImpl(PacienteGateway pacienteGateway,
                                              AdicionaItemHistoricoUseCase adicionaItemHistoricoUseCase,
                                              CalculaComparecimentoUseCase calculaComparecimentoUseCase,
                                              ComparecimentoProducer comparecimentoProducer) {
        this.pacienteGateway = pacienteGateway;
        this.adicionaItemHistoricoUseCase = adicionaItemHistoricoUseCase;
        this.calculaComparecimentoUseCase = calculaComparecimentoUseCase;
        this.comparecimentoProducer = comparecimentoProducer;
    }

    @Override
    public void processaComparecimento(EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain) {

        if(pacienteGateway.verificaExistenciaPaciente(eventoAgendamentoMessageDomain.getCns()).isEmpty()){
            PacienteDomain pacienteDomain = new PacienteDomain(eventoAgendamentoMessageDomain.getCns(), 100,
                    ClassificacaoPacienteEnum.MUITO_CONFIAVEL.toString(),
                    0,0,0,0,0 ,
                    OffsetDateTime.now()
            );
            pacienteGateway.criaOuAtualizarInformacoesPaciente(pacienteDomain);
        }

        PacienteDomain pacienteDomain = pacienteGateway.consultar(eventoAgendamentoMessageDomain.getCns());
        calculaComparecimentoUseCase.calculaComparecimento(pacienteDomain, eventoAgendamentoMessageDomain);
        adicionaItemHistoricoUseCase.adicionaItemHistorico(buildHistorico(eventoAgendamentoMessageDomain));
        comparecimentoProducer.sendSugestions(toBuildPayloadComparecimento(eventoAgendamentoMessageDomain.getIdAgendamento(), pacienteDomain.getCns()));
    }

    private EventoComparecimentoMessageDto toBuildPayloadComparecimento(Long idAgendamento, String cns) {
        PacienteDomain paciente = pacienteGateway.consultar(cns);
        ClassificacaoPacienteEnum classificacaoPaciente = ClassificacaoPacienteEnum.valueOf(paciente.getClassificacao());

        SugestaoCondutaEnum sugestaoConduta = sugerirConduta(classificacaoPaciente);

        EventoComparecimentoMessageDomain evento = new EventoComparecimentoMessageDomain(idAgendamento, cns, sugestaoConduta.name(),
                paciente.getIcc(), justificativaConduta(paciente));

        return ComparecimentoProducerMapper.INSTANCE.toEventoComparecimentoMessageDto(evento);
    }


    private SugestaoCondutaEnum sugerirConduta(ClassificacaoPacienteEnum classificacao) {
        return switch (classificacao) {
            case MUITO_CONFIAVEL -> SugestaoCondutaEnum.MANTER_FLUXO;
            case CONFIAVEL, COMPARECIMENTO_PROVAVEL -> SugestaoCondutaEnum.MONITORAR;
            case COMPARECIMENTO_INCERTO -> SugestaoCondutaEnum.CONFIRMAR_AGENDAMENTO;
            case BAIXA_PROBABILIDADE_DE_COMPARECIMENTO -> SugestaoCondutaEnum.REAGENDAMENTO_PREVENTIVO;
            case PROVAVEL_NAO_COMPARECIMENTO, CRITICO, REALOCACAO_POSSIVEL -> SugestaoCondutaEnum.ALOCACAO_ALTERNATIVA;
            case REALOCACAO_IMEDIATA -> SugestaoCondutaEnum.REALOCACAO_IMEDIATA;
        };
    }


    private String justificativaConduta(PacienteDomain paciente) {

        int agendamentos = paciente.getTotalAgendamentos();
        int comparecimentos = paciente.getTotalComparecimentos();
        int faltas = paciente.getTotalFaltas();
        int icc = paciente.getIcc();

        ClassificacaoPacienteEnum classificacao = ClassificacaoPacienteEnum.valueOf(paciente.getClassificacao());
        return switch (classificacao) {
            case MUITO_CONFIAVEL ->
                    String.format("O histórico do paciente indica comparecimento consistente em %d de %d agendamentos, demonstrando alta previsibilidade para manter o horário.",
                            comparecimentos, agendamentos);

            case CONFIAVEL ->
                    String.format("Na maior parte dos agendamentos (%d de %d), o paciente conseguiu comparecer, indicando um padrão estável ao longo do tempo.",
                            comparecimentos, agendamentos);

            case COMPARECIMENTO_PROVAVEL ->
                    String.format("O paciente costuma comparecer na maioria das vezes (%d presenças), sugerindo boa chance de comparecimento.",
                            comparecimentos);

            case COMPARECIMENTO_INCERTO ->
                    String.format("O histórico mostra alternância entre presenças (%d) e ausências (%d), o que indica necessidade de atenção no momento do agendamento.",
                            comparecimentos, faltas);

            case BAIXA_PROBABILIDADE_DE_COMPARECIMENTO ->
                    String.format("Em parte dos agendamentos anteriores, o paciente teve dificuldade para comparecer (%d ausências), indicando menor previsibilidade do horário.",
                            faltas);

            case PROVAVEL_NAO_COMPARECIMENTO -> "O histórico recente aponta dificuldade recorrente de comparecimento, o que pode impactar a ocupação do horário agendado.";

            case CRITICO ->
                    String.format("O histórico recente mostra dificuldade em manter regularidade de comparecimento. O ICC %d indica baixa previsibilidade, sugerindo acompanhamento e possíveis ajustes no agendamento.",
                            icc);

            case REALOCACAO_POSSIVEL ->
                    String.format("Com base no histórico recente e no ICC %d, pode ser avaliado um ajuste preventivo da agenda, buscando melhor adaptação do horário ao contexto do paciente.",
                            icc);

            case REALOCACAO_IMEDIATA ->
                    String.format("Considerando o histórico recente e o ICC %d, recomenda-se ajuste imediato da alocação do horário, visando reduzir impactos na agenda e apoiar o cuidado.",
                            icc);
        };
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
