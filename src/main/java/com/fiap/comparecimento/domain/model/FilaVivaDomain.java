package com.fiap.comparecimento.domain.model;

public class FilaVivaDomain {

    private Long idAgendamento;
    private String cns;
    private String sugestaoConduta;
    private String perfilPaciente;
    private String justificativa;

    public FilaVivaDomain() {
    }

    public FilaVivaDomain(Long idAgendamento, String cns, String sugestaoConduta, String perfilPaciente, String justificativa) {
        this.idAgendamento = idAgendamento;
        this.cns = cns;
        this.sugestaoConduta = sugestaoConduta;
        this.perfilPaciente = perfilPaciente;
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

    public String getPerfilPaciente() {
        return perfilPaciente;
    }

    public void setPerfilPaciente(String perfilPaciente) {
        this.perfilPaciente = perfilPaciente;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
