package com.fiap.comparecimento.domain.enuns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.fiap.comparecimento.domain.exception.StatusConsultaNotFoundException;

class StatusConsultaEnumTest {

    @Test
    void deveRetornarStatusConsultaPorStringValida() {
        // When & Then
        assertEquals(StatusConsultaEnum.AGENDADO, StatusConsultaEnum.fromString("AGENDADO"));
        assertEquals(StatusConsultaEnum.CONFIRMADO, StatusConsultaEnum.fromString("CONFIRMADO"));
        assertEquals(StatusConsultaEnum.CANCELADO, StatusConsultaEnum.fromString("CANCELADO"));
        assertEquals(StatusConsultaEnum.FALTA, StatusConsultaEnum.fromString("FALTA"));
        assertEquals(StatusConsultaEnum.REALIZADO, StatusConsultaEnum.fromString("REALIZADO"));
    }

    @Test
    void deveRetornarStatusConsultaCaseInsensitive() {
        // When & Then
        assertEquals(StatusConsultaEnum.AGENDADO, StatusConsultaEnum.fromString("agendado"));
        assertEquals(StatusConsultaEnum.CONFIRMADO, StatusConsultaEnum.fromString("confirmado"));
        assertEquals(StatusConsultaEnum.REALIZADO, StatusConsultaEnum.fromString("realizado"));
    }

    @Test
    void deveRetornarNullQuandoStringNula() {
        // When & Then
        assertNull(StatusConsultaEnum.fromString(null));
    }

    @Test
    void deveLancarExcecaoQuandoStatusInvalido() {
        // When & Then
        assertThrows(StatusConsultaNotFoundException.class, () -> {
            StatusConsultaEnum.fromString("INVALIDO");
        });
    }

    @Test
    void deveRetornarPesoCorretoParaCadaStatus() {
        // When & Then
        assertEquals(1.0, StatusConsultaEnum.AGENDADO.getPeso());
        assertEquals(5.0, StatusConsultaEnum.CONFIRMADO.getPeso());
        assertEquals(3.0, StatusConsultaEnum.CANCELADO.getPeso());
        assertEquals(-7.0, StatusConsultaEnum.FALTA.getPeso());
        assertEquals(7.0, StatusConsultaEnum.REALIZADO.getPeso());
    }
}
