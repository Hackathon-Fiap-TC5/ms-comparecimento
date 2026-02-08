package com.fiap.comparecimento.domain.model;

import com.fiap.comparecimento.domain.enuns.StatusConsultaEnum;
import com.fiap.comparecimento.domain.enuns.StatusNotificacaoEnum;

import java.time.OffsetDateTime;

public class EventoAgendamentoMessageDomain {

    private Long idAgendamento;
    private String cns;
    private StatusConsultaEnum statusConsulta;
    private StatusNotificacaoEnum statusNotificacao;
    private OffsetDateTime dataEvento;

    public EventoAgendamentoMessageDomain() {
    }

    public EventoAgendamentoMessageDomain(Long idAgendamento, String cns, StatusConsultaEnum statusConsulta,
                                          StatusNotificacaoEnum statusNotificacao, OffsetDateTime dataEvento) {
        this.idAgendamento = idAgendamento;
        this.cns = cns;
        this.statusConsulta = statusConsulta;
        this.statusNotificacao = statusNotificacao;
        this.dataEvento = dataEvento;
    }

    public Long getIdAgendamento() {
        return idAgendamento;
    }

    public void setIdAgendamento(Long idAgendamento) {
        this.idAgendamento = idAgendamento;
    }

    public String getCns() {
        return cns;
    }

    public void setCns(String cns) {
        this.cns = cns;
    }

    public StatusConsultaEnum getStatusConsulta() {
        return statusConsulta;
    }

    public void setStatusConsulta(StatusConsultaEnum statusConsulta) {
        this.statusConsulta = statusConsulta;
    }

    public StatusNotificacaoEnum getStatusNotificacao() {
        return statusNotificacao;
    }

    public void setStatusNotificacao(StatusNotificacaoEnum statusNotificacao) {
        this.statusNotificacao = statusNotificacao;
    }

    public OffsetDateTime getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(OffsetDateTime dataEvento) {
        this.dataEvento = dataEvento;
    }
}