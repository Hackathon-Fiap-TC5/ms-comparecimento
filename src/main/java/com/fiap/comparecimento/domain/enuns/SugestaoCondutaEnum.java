package com.fiap.comparecimento.domain.enuns;

public enum SugestaoCondutaEnum {

    MANTER_FLUXO(1, "Manter fluxo padrão"),
    MONITORAR(2, "Monitorar comparecimento"),
    CONFIRMAR_AGENDAMENTO(3, "Confirmar agendamento"),
    REAGENDAMENTO_PREVENTIVO(4, "Reagendamento preventivo"),
    ALOCACAO_ALTERNATIVA(5, "Alocação alternativa"),
    REALOCACAO_IMEDIATA(6, "Realocação imediata");

    private final Integer id;
    private final String descricao;

    SugestaoCondutaEnum(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}