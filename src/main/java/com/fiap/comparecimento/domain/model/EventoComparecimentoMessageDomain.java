package com.fiap.comparecimento.domain.model;

public class EventoComparecimentoMessageDomain {

    private Long idAgendamento;
    private String cns;
    private String sugestaoConduta;
    private Integer iccScore;
    private String justificativa;

    public EventoComparecimentoMessageDomain() {
    }

    public EventoComparecimentoMessageDomain(Long idAgendamento, String cns, String sugestaoConduta,
                                             Integer iccScore, String justificativa) {
        this.idAgendamento = idAgendamento;
        this.cns = cns;
        this.sugestaoConduta = sugestaoConduta;
        this.iccScore = iccScore;
        this.justificativa = justificativa;
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

    public String getSugestaoConduta() {
        return sugestaoConduta;
    }

    public void setSugestaoConduta(String sugestaoConduta) {
        this.sugestaoConduta = sugestaoConduta;
    }

    public Integer getIccScore() {
        return iccScore;
    }

    public void setIccScore(Integer iccScore) {
        this.iccScore = iccScore;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
