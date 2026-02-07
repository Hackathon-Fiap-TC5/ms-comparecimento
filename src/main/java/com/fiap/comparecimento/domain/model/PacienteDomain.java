package com.fiap.comparecimento.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class PacienteDomain {

    private String cns;
    private Integer icc;
    private String classificacao;
    private Integer totalComparecimentos;
    private Integer totalFaltas;
    private Integer totalConfirmacoes;
    private Integer totalAgendamentos;
    private OffsetDateTime ultimaAtualizacao;

    public PacienteDomain() {
    }

    public PacienteDomain(String cns, Integer icc, String classificacao, Integer totalComparecimentos, Integer totalFaltas, Integer totalConfirmacoes, Integer totalAgendamentos, OffsetDateTime ultimaAtualizacao) {
        this.cns = cns;
        this.icc = icc;
        this.classificacao = classificacao;
        this.totalComparecimentos = totalComparecimentos;
        this.totalFaltas = totalFaltas;
        this.totalConfirmacoes = totalConfirmacoes;
        this.totalAgendamentos = totalAgendamentos;
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
}
