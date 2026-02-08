package com.fiap.comparecimento.domain.enuns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.fiap.comparecimento.domain.exception.StatusNotificacaoNotFoundException;

class StatusNotificacaoEnumTest {

    @Test
    void deveRetornarStatusNotificacaoPorStringValida() {
        // When & Then
        assertEquals(StatusNotificacaoEnum.NAO_ENVIADA, StatusNotificacaoEnum.fromString("NAO_ENVIADA"));
        assertEquals(StatusNotificacaoEnum.ENVIADA, StatusNotificacaoEnum.fromString("ENVIADA"));
        assertEquals(StatusNotificacaoEnum.ENTREGUE, StatusNotificacaoEnum.fromString("ENTREGUE"));
        assertEquals(StatusNotificacaoEnum.CONFIRMOU_48H_ANTECEDENCIA, StatusNotificacaoEnum.fromString("CONFIRMOU_48H_ANTECEDENCIA"));
        assertEquals(StatusNotificacaoEnum.CONFIRMOU_24H_ANTECEDENCIA, StatusNotificacaoEnum.fromString("CONFIRMOU_24H_ANTECEDENCIA"));
        assertEquals(StatusNotificacaoEnum.FALHA, StatusNotificacaoEnum.fromString("FALHA"));
        assertEquals(StatusNotificacaoEnum.EXPIRADA, StatusNotificacaoEnum.fromString("EXPIRADA"));
    }

    @Test
    void deveRetornarStatusNotificacaoCaseInsensitive() {
        // When & Then
        assertEquals(StatusNotificacaoEnum.ENVIADA, StatusNotificacaoEnum.fromString("enviada"));
        assertEquals(StatusNotificacaoEnum.ENTREGUE, StatusNotificacaoEnum.fromString("entregue"));
        assertEquals(StatusNotificacaoEnum.FALHA, StatusNotificacaoEnum.fromString("falha"));
    }

    @Test
    void deveRetornarNullQuandoStringNula() {
        // When & Then
        assertNull(StatusNotificacaoEnum.fromString(null));
    }

    @Test
    void deveLancarExcecaoQuandoStatusInvalido() {
        // When & Then
        assertThrows(StatusNotificacaoNotFoundException.class, () -> {
            StatusNotificacaoEnum.fromString("INVALIDO");
        });
    }

    @Test
    void deveRetornarPesoCorretoParaCadaStatus() {
        // When & Then
        assertEquals(-2.0, StatusNotificacaoEnum.NAO_ENVIADA.getPeso());
        assertEquals(1.0, StatusNotificacaoEnum.ENVIADA.getPeso());
        assertEquals(2.0, StatusNotificacaoEnum.ENTREGUE.getPeso());
        assertEquals(5.0, StatusNotificacaoEnum.CONFIRMOU_48H_ANTECEDENCIA.getPeso());
        assertEquals(3.0, StatusNotificacaoEnum.CONFIRMOU_24H_ANTECEDENCIA.getPeso());
        assertEquals(-2.0, StatusNotificacaoEnum.FALHA.getPeso());
        assertEquals(-1.0, StatusNotificacaoEnum.EXPIRADA.getPeso());
    }
}
