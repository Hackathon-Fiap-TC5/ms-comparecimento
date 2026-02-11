package com.fiap.comparecimento.domain.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistoricoDomainTest {

    @Test
    void deveCriarHistoricoComConstrutor() {
        OffsetDateTime now = OffsetDateTime.now();
        HistoricoDomain historico = new HistoricoDomain(
                "123456789012345",
                1L,
                "REALIZADO",
                "ENTREGUE",
                now
        );

        assertEquals("123456789012345", historico.getCns());
        assertEquals(1L, historico.getIdAgendamento());
        assertEquals("REALIZADO", historico.getStatusConsulta());
        assertEquals("ENTREGUE", historico.getStatusNotificacao());
        assertEquals(now, historico.getDataEvento());
    }

    @Test
    void devePermitirSetters() {
        HistoricoDomain historico = new HistoricoDomain("cns", 1L, "FALTA", "EXPIRADA", OffsetDateTime.now());

        historico.setId(10L);
        historico.setCns("new");
        historico.setIdAgendamento(2L);
        historico.setStatusConsulta("CANCELADO");
        historico.setStatusNotificacao("FALHA");

        assertEquals(10L, historico.getId());
        assertEquals("new", historico.getCns());
        assertEquals(2L, historico.getIdAgendamento());
        assertEquals("CANCELADO", historico.getStatusConsulta());
        assertEquals("FALHA", historico.getStatusNotificacao());
    }
}
