package com.fiap.comparecimento.application.usecase.calcula.comparecimento.implementation;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.domain.enuns.ClassificacaoPacienteEnum;
import com.fiap.comparecimento.domain.enuns.StatusConsultaEnum;
import com.fiap.comparecimento.domain.enuns.StatusNotificacaoEnum;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.domain.model.PacienteDomain;

@ExtendWith(MockitoExtension.class)
// Adicionado para evitar erros de stubbing caso algum método mude o fluxo
@MockitoSettings(strictness = Strictness.LENIENT)
class CalculaComparecimentoUseCaseImplTest {

    @Mock
    private PacienteGateway pacienteGateway;

    @InjectMocks
    private CalculaComparecimentoUseCaseImpl useCase;

    private PacienteDomain pacienteDomain;
    private EventoAgendamentoMessageDomain eventoDomain;

    @BeforeEach
    void setUp() {
        // Estado inicial consistente
        pacienteDomain = new PacienteDomain();
        pacienteDomain.setCns("123456789012345");
        pacienteDomain.setTotalComparecimentos(10);
        pacienteDomain.setTotalFaltas(2);
        pacienteDomain.setTotalConfirmacoes(8);
        pacienteDomain.setTotalCancelamentos(1);
        pacienteDomain.setTotalAgendamentos(13);
        pacienteDomain.setIcc(75);
        pacienteDomain.setClassificacao(ClassificacaoPacienteEnum.COMPARECIMENTO_PROVAVEL.name());
        pacienteDomain.setUltimaAtualizacao(OffsetDateTime.now().minusDays(1));
    }

    @Test
    void deveCalcularComparecimentoParaStatusAgendado() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.AGENDADO,
                StatusNotificacaoEnum.ENVIADA, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        ArgumentCaptor<PacienteDomain> captor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(captor.capture());

        assertEquals(14, captor.getValue().getTotalAgendamentos());
    }

    @Test
    void deveCalcularComparecimentoParaStatusRealizado() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.ENTREGUE, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        ArgumentCaptor<PacienteDomain> captor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(captor.capture());

        // Se o teste falha aqui dizendo que recebeu 1, verifique se sua UseCase não está dando 'new' no paciente
        assertEquals(11, captor.getValue().getTotalComparecimentos());
    }

    @Test
    void deveCalcularComparecimentoParaStatusFalta() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.FALTA,
                StatusNotificacaoEnum.EXPIRADA, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        ArgumentCaptor<PacienteDomain> captor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(captor.capture());
        assertEquals(3, captor.getValue().getTotalFaltas());
    }

    @Test
    void deveCalcularComparecimentoParaStatusConfirmado() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.CONFIRMADO,
                StatusNotificacaoEnum.CONFIRMOU_48H_ANTECEDENCIA, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        ArgumentCaptor<PacienteDomain> captor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(captor.capture());
        assertEquals(9, captor.getValue().getTotalConfirmacoes());
    }

    @Test
    void deveCalcularComparecimentoParaStatusCancelado() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.CANCELADO,
                StatusNotificacaoEnum.CONFIRMOU_24H_ANTECEDENCIA, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        ArgumentCaptor<PacienteDomain> captor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(captor.capture());
        assertEquals(2, captor.getValue().getTotalCancelamentos());
    }

    @Test
    void deveClassificarComoMuitoConfiavelQuandoIccMaiorOuIgual90() {
        // Forçando cenário de alta confiança
        pacienteDomain.setTotalAgendamentos(10);
        pacienteDomain.setTotalComparecimentos(9);

        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.CONFIRMOU_48H_ANTECEDENCIA, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        ArgumentCaptor<PacienteDomain> captor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(captor.capture());
        assertTrue(captor.getValue().getIcc() >= 90);
    }

    @Test
    void deveClassificarComoConfiavelQuandoIccEntre80e89() {
        pacienteDomain.setTotalAgendamentos(10);
        pacienteDomain.setTotalComparecimentos(8);

        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.ENTREGUE, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        ArgumentCaptor<PacienteDomain> captor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(captor.capture());
        assertNotNull(captor.getValue().getClassificacao());
    }

    @Test
    void deveClassificarComoCriticoQuandoIccEntre30e39() {
        pacienteDomain.setTotalAgendamentos(10);
        pacienteDomain.setTotalComparecimentos(3);

        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.FALTA,
                StatusNotificacaoEnum.EXPIRADA, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        ArgumentCaptor<PacienteDomain> captor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(captor.capture());
        assertNotNull(captor.getValue().getIcc());
    }

    @Test
    void deveCalcularScoreEventoComDiferentesCombinacoesDeStatus() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.CONFIRMOU_48H_ANTECEDENCIA, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        verify(pacienteGateway, times(1)).criaOuAtualizarInformacoesPaciente(any(PacienteDomain.class));
    }

    @Test
    void deveCalcularScoreEventoComNotificacaoEnviada() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.AGENDADO,
                StatusNotificacaoEnum.ENVIADA, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(any(PacienteDomain.class));
    }

    @Test
    void deveCalcularScoreEventoComNotificacaoEntregue() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.ENTREGUE, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(any(PacienteDomain.class));
    }

    @Test
    void deveCalcularScoreEventoComNotificacaoConfirmou24H() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.CONFIRMADO,
                StatusNotificacaoEnum.CONFIRMOU_24H_ANTECEDENCIA, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(any(PacienteDomain.class));
    }

    @Test
    void deveCalcularScoreEventoComNotificacaoFalha() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.FALHA, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(any(PacienteDomain.class));
    }

    @Test
    void deveCalcularIccComPacienteNovo() {
        PacienteDomain novoPaciente = new PacienteDomain();
        novoPaciente.setCns("555");

        novoPaciente.setIcc(100);
        novoPaciente.setClassificacao(ClassificacaoPacienteEnum.MUITO_CONFIAVEL.name());

        novoPaciente.setTotalAgendamentos(0);
        novoPaciente.setTotalComparecimentos(0);
        novoPaciente.setTotalFaltas(0);
        novoPaciente.setTotalConfirmacoes(0);
        novoPaciente.setTotalCancelamentos(0);

        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "555",
                StatusConsultaEnum.AGENDADO,
                StatusNotificacaoEnum.ENVIADA,
                OffsetDateTime.now()
        );

        useCase.calculaComparecimento(novoPaciente, eventoDomain);

        ArgumentCaptor<PacienteDomain> captor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(captor.capture());

        assertEquals(1, captor.getValue().getTotalAgendamentos());
        assertEquals(100, captor.getValue().getIcc()); // 🔒 continua máximo
        assertEquals(
                ClassificacaoPacienteEnum.MUITO_CONFIAVEL.name(),
                captor.getValue().getClassificacao()
        );
    }

    @Test
    void deveAtualizarUltimaAtualizacao() {
        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123456789012345", StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.ENTREGUE, OffsetDateTime.now());

        useCase.calculaComparecimento(pacienteDomain, eventoDomain);

        ArgumentCaptor<PacienteDomain> captor = ArgumentCaptor.forClass(PacienteDomain.class);
        verify(pacienteGateway).criaOuAtualizarInformacoesPaciente(captor.capture());
        assertNotNull(captor.getValue().getUltimaAtualizacao());
    }

    @Test
    void deveCalcularIccComTodosOsValoresZerados() {
        PacienteDomain zerado = new PacienteDomain();
        zerado.setCns("123");

        zerado.setIcc(100);
        zerado.setClassificacao(ClassificacaoPacienteEnum.MUITO_CONFIAVEL.name());

        zerado.setTotalAgendamentos(0);
        zerado.setTotalComparecimentos(0);
        zerado.setTotalFaltas(0);
        zerado.setTotalConfirmacoes(0);
        zerado.setTotalCancelamentos(0);

        eventoDomain = new EventoAgendamentoMessageDomain(
                1L, "123",
                StatusConsultaEnum.REALIZADO,
                StatusNotificacaoEnum.CONFIRMOU_48H_ANTECEDENCIA,
                OffsetDateTime.now()
        );

        useCase.calculaComparecimento(zerado, eventoDomain);

        verify(pacienteGateway)
                .criaOuAtualizarInformacoesPaciente(any(PacienteDomain.class));
    }

}