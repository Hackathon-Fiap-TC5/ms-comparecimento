package com.fiap.comparecimento.entrypoint.controllers;

import com.fiap.comparecimento.application.usecase.relatorios.ConsultarIndicadoresPorPeriodoUseCase;
import com.fiap.comparecimento.domain.model.PeriodoDomain;
import com.fiap.comparecimento.domain.model.RelatorioAbsenteismoDomain;
import com.fiap.comparecimentoDomain.gen.model.RelatorioAbsenteismoResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatoriosControllerTest {

    @Mock
    private ConsultarIndicadoresPorPeriodoUseCase consultarIndicadoresPorPeriodoUseCase;

    @InjectMocks
    private RelatoriosController controller;

    private RelatorioAbsenteismoDomain relatorioDomain;

    @BeforeEach
    void setUp() {
        PeriodoDomain periodo = new PeriodoDomain();
        periodo.setDataInicio(OffsetDateTime.now().minusDays(30));
        periodo.setDataFim(OffsetDateTime.now());

        relatorioDomain = new RelatorioAbsenteismoDomain();
        relatorioDomain.setPeriodo(periodo);
        relatorioDomain.setTotalPessoas(100);
        relatorioDomain.setIccMedio(new BigDecimal("75.50"));
        relatorioDomain.setTotalConsultas(500);
        relatorioDomain.setTotalFaltas(50);
        relatorioDomain.setTaxaAbsenteismo(new BigDecimal("10.00"));
        relatorioDomain.setDataGeracao(OffsetDateTime.now());
    }

    @Test
    void deveConsultarRelatorioAbsenteismoPorPeriodoComSucesso() {
        // Given
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 1, 31);
        
        when(consultarIndicadoresPorPeriodoUseCase.consultar(dataInicio, dataFim)).thenReturn(relatorioDomain);

        // When
        ResponseEntity<RelatorioAbsenteismoResponseDto> response = 
                controller._relatorioAbsenteismoPorPeriodo(dataInicio, dataFim);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(consultarIndicadoresPorPeriodoUseCase, times(1)).consultar(dataInicio, dataFim);
    }

    @Test
    void deveRetornarResponseComDadosCorretos() {
        // Given
        LocalDate dataInicio = LocalDate.of(2024, 6, 1);
        LocalDate dataFim = LocalDate.of(2024, 6, 30);
        
        when(consultarIndicadoresPorPeriodoUseCase.consultar(dataInicio, dataFim)).thenReturn(relatorioDomain);

        // When
        ResponseEntity<RelatorioAbsenteismoResponseDto> response = 
                controller._relatorioAbsenteismoPorPeriodo(dataInicio, dataFim);

        // Then
        assertNotNull(response.getBody());
        RelatorioAbsenteismoResponseDto dto = response.getBody();
        assertEquals(100, dto.getTotalPessoas());
        assertEquals(new BigDecimal("75.50"), dto.getIccMedio());
        assertEquals(500, dto.getTotalConsultas());
        assertEquals(50, dto.getTotalFaltas());
        assertEquals(new BigDecimal("10.00"), dto.getTaxaAbsenteismo());
    }

    @Test
    void deveChamarUseCaseUmaVez() {
        // Given
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 12, 31);
        
        when(consultarIndicadoresPorPeriodoUseCase.consultar(dataInicio, dataFim)).thenReturn(relatorioDomain);

        // When
        controller._relatorioAbsenteismoPorPeriodo(dataInicio, dataFim);

        // Then
        verify(consultarIndicadoresPorPeriodoUseCase, times(1)).consultar(dataInicio, dataFim);
        verifyNoMoreInteractions(consultarIndicadoresPorPeriodoUseCase);
    }
}
