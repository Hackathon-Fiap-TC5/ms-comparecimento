package com.fiap.comparecimento.domain.enuns;

import com.fiap.comparecimento.domain.exception.StatusConsultaNotFoundException;

import java.util.Arrays;

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

    public static StatusConsultaEnum fromString(String value) {
        if (value == null) return null;

        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new StatusConsultaNotFoundException(value)
                );
    }
}
