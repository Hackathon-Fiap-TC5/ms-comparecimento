package com.fiap.comparecimento.entrypoint.controllers;

import com.fiap.comparecimento.application.usecase.pacientes.ConsultarIndiceComparecimentoPacienteUseCase;
import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimentoDomain.gen.model.IndiceComparecimentoResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacientesControllerTest {

    @Mock
    private ConsultarIndiceComparecimentoPacienteUseCase consultarIndiceComparecimentoPacienteUseCase;

    @InjectMocks
    private PacientesController controller;

    private PacienteDomain pacienteDomain;

    @BeforeEach
    void setUp() {
        pacienteDomain = new PacienteDomain();
        pacienteDomain.setCns("123456789012345");
        pacienteDomain.setIcc(85);
        pacienteDomain.setClassificacao("CONFIAVEL");
        pacienteDomain.setTotalComparecimentos(17);
        pacienteDomain.setTotalFaltas(3);
        pacienteDomain.setTotalConfirmacoes(15);
        pacienteDomain.setTotalCancelamentos(2);
        pacienteDomain.setTotalAgendamentos(20);
        pacienteDomain.setUltimaAtualizacao(OffsetDateTime.now());
    }

    @Test
    void deveConsultarIndiceComparecimentoPacienteComSucesso() {
        // Given
        String cns = "123456789012345";
        when(consultarIndiceComparecimentoPacienteUseCase.consultar(cns)).thenReturn(pacienteDomain);

        // When
        ResponseEntity<IndiceComparecimentoResponseDto> response = controller._consultarIndiceComparecimentoPaciente(cns);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(consultarIndiceComparecimentoPacienteUseCase, times(1)).consultar(cns);
    }

    @Test
    void deveRetornarResponseComDadosCorretos() {
        // Given
        String cns = "123456789012345";
        when(consultarIndiceComparecimentoPacienteUseCase.consultar(cns)).thenReturn(pacienteDomain);

        // When
        ResponseEntity<IndiceComparecimentoResponseDto> response = controller._consultarIndiceComparecimentoPaciente(cns);

        // Then
        assertNotNull(response.getBody());
        IndiceComparecimentoResponseDto dto = response.getBody();
        assertEquals(cns, dto.getCns());
        // ICC can be Integer or BigDecimal depending on mapper
        assertNotNull(dto.getIcc());
        assertEquals("CONFIAVEL", dto.getClassificacao());
    }

    @Test
    void deveChamarUseCaseUmaVez() {
        // Given
        String cns = "987654321098765";
        when(consultarIndiceComparecimentoPacienteUseCase.consultar(cns)).thenReturn(pacienteDomain);

        // When
        controller._consultarIndiceComparecimentoPaciente(cns);

        // Then
        verify(consultarIndiceComparecimentoPacienteUseCase, times(1)).consultar(cns);
        verifyNoMoreInteractions(consultarIndiceComparecimentoPacienteUseCase);
    }
}
