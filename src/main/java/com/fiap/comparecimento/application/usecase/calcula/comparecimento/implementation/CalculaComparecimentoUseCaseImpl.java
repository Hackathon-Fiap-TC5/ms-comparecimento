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
    public void calculaComparecimento(PacienteDomain pacienteDomain, EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain) {
        executarFluxoIcc(pacienteDomain, eventoAgendamentoMessageDomain);
    }

    /*
    * Atualiza as infos do paciente apos calculo e classificação de comparecimento
    * */
    private void executarFluxoIcc(PacienteDomain pacienteDomain, EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain){
        int totalComparecimentos = pacienteDomain.getTotalComparecimentos();
        int totalFaltas = pacienteDomain.getTotalFaltas();
        int totalConfirmacoes = pacienteDomain.getTotalConfirmacoes();
        int totalCancelamentos = pacienteDomain.getTotalCancelamentos();
        int totalAgendamentos = pacienteDomain.getTotalAgendamentos();

        StatusConsultaEnum status = eventoAgendamentoMessageDomain.getStatusConsulta();

        switch (status){
            case AGENDADO : totalAgendamentos += 1;
            case REALIZADO: totalComparecimentos += 1;
            case FALTA: totalFaltas += 1;
            case CONFIRMADO: totalConfirmacoes += 1;
            case CANCELADO: totalCancelamentos += 1;
        }

        int icc = calculaICC(pacienteDomain, eventoAgendamentoMessageDomain);

        pacienteDomain.setIcc(icc);
        pacienteDomain.setClassificacao(classificarICC(icc).toString());
        pacienteDomain.setTotalComparecimentos(totalComparecimentos);
        pacienteDomain.setTotalFaltas(totalFaltas);
        pacienteDomain.setTotalConfirmacoes(totalConfirmacoes);
        pacienteDomain.setTotalCancelamentos(totalCancelamentos);
        pacienteDomain.setTotalAgendamentos(totalAgendamentos);
        pacienteDomain.setUltimaAtualizacao(OffsetDateTime.now());
        pacienteGateway.atualizarInformacoesPaciente(pacienteDomain);
    }

    /*
    * Classifica o agendamento e sugere uma acao
    * */
    private ClassificacaoPacienteEnum classificarICC(int icc) {
        if (icc >= 90) {
            return ClassificacaoPacienteEnum.MUITO_CONFIAVEL;
        } else if (icc >= 80) {
            return ClassificacaoPacienteEnum.CONFIAVEL;
        } else if (icc >= 70) {
            return ClassificacaoPacienteEnum.COMPARECIMENTO_PROVAVEL;
        } else if (icc >= 60) {
            return ClassificacaoPacienteEnum.COMPARECIMENTO_INCERTO;
        } else if (icc >= 50) {
            return ClassificacaoPacienteEnum.BAIXA_PROBABILIDADE_DE_COMPARECIMENTO;
        } else if (icc >= 40) {
            return ClassificacaoPacienteEnum.PROVAVEL_NAO_COMPARECIMENTO;
        } else if (icc >= 30) {
            return ClassificacaoPacienteEnum.CRITICO;
        } if (icc >= 20) {
            return ClassificacaoPacienteEnum.REALOCACAO_POSSIVEL;
        } else {
            return ClassificacaoPacienteEnum.REALOCACAO_IMEDIATA;
        }
    }

    /*
    * Calcula o ICC com base nos dados do paciente e sobre o evento de agendamento
    * */
    private Integer calculaICC(PacienteDomain pacienteDomain, EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain){

        //Dados base para calculo
        double totalAg = Math.max(1, pacienteDomain.getTotalAgendamentos());
        double totalComp = pacienteDomain.getTotalComparecimentos();
        double totalFalta = pacienteDomain.getTotalFaltas();
        double totalConf = pacienteDomain.getTotalConfirmacoes();

        //Taxa registradas anteriormente para analise de paciente
        double taxaComparecimento = (totalComp + 1.0) / (totalAg + 2.0);
        double taxaConfirmacao = (totalConf + 1.0) / (totalAg + 2.0);
        double taxaFalta = (totalFalta + 0.5) / (totalAg + 1.5);

        //Score sobre o historico de acoes do paciente
        double scoreComparecimento = 12.0 * (1.0 / (1.0 + Math.exp(-6.0 * (taxaComparecimento - 0.6))));
        double scoreConfirmacao = 6.0 * (1.0 + Math.exp(-5.0 * (taxaConfirmacao - 0.5)));

        // Penalidades
        double penalidadeFaltas = 10.0 * Math.pow(taxaFalta, 1.6);

        //Maturidade paciente
        double maturidade = Math.min(1.0, Math.log(totalAg + 1) / Math.log(20));

        //Comportamento paciente
        double scoreHistorico = scoreComparecimento + scoreConfirmacao - penalidadeFaltas;


        //Evento sobre ação recente do paciente
        double scoreEvento = calculaScoreEvento(eventoAgendamentoMessageDomain);
        double pesoEvento = Math.max(0.05, 1.0 - maturidade);

        //ICC bruto estabilizado
        double iccBruto = (scoreHistorico * maturidade) + (scoreEvento * pesoEvento);

        double iccNormalizado = 100.0 / (1.0 + Math.exp(-iccBruto / 3.2));
        return (int) Math.round(iccNormalizado);
    }

    /*
    * Calcula score sobre comportamento realizado entorno de toda a vida de um agendamento
    * */
    private double calculaScoreEvento(EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain){
        if (eventoAgendamentoMessageDomain == null) return 0.0;

        StatusConsultaEnum status = eventoAgendamentoMessageDomain.getStatusConsulta();
        StatusNotificacaoEnum notificacao = eventoAgendamentoMessageDomain.getStatusNotificacao();
        double score = 0.0;

        // Peso status da consulta
        switch (status) {
            case AGENDADO, CANCELADO -> score += 0.5;
            case CONFIRMADO -> score += 1.5;
            case REALIZADO -> score += 3.0;
            case FALTA -> score -= 4.0;
        }

        //Peso status de notificacao
        switch (notificacao) {
            case ENVIADA -> {
                if (status == StatusConsultaEnum.AGENDADO) score += 2.0;
                if (status == StatusConsultaEnum.FALTA) score -= 1.0;
            }

            case ENTREGUE -> {
                if(status == StatusConsultaEnum.REALIZADO) score += 2.5;
                if(status == StatusConsultaEnum.FALTA) score -= 1.5;
            }

            case CONFIRMOU_48H_ANTECEDENCIA -> {
                if(status == StatusConsultaEnum.CONFIRMADO) score += 3.0;
                if(status == StatusConsultaEnum.CANCELADO) score += 1.5;
                if(status == StatusConsultaEnum.REALIZADO) score += 4.0;
                if(status == StatusConsultaEnum.FALTA) score -= 2.0;
            }

            case CONFIRMOU_24H_ANTECEDENCIA -> {
                if(status == StatusConsultaEnum.CONFIRMADO) score += 2.3;
                if(status == StatusConsultaEnum.CANCELADO) score += 0.9;
                if(status == StatusConsultaEnum.REALIZADO) score += 3.3;
                if(status == StatusConsultaEnum.FALTA) score -= 1.3;
            }

            case EXPIRADA -> {
                if(status == StatusConsultaEnum.FALTA) score -= 5.5;
                if(status == StatusConsultaEnum.REALIZADO) score += 1.0;
            }

            case FALHA -> {
                if(status == StatusConsultaEnum.REALIZADO) score += 3.5;
                if(status == StatusConsultaEnum.FALTA) score -= 2.5;
            }
        }
        return score;
    };
}
