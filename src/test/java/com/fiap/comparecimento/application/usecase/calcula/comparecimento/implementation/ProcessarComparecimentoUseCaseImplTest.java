package com.fiap.comparecimento.application.usecase.calcula.comparecimento.implementation;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.calcula.comparecimento.CalculaComparecimentoUseCase;
import com.fiap.comparecimento.application.usecase.historico.AdicionaItemHistoricoUseCase;
import com.fiap.comparecimento.domain.enuns.StatusConsultaEnum;
import com.fiap.comparecimento.domain.enuns.StatusNotificacaoEnum;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.entrypoint.producer.ComparecimentoProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private ProcessarComparecimentoUseCaseImpl useCase;

    private EventoAgendamentoMessageDomain evento;
    private PacienteDomain pacienteDomain;

    @BeforeEach
    void setUp() {
        evento = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.ENTREGUE, OffsetDateTime.now());
        pacienteDomain = new PacienteDomain();
        pacienteDomain.setCns("123456789012345");
        pacienteDomain.setIcc(80);
        pacienteDomain.setClassificacao("CONFIAVEL");
        pacienteDomain.setTotalComparecimentos(8);
        pacienteDomain.setTotalFaltas(2);
        pacienteDomain.setTotalAgendamentos(10);
    }

    @Test
    void deveProcessarComparecimentoQuandoPacienteExiste() {
        when(pacienteGateway.buscarPorCns("123456789012345")).thenReturn(Optional.of(pacienteDomain));
        when(pacienteGateway.consultar("123456789012345")).thenReturn(pacienteDomain);

        useCase.processaComparecimento(evento);

        verify(calculaComparecimentoUseCase).calculaComparecimento(pacienteDomain, evento);
        verify(adicionaItemHistoricoUseCase).adicionaItemHistorico(any());
        verify(comparecimentoProducer).sendSugestions(any());
    }

    @Test
    void deveCriarPacienteNovoQuandoNaoExiste() {
        when(pacienteGateway.buscarPorCns("123456789012345")).thenReturn(Optional.empty());
        when(pacienteGateway.consultar("123456789012345")).thenReturn(pacienteDomain);

        useCase.processaComparecimento(evento);

        ArgumentCaptor<PacienteDomain> pacienteCaptor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).atualizarInformacoesPaciente(pacienteCaptor.capture());
        PacienteDomain novoPaciente = pacienteCaptor.getValue();
        assertEquals("123456789012345", novoPaciente.getCns());
        assertEquals(100, novoPaciente.getIcc());
        assertEquals("MUITO_CONFIAVEL", novoPaciente.getClassificacao());
    }

    @Test
    void deveAdicionarHistoricoComDadosCorretos() {
        when(pacienteGateway.buscarPorCns("123456789012345")).thenReturn(Optional.of(pacienteDomain));
        when(pacienteGateway.consultar("123456789012345")).thenReturn(pacienteDomain);

        useCase.processaComparecimento(evento);

        ArgumentCaptor<com.fiap.comparecimento.domain.model.HistoricoDomain> historicoCaptor =
                ArgumentCaptor.forClass(com.fiap.comparecimento.domain.model.HistoricoDomain.class);
        verify(adicionaItemHistoricoUseCase).adicionaItemHistorico(historicoCaptor.capture());
        var historico = historicoCaptor.getValue();
        assertEquals("123456789012345", historico.getCns());
        assertEquals(1L, historico.getIdAgendamento());
        assertEquals("REALIZADO", historico.getStatusConsulta());
        assertEquals("ENTREGUE", historico.getStatusNotificacao());
    }
}
