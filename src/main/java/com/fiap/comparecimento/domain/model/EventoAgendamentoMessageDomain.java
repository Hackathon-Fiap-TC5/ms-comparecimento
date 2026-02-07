package com.fiap.comparecimento.domain.model;

import com.fiap.comparecimento.domain.enuns.StatusConsultaEnum;
import com.fiap.comparecimento.domain.enuns.StatusNotificacaoEnum;

public class EventoAgendamentoMessageDomain {

    private String cns;
    private StatusConsultaEnum statusConsulta;
    private StatusNotificacaoEnum statusNotificacao;

    public EventoAgendamentoMessageDomain() {
    }

    public EventoAgendamentoMessageDomain(String cns, StatusConsultaEnum statusConsulta, StatusNotificacaoEnum statusNotificacao) {
        this.cns = cns;
        this.statusConsulta = statusConsulta;
        this.statusNotificacao = statusNotificacao;
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
}