package com.fiap.comparecimento.application.usecase.historico.implementation;

import com.fiap.comparecimento.application.gateway.HistoricoGateway;
import com.fiap.comparecimento.domain.model.HistoricoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaHistoricoUseCaseImplTest {

    @Mock
    private HistoricoGateway historicoGateway;

    @InjectMocks
    private ConsultaHistoricoUseCaseImpl useCase;

    @Test
    void deveRetornarHistoricoQuandoExistir() {
        Long idAgendamento = 1L;
        String cns = "123456789012345";
        List<HistoricoDomain> historicos = List.of(
                new HistoricoDomain(cns, idAgendamento, "REALIZADO", "ENTREGUE", OffsetDateTime.now())
        );

        when(historicoGateway.getHistorico(idAgendamento, cns)).thenReturn(historicos);

        List<HistoricoDomain> result = useCase.getHistorico(idAgendamento, cns);

        assertEquals(1, result.size());
        assertEquals(cns, result.get(0).getCns());
        assertEquals(idAgendamento, result.get(0).getIdAgendamento());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistirHistorico() {
        when(historicoGateway.getHistorico(1L, "123")).thenReturn(List.of());

        List<HistoricoDomain> result = useCase.getHistorico(1L, "123");

        assertEquals(0, result.size());
    }
}
