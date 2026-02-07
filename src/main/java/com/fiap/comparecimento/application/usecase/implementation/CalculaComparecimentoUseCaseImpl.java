package com.fiap.comparecimento.application.usecase.implementation;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.CalculaComparecimentoUseCase;
import com.fiap.comparecimento.domain.enuns.ClassificacaoPacienteEnum;
import com.fiap.comparecimento.domain.enuns.StatusConsultaEnum;
import com.fiap.comparecimento.domain.enuns.StatusNotificacaoEnum;
import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.entrypoint.controllers.dto.AgendamentoDTO;

import java.time.OffsetDateTime;

public class CalculaComparecimentoUseCaseImpl implements CalculaComparecimentoUseCase {

    private final PacienteGateway pacienteGateway;

    public CalculaComparecimentoUseCaseImpl(PacienteGateway pacienteGateway) {
        this.pacienteGateway = pacienteGateway;
    }

    @Override
    public void calculaComparecimento(AgendamentoDTO infos) {
        PacienteDomain p = pacienteGateway.consultar(infos.getCns());
        run(p, infos);
    }

    /*
    * Atualiza as infos do paciente apos calculo e classificação de comparecimento
    * */
    private void run(PacienteDomain p, AgendamentoDTO info){
        int totalComparecimentos = p.getTotalComparecimentos();
        int totalFaltas = p.getTotalFaltas();
        int totalConfirmacoes = p.getTotalConfirmacoes();
        int totalAgendamentos = p.getTotalAgendamentos();

        StatusConsultaEnum status = StatusConsultaEnum.valueOf(info.getStatusConsulta());
        switch (status){
            case AGENDADO : totalAgendamentos += 1;
            case REALIZADO: totalComparecimentos += 1;
            case FALTA: totalFaltas += 1;
            case CONFIRMADO: totalConfirmacoes += 1;
        }

        int icc = calculaICC(p, info);

        p.setIcc(icc);
        p.setClassificacao(classificaPaciente(icc).toString());
        p.setTotalComparecimentos(totalComparecimentos);
        p.setTotalFaltas(totalFaltas);
        p.setTotalConfirmacoes(totalConfirmacoes);
        p.setTotalAgendamentos(totalAgendamentos);
        p.setUltimaAtualizacao(OffsetDateTime.now());
        pacienteGateway.save(p);
    }

    /*
    * Classifica o agendamento e sugere uma acao
    * */
    private ClassificacaoPacienteEnum classificaPaciente(int icc) {
        if (icc >= 85) return ClassificacaoPacienteEnum.MUITO_CONFIAVEL;
        if (icc >= 75) return ClassificacaoPacienteEnum.CONFIAVEL;
        if (icc >= 60) return ClassificacaoPacienteEnum.ATENCAO;
        if (icc >= 45) return ClassificacaoPacienteEnum.RISCO;
        if (icc >= 30) return ClassificacaoPacienteEnum.ALTO_RISCO;
        if (icc >= 20) return ClassificacaoPacienteEnum.FALTA_PROVAVEL;
        if (icc >= 15) return ClassificacaoPacienteEnum.MODO_REALOCACAO;
        return ClassificacaoPacienteEnum.REALOQUE;
    }

    /*
    * Calcula o ICC com base nos dados do paciente e sobre o evento de agendamento
    * */
    private Integer calculaICC(PacienteDomain p, AgendamentoDTO dto){

        //Dados base para calculo
        double totalAg = Math.max(1, p.getTotalAgendamentos());
        double totalComp = p.getTotalComparecimentos();
        double totalFalta = p.getTotalFaltas();
        double totalConf = p.getTotalConfirmacoes();

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
        double scoreEvento = calculaScoreEvento(dto);
        double pesoEvento = Math.max(0.05, 1.0 - maturidade);

        //ICC bruto estabilizado
        double iccBruto = (scoreHistorico * maturidade) + (scoreEvento * pesoEvento);

        double iccNormalizado = 100.0 / (1.0 + Math.exp(-iccBruto / 3.2));
        return (int) Math.round(iccNormalizado);
    }

    /*
    * Calcula score sobre comportamento realizado entorno de toda a vida de um agendamento
    * */
    private double calculaScoreEvento(AgendamentoDTO dto){
        if (dto == null) return 0.0;

        StatusConsultaEnum status = StatusConsultaEnum.valueOf(dto.getStatusConsulta());
        StatusNotificacaoEnum notificacao = StatusNotificacaoEnum.valueOf(dto.getStatusNotificacao());
        double score = 0.0;

        // Peso status da consulta
        switch (status) {
            case AGENDADO -> score += 0.5;
            case CONFIRMADO -> score += 1.5;
            case CANCELADO -> score += 0.5;
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
