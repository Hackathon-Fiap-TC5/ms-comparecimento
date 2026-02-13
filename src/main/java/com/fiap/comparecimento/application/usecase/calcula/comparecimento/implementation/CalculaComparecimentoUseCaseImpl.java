package com.fiap.comparecimento.application.usecase.calcula.comparecimento.implementation;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.CalculaComparecimentoUseCase;
import com.fiap.comparecimento.domain.enuns.ClassificacaoPacienteEnum;
import com.fiap.comparecimento.domain.enuns.StatusConsultaEnum;
import com.fiap.comparecimento.domain.enuns.StatusNotificacaoEnum;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.domain.model.PacienteDomain;

import java.time.OffsetDateTime;

public class CalculaComparecimentoUseCaseImpl implements CalculaComparecimentoUseCase {

    private final PacienteGateway pacienteGateway;

    public CalculaComparecimentoUseCaseImpl(PacienteGateway pacienteGateway) {
        this.pacienteGateway = pacienteGateway;
    }

    @Override
    public void calculaComparecimento(PacienteDomain paciente, EventoAgendamentoMessageDomain evento) {

        boolean primeiroAgendamento = paciente.getTotalAgendamentos() == 0;

        AtualizacaoPaciente atualizacao = calcularAtualizacao(
                paciente,
                evento,
                primeiroAgendamento
        );

        aplicarAtualizacao(paciente, atualizacao);
        pacienteGateway.criaOuAtualizarInformacoesPaciente(paciente);
    }

    private AtualizacaoPaciente calcularAtualizacao(
            PacienteDomain paciente,
            EventoAgendamentoMessageDomain evento,
            boolean primeiroAgendamento
    ) {

        Contadores contadores = atualizarContadores(paciente, evento.getStatusConsulta());

        int icc;
        ClassificacaoPacienteEnum classificacao;

        if (primeiroAgendamento) {
            icc = paciente.getIcc();
            classificacao = ClassificacaoPacienteEnum.MUITO_CONFIAVEL;
        } else {
            icc = calcularIcc(paciente, evento);
            classificacao = classificarIcc(icc);
        }

        return new AtualizacaoPaciente(
                icc,
                classificacao,
                contadores,
                OffsetDateTime.now()
        );
    }

    private Contadores atualizarContadores(PacienteDomain paciente, StatusConsultaEnum status) {

        int agendamentos = paciente.getTotalAgendamentos();
        int comparecimentos = paciente.getTotalComparecimentos();
        int faltas = paciente.getTotalFaltas();
        int confirmacoes = paciente.getTotalConfirmacoes();
        int cancelamentos = paciente.getTotalCancelamentos();

        switch (status) {
            case AGENDADO -> agendamentos++;
            case REALIZADO -> comparecimentos++;
            case FALTA -> faltas++;
            case CONFIRMADO -> confirmacoes++;
            case CANCELADO -> cancelamentos++;
        }

        return new Contadores(
                agendamentos,
                comparecimentos,
                faltas,
                confirmacoes,
                cancelamentos
        );
    }

    private void aplicarAtualizacao(PacienteDomain paciente, AtualizacaoPaciente atualizacao) {

        paciente.setIcc(atualizacao.icc());
        paciente.setClassificacao(atualizacao.classificacao().name());
        paciente.setTotalAgendamentos(atualizacao.contadores().agendamentos());
        paciente.setTotalComparecimentos(atualizacao.contadores().comparecimentos());
        paciente.setTotalFaltas(atualizacao.contadores().faltas());
        paciente.setTotalConfirmacoes(atualizacao.contadores().confirmacoes());
        paciente.setTotalCancelamentos(atualizacao.contadores().cancelamentos());
        paciente.setUltimaAtualizacao(atualizacao.atualizacao());
    }

    private int calcularIcc(PacienteDomain paciente, EventoAgendamentoMessageDomain evento) {

        double totalAg = Math.max(1, paciente.getTotalAgendamentos());
        double totalComp = paciente.getTotalComparecimentos();
        double totalFaltas = paciente.getTotalFaltas();
        double totalConf = paciente.getTotalConfirmacoes();

        double taxaComparecimento = (totalComp + 1.0) / (totalAg + 2.0);
        double taxaConfirmacao = (totalConf + 1.0) / (totalAg + 2.0);
        double taxaFalta = (totalFaltas + 0.5) / (totalAg + 1.5);

        double scoreHistorico = calcularScoreHistorico(
                taxaComparecimento,
                taxaConfirmacao,
                taxaFalta
        );

        double maturidade = calcularMaturidade(totalAg);
        double scoreEvento = calcularScoreEvento(evento);

        double iccBruto =
                (scoreHistorico * maturidade) +
                        (scoreEvento * pesoEvento(maturidade));

        return normalizarIcc(iccBruto);
    }

    private double calcularScoreHistorico(
            double taxaComparecimento,
            double taxaConfirmacao,
            double taxaFalta
    ) {

        double scoreComparecimento =
                12.0 / (1.0 + Math.exp(-6.0 * (taxaComparecimento - 0.6)));

        double scoreConfirmacao =
                6.0 / (1.0 + Math.exp(-5.0 * (taxaConfirmacao - 0.5)));

        double penalidadeFaltas =
                10.0 * Math.pow(taxaFalta, 1.6);

        return scoreComparecimento + scoreConfirmacao - penalidadeFaltas;
    }

    private double calcularMaturidade(double totalAgendamentos) {
        return Math.min(1.0, Math.log(totalAgendamentos + 1) / Math.log(20));
    }

    private double pesoEvento(double maturidade) {
        return Math.max(0.05, 1.0 - maturidade);
    }

    private int normalizarIcc(double iccBruto) {
        double iccNormalizado =
                100.0 / (1.0 + Math.exp(-iccBruto / 3.2));
        return (int) Math.round(iccNormalizado);
    }

    private ClassificacaoPacienteEnum classificarIcc(int icc) {
        if (icc >= 90) return ClassificacaoPacienteEnum.MUITO_CONFIAVEL;
        if (icc >= 80) return ClassificacaoPacienteEnum.CONFIAVEL;
        if (icc >= 70) return ClassificacaoPacienteEnum.COMPARECIMENTO_PROVAVEL;
        if (icc >= 60) return ClassificacaoPacienteEnum.COMPARECIMENTO_INCERTO;
        if (icc >= 50) return ClassificacaoPacienteEnum.BAIXA_PROBABILIDADE_DE_COMPARECIMENTO;
        if (icc >= 40) return ClassificacaoPacienteEnum.PROVAVEL_NAO_COMPARECIMENTO;
        if (icc >= 30) return ClassificacaoPacienteEnum.CRITICO;
        if (icc >= 20) return ClassificacaoPacienteEnum.REALOCACAO_POSSIVEL;
        return ClassificacaoPacienteEnum.REALOCACAO_IMEDIATA;
    }

    private double calcularScoreEvento(EventoAgendamentoMessageDomain evento) {
        if (evento == null) return 0.0;

        return scorePorStatus(evento.getStatusConsulta()) +
                scorePorNotificacao(
                        evento.getStatusConsulta(),
                        evento.getStatusNotificacao()
                );
    }

    private double scorePorStatus(StatusConsultaEnum status) {
        return switch (status) {
            case AGENDADO, CANCELADO -> 0.5;
            case CONFIRMADO -> 1.5;
            case REALIZADO -> 3.0;
            case FALTA -> -4.0;
        };
    }

    private double scorePorNotificacao(
            StatusConsultaEnum status,
            StatusNotificacaoEnum notificacao
    ) {

        return switch (notificacao) {
            case ENVIADA ->
                    status == StatusConsultaEnum.AGENDADO ? 2.0 :
                            status == StatusConsultaEnum.FALTA ? -1.0 : 0.0;

            case ENTREGUE ->
                    status == StatusConsultaEnum.REALIZADO ? 2.5 :
                            status == StatusConsultaEnum.FALTA ? -1.5 : 0.0;

            case CONFIRMOU_48H_ANTECEDENCIA ->
                    status == StatusConsultaEnum.CONFIRMADO ? 3.0 :
                            status == StatusConsultaEnum.CANCELADO ? 1.5 :
                                    status == StatusConsultaEnum.REALIZADO ? 4.0 :
                                            status == StatusConsultaEnum.FALTA ? -2.0 : 0.0;

            case CONFIRMOU_24H_ANTECEDENCIA ->
                    status == StatusConsultaEnum.CONFIRMADO ? 2.3 :
                            status == StatusConsultaEnum.CANCELADO ? 0.9 :
                                    status == StatusConsultaEnum.REALIZADO ? 3.3 :
                                            status == StatusConsultaEnum.FALTA ? -1.3 : 0.0;

            case EXPIRADA ->
                    status == StatusConsultaEnum.FALTA ? -5.5 :
                            status == StatusConsultaEnum.REALIZADO ? 1.0 : 0.0;

            case FALHA ->
                    status == StatusConsultaEnum.REALIZADO ? 3.5 :
                            status == StatusConsultaEnum.FALTA ? -2.5 : 0.0;

            default -> 0.0;
        };
    }

    private record AtualizacaoPaciente(
            int icc,
            ClassificacaoPacienteEnum classificacao,
            Contadores contadores,
            OffsetDateTime atualizacao
    ) {}

    private record Contadores(
            int agendamentos,
            int comparecimentos,
            int faltas,
            int confirmacoes,
            int cancelamentos
    ) {}
}
