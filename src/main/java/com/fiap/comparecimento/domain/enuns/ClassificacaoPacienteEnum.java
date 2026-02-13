package com.fiap.comparecimento.domain.enuns;

public enum ClassificacaoPacienteEnum {

    MUITO_CONFIAVEL(0, "Muito confiável"),
    CONFIAVEL(1, "Confiável"),
    COMPARECIMENTO_PROVAVEL(2, "Comparecimento provável"),
    COMPARECIMENTO_INCERTO(3, "Comparecimento incerto"),
    BAIXA_PROBABILIDADE_DE_COMPARECIMENTO(4, "Baixa probabilidade de comparecimento"),
    PROVAVEL_NAO_COMPARECIMENTO(5, "Provável não comparecimento"),
    CRITICO(6, "Crítico"),
    REALOCACAO_POSSIVEL(7, "Realocação possível"),
    REALOCACAO_IMEDIATA(8, "Realocação imediata");

    private final int codigo;
    private final String descricao;

    ClassificacaoPacienteEnum(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}