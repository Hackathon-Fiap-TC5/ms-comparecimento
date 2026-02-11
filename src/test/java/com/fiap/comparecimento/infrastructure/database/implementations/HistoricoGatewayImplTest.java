package com.fiap.comparecimento.infrastructure.database.implementations;

import com.fiap.comparecimento.domain.model.HistoricoDomain;
import com.fiap.comparecimento.infrastructure.database.entities.HistoricoEntity;
import com.fiap.comparecimento.infrastructure.database.repositories.HistoricoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricoGatewayImplTest {

    @Mock
    private HistoricoRepository historicoRepository;

    @InjectMocks
    private HistoricoGatewayImpl gateway;

    @Test
    void deveRetornarHistoricoQuandoExistir() {
        Long idAgendamento = 1L;
        String cns = "123456789012345";
        HistoricoEntity entity = new HistoricoEntity();
        entity.setId(1L);
        entity.setIdAgendamento(idAgendamento);
        entity.setCns(cns);
        entity.setStatusConsulta("REALIZADO");
        entity.setStatusNotificacao("ENTREGUE");
        entity.setDataEvento(OffsetDateTime.now());

        when(historicoRepository.findByIdAgendamentoAndCns(idAgendamento, cns))
                .thenReturn(List.of(entity));

        List<HistoricoDomain> result = gateway.getHistorico(idAgendamento, cns);

        assertEquals(1, result.size());
        assertEquals(cns, result.get(0).getCns());
        assertEquals(idAgendamento, result.get(0).getIdAgendamento());
    }

    @Test
    void deveAdicionarHistorico() {
        HistoricoDomain historico = new HistoricoDomain(
                "123456789012345",
                1L,
                "REALIZADO",
                "ENTREGUE",
                OffsetDateTime.now()
        );

        gateway.adiciona(historico);

        verify(historicoRepository).save(org.mockito.ArgumentMatchers.any(HistoricoEntity.class));
    }
}
