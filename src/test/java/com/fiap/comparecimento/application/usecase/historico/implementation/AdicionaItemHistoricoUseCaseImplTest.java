package com.fiap.comparecimento.application.usecase.historico.implementation;

import com.fiap.comparecimento.application.gateway.HistoricoGateway;
import com.fiap.comparecimento.domain.model.HistoricoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdicionaItemHistoricoUseCaseImplTest {

    @Mock
    private HistoricoGateway historicoGateway;

    @InjectMocks
    private AdicionaItemHistoricoUseCaseImpl useCase;

    @Test
    void deveAdicionarItemHistorico() {
        HistoricoDomain historico = new HistoricoDomain(
                "123456789012345",
                1L,
                "REALIZADO",
                "ENTREGUE",
                OffsetDateTime.now()
        );

        useCase.adicionaItemHistorico(historico);

        ArgumentCaptor<HistoricoDomain> captor = ArgumentCaptor.forClass(HistoricoDomain.class);
        verify(historicoGateway).adiciona(captor.capture());
        assertEquals("123456789012345", captor.getValue().getCns());
        assertEquals(1L, captor.getValue().getIdAgendamento());
        assertEquals("REALIZADO", captor.getValue().getStatusConsulta());
        assertEquals("ENTREGUE", captor.getValue().getStatusNotificacao());
    }
}
