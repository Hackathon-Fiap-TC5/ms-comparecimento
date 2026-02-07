package com.fiap.comparecimento.domain.enuns;

public enum StatusConsultaEnum {

    AGENDADO(1.0),
    CONFIRMADO(5.0),
    CANCELADO(3.0),
    FALTA(-7.0),
    REALIZADO(7.0);

    private final double peso;

    StatusConsultaEnum(double peso) {
        this.peso = peso;
    }

    public double getPeso() {
        return peso;
    }
}
