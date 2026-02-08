package com.fiap.comparecimento.application.usecase.pacientes.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.domain.model.PacienteDomain;

@ExtendWith(MockitoExtension.class)
class ConsultarIndiceComparecimentoPacienteUseCaseImplTest {

    @Mock
    private PacienteGateway pacienteGateway;

    @InjectMocks
    private ConsultarIndiceComparecimentoPacienteUseCaseImpl useCase;

    private PacienteDomain pacienteDomain;

    @BeforeEach
    void setUp() {
        pacienteDomain = new PacienteDomain();
        pacienteDomain.setCns("123456789012345");
        pacienteDomain.setIcc(85);
        pacienteDomain.setClassificacao("CONFIAVEL");
        pacienteDomain.setTotalComparecimentos(17);
        pacienteDomain.setTotalFaltas(3);
    }

    @Test
    void deveConsultarPacientePorCns() {
        // Given
        String cns = "123456789012345";
        when(pacienteGateway.consultar(cns)).thenReturn(pacienteDomain);

        // When
        PacienteDomain result = useCase.consultar(cns);

        // Then
        assertNotNull(result);
        assertEquals(cns, result.getCns());
        assertEquals(85, result.getIcc());
        assertEquals("CONFIAVEL", result.getClassificacao());
        verify(pacienteGateway, times(1)).consultar(cns);
    }

    @Test
    void deveRetornarPacienteCompleto() {
        // Given
        String cns = "987654321098765";
        pacienteDomain.setCns(cns);
        pacienteDomain.setTotalConfirmacoes(15);
        pacienteDomain.setTotalCancelamentos(2);
        pacienteDomain.setTotalAgendamentos(20);
        
        when(pacienteGateway.consultar(cns)).thenReturn(pacienteDomain);

        // When
        PacienteDomain result = useCase.consultar(cns);

        // Then
        assertNotNull(result);
        assertEquals(cns, result.getCns());
        assertEquals(15, result.getTotalConfirmacoes());
        assertEquals(2, result.getTotalCancelamentos());
        assertEquals(20, result.getTotalAgendamentos());
        verify(pacienteGateway).consultar(cns);
    }

    @Test
    void deveChamarGatewayUmaVez() {
        // Given
        String cns = "123456789012345";
        when(pacienteGateway.consultar(cns)).thenReturn(pacienteDomain);

        // When
        useCase.consultar(cns);

        // Then
        verify(pacienteGateway, times(1)).consultar(cns);
        verifyNoMoreInteractions(pacienteGateway);
    }
}
