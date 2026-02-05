package com.fiap.comparecimento.domain.model;

import java.time.OffsetDateTime;

public class PacienteDomain {

    private Long id;
    private String cns;
    private Integer icc;
    private String classificacao;
    private Integer totalComparecimentos;
    private Integer totalFaltas;
    private Integer totalConfirmacoes;
    private OffsetDateTime ultimaAtualizacao;

    public PacienteDomain() {
    }

    public PacienteDomain(Long id, String cns, Integer icc, String classificacao, Integer totalComparecimentos, Integer totalFaltas, Integer totalConfirmacoes, OffsetDateTime ultimaAtualizacao) {
        this.id = id;
        this.cns = cns;
        this.icc = icc;
        this.classificacao = classificacao;
        this.totalComparecimentos = totalComparecimentos;
        this.totalFaltas = totalFaltas;
        this.totalConfirmacoes = totalConfirmacoes;
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCns() {
        return cns;
    }

    public void setCns(String cns) {
        this.cns = cns;
    }

    public Integer getIcc() {
        return icc;
    }

    public void setIcc(Integer icc) {
        this.icc = icc;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public Integer getTotalComparecimentos() {
        return totalComparecimentos;
    }

    public void setTotalComparecimentos(Integer totalComparecimentos) {
        this.totalComparecimentos = totalComparecimentos;
    }

    public Integer getTotalFaltas() {
        return totalFaltas;
    }

    public void setTotalFaltas(Integer totalFaltas) {
        this.totalFaltas = totalFaltas;
    }

    public Integer getTotalConfirmacoes() {
        return totalConfirmacoes;
    }

    public void setTotalConfirmacoes(Integer totalConfirmacoes) {
        this.totalConfirmacoes = totalConfirmacoes;
    }

    public OffsetDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(OffsetDateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
}
