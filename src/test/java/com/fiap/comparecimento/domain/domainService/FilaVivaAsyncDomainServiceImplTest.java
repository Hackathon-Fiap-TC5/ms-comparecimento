package com.fiap.comparecimento.domain.domainService;

import com.fiap.comparecimento.application.gateway.FilaVivaGateway;
import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.domain.domainService.implementation.FilaVivaAsyncDomainServiceImpl;
import com.fiap.comparecimento.domain.enuns.StatusConsultaEnum;
import com.fiap.comparecimento.domain.enuns.StatusNotificacaoEnum;
import com.fiap.comparecimento.domain.enuns.SugestaoCondutaEnum;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.domain.model.FilaVivaDomain;
import com.fiap.comparecimento.domain.model.PacienteDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilaVivaAsyncDomainServiceImplTest {

    @Mock
    private FilaVivaGateway filaVivaGateway;

    @Mock
    private PacienteGateway pacienteGateway;

    @InjectMocks
    private FilaVivaAsyncDomainServiceImpl service;

    private EventoAgendamentoMessageDomain evento;
    private PacienteDomain paciente;

    @BeforeEach
    void setUp() {
        paciente = new PacienteDomain();
        paciente.setCns("123456789012345");

        evento = new EventoAgendamentoMessageDomain(
                1L,
                "999999999999999",
                StatusConsultaEnum.CANCELADO,
                StatusNotificacaoEnum.CONFIRMOU_24H_ANTECEDENCIA,
                OffsetDateTime.now()
        );
    }

    @Test
    void naoDeveProcessarQuandoStatusNaoForCanceladoOuFalta() {

        evento = new EventoAgendamentoMessageDomain(
                1L,
                "999",
                StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.ENTREGUE,
                OffsetDateTime.now()
        );

        service.processarFilaViva(evento);

        verifyNoInteractions(pacienteGateway);
        verifyNoInteractions(filaVivaGateway);
    }

    @Test
    void naoDevePublicarQuandoNaoExistirPacienteNaFilaViva() {

        when(pacienteGateway.consultaPacienteFilaViva())
                .thenReturn(null);

        service.processarFilaViva(evento);

        verify(pacienteGateway).consultaPacienteFilaViva();
        verifyNoInteractions(filaVivaGateway);
    }

    @Test
    void devePublicarFilaVivaQuandoStatusForCancelado() {

        when(pacienteGateway.consultaPacienteFilaViva())
                .thenReturn(paciente);

        service.processarFilaViva(evento);

        ArgumentCaptor<FilaVivaDomain> captor =
                ArgumentCaptor.forClass(FilaVivaDomain.class);

        verify(filaVivaGateway).publicarFilaViva(captor.capture());

        FilaVivaDomain filaViva = captor.getValue();

        assertEquals(evento.getIdAgendamento(), filaViva.getIdAgendamento());
        assertEquals(paciente.getCns(), filaViva.getCns());
        assertEquals(
                SugestaoCondutaEnum.REALOCACAO_IMEDIATA.getDescricao(),
                filaViva.getSugestaoConduta()
        );
    }

    @Test
    void devePublicarFilaVivaQuandoStatusForFalta() {

        evento = new EventoAgendamentoMessageDomain(
                2L,
                "999",
                StatusConsultaEnum.FALTA,
                StatusNotificacaoEnum.EXPIRADA,
                OffsetDateTime.now()
        );

        when(pacienteGateway.consultaPacienteFilaViva())
                .thenReturn(paciente);

        service.processarFilaViva(evento);

        verify(filaVivaGateway, times(1))
                .publicarFilaViva(any(FilaVivaDomain.class));
    }
}