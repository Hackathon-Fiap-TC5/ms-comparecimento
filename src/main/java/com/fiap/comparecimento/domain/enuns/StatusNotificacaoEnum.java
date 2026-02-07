package com.fiap.comparecimento.domain.enuns;

public enum StatusNotificacaoEnum {

    NAO_ENVIADA(-2.0),
    ENVIADA(1.0),
    ENTREGUE(2.0),
    CONFIRMOU_48H_ANTECEDENCIA(5.0),
    CONFIRMOU_24H_ANTECEDENCIA(3.0),
    FALHA(-2.0),
    EXPIRADA(-1.0);

    private final double peso;

    StatusNotificacaoEnum(double peso) {
        this.peso = peso;
    }

    public double getPeso() {
        return peso;
    }
}
