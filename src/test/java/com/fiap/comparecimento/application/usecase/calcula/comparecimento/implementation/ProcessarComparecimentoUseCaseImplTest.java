package com.fiap.comparecimento.application.usecase.calcula.comparecimento.implementation;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.CalculaComparecimentoUseCase;
import com.fiap.comparecimento.application.usecase.historico.AdicionaItemHistoricoUseCase;
import com.fiap.comparecimento.domain.domainService.FilaVivaAsyncDomainService;
import com.fiap.comparecimento.domain.enuns.StatusConsultaEnum;
import com.fiap.comparecimento.domain.enuns.StatusNotificacaoEnum;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.infrastructure.producer.ComparecimentoProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessarComparecimentoUseCaseImplTest {

    @Mock
    private PacienteGateway pacienteGateway;

    @Mock
    private AdicionaItemHistoricoUseCase adicionaItemHistoricoUseCase;

    @Mock
    private CalculaComparecimentoUseCase calculaComparecimentoUseCase;

    @Mock
    private ComparecimentoProducer comparecimentoProducer;

    @Mock
    private FilaVivaAsyncDomainService filaVivaAsyncService;

    @InjectMocks
    private ProcessarComparecimentoUseCaseImpl useCase;

    private EventoAgendamentoMessageDomain evento;
    private PacienteDomain paciente;

    @BeforeEach
    void setUp() {
        evento = new EventoAgendamentoMessageDomain(
                1L,
                "123456789012345",
                StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.ENTREGUE,
                OffsetDateTime.now()
        );

        paciente = new PacienteDomain();
        paciente.setCns("123456789012345");
        paciente.setIcc(80);
        paciente.setClassificacao("CONFIAVEL");
        paciente.setTotalComparecimentos(8);
        paciente.setTotalFaltas(2);
        paciente.setTotalAgendamentos(10);
    }

    @Test
    void deveProcessarComparecimentoQuandoPacienteExiste() {
        when(pacienteGateway.consultar("123456789012345"))
                .thenReturn(Optional.of(paciente));

        useCase.processaComparecimento(evento);

        verify(filaVivaAsyncService).processarFilaViva(evento);
        verify(calculaComparecimentoUseCase)
                .calculaComparecimento(paciente, evento);
        verify(adicionaItemHistoricoUseCase)
                .adicionaItemHistorico(any());
        verify(comparecimentoProducer)
                .sendSugestions(any());

        verify(pacienteGateway, never())
                .criaOuAtualizarInformacoesPaciente(argThat(p ->
                        p.getIcc() == 100
                ));
    }

    @Test
    void deveAdicionarHistoricoComDadosCorretos() {
        when(pacienteGateway.consultar("123456789012345"))
                .thenReturn(Optional.of(paciente));

        ArgumentCaptor<com.fiap.comparecimento.domain.model.HistoricoDomain>
                historicoCaptor =
                ArgumentCaptor.forClass(
                        com.fiap.comparecimento.domain.model.HistoricoDomain.class
                );

        useCase.processaComparecimento(evento);

        verify(adicionaItemHistoricoUseCase)
                .adicionaItemHistorico(historicoCaptor.capture());

        var historico = historicoCaptor.getValue();

        assertEquals("123456789012345", historico.getCns());
        assertEquals(1L, historico.getIdAgendamento());
        assertEquals("REALIZADO", historico.getStatusConsulta());
        assertEquals("ENTREGUE", historico.getStatusNotificacao());
    }
}
