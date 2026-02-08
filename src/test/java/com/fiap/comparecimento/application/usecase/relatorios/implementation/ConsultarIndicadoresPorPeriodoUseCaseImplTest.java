package com.fiap.comparecimento.application.usecase.relatorios.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.domain.model.RelatorioAbsenteismoDomain;

@ExtendWith(MockitoExtension.class)
class ConsultarIndicadoresPorPeriodoUseCaseImplTest {

    @Mock
    private PacienteGateway pacienteGateway;

    @InjectMocks
    private ConsultarIndicadoresPorPeriodoUseCaseImpl useCase;

    private RelatorioAbsenteismoDomain relatorioDomain;

    @BeforeEach
    void setUp() {
        relatorioDomain = new RelatorioAbsenteismoDomain();
        relatorioDomain.setTotalPessoas(100);
        relatorioDomain.setIccMedio(new BigDecimal("75.50"));
        relatorioDomain.setTotalConsultas(500);
        relatorioDomain.setTotalFaltas(50);
        relatorioDomain.setTaxaAbsenteismo(new BigDecimal("10.00"));
    }

    @Test
    void deveConsultarRelatorioPorPeriodo() {
        // Given
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 1, 31);
        
        when(pacienteGateway.consultarRelatorioAbsenteismo(any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(relatorioDomain);

        // When
        RelatorioAbsenteismoDomain result = useCase.consultar(dataInicio, dataFim);

        // Then
        assertNotNull(result);
        assertEquals(100, result.getTotalPessoas());
        assertEquals(new BigDecimal("75.50"), result.getIccMedio());
        assertEquals(500, result.getTotalConsultas());
        assertEquals(50, result.getTotalFaltas());
        verify(pacienteGateway, times(1)).consultarRelatorioAbsenteismo(any(OffsetDateTime.class), any(OffsetDateTime.class));
    }

    @Test
    void deveConverterDataInicioParaInicioDoDia() {
        // Given
        LocalDate dataInicio = LocalDate.of(2024, 6, 15);
        LocalDate dataFim = LocalDate.of(2024, 6, 30);
        
        when(pacienteGateway.consultarRelatorioAbsenteismo(any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(relatorioDomain);

        // When
        useCase.consultar(dataInicio, dataFim);

        // Then
        verify(pacienteGateway).consultarRelatorioAbsenteismo(any(OffsetDateTime.class), any(OffsetDateTime.class));
    }

    @Test
    void deveConverterDataFimParaFimDoDia() {
        // Given
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 12, 31);
        
        when(pacienteGateway.consultarRelatorioAbsenteismo(any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(relatorioDomain);

        // When
        useCase.consultar(dataInicio, dataFim);

        // Then
        verify(pacienteGateway).consultarRelatorioAbsenteismo(any(OffsetDateTime.class), any(OffsetDateTime.class));
    }

    @Test
    void deveRetornarRelatorioCompleto() {
        // Given
        LocalDate dataInicio = LocalDate.of(2024, 3, 1);
        LocalDate dataFim = LocalDate.of(2024, 3, 31);
        
        when(pacienteGateway.consultarRelatorioAbsenteismo(any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(relatorioDomain);

        // When
        RelatorioAbsenteismoDomain result = useCase.consultar(dataInicio, dataFim);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTotalPessoas());
        assertNotNull(result.getIccMedio());
        assertNotNull(result.getTotalConsultas());
        assertNotNull(result.getTotalFaltas());
        assertNotNull(result.getTaxaAbsenteismo());
    }

    @Test
    void deveChamarGatewayUmaVez() {
        // Given
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 1, 31);
        
        when(pacienteGateway.consultarRelatorioAbsenteismo(any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(relatorioDomain);

        // When
        useCase.consultar(dataInicio, dataFim);

        // Then
        verify(pacienteGateway, times(1)).consultarRelatorioAbsenteismo(any(OffsetDateTime.class), any(OffsetDateTime.class));
        verifyNoMoreInteractions(pacienteGateway);
    }
}
