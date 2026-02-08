package com.fiap.comparecimento.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RelatorioAbsenteismoDomainTest {

    private RelatorioAbsenteismoDomain relatorio;

    @BeforeEach
    void setUp() {
        relatorio = new RelatorioAbsenteismoDomain();
    }

    @Test
    void deveAjustarEscalaParaTaxaAbsenteismo() {
        // Given
        relatorio.setTaxaAbsenteismo(new BigDecimal("10.123456"));

        // When
        relatorio.ajustarEscalas();

        // Then
        assertNotNull(relatorio.getTaxaAbsenteismo());
        assertEquals(2, relatorio.getTaxaAbsenteismo().scale());
    }

    @Test
    void deveAjustarEscalaParaIccMedio() {
        // Given
        relatorio.setIccMedio(new BigDecimal("75.555555"));

        // When
        relatorio.ajustarEscalas();

        // Then
        assertNotNull(relatorio.getIccMedio());
        assertEquals(2, relatorio.getIccMedio().scale());
    }

    @Test
    void deveAjustarAmbasEscalas() {
        // Given
        relatorio.setTaxaAbsenteismo(new BigDecimal("10.123456"));
        relatorio.setIccMedio(new BigDecimal("75.555555"));

        // When
        relatorio.ajustarEscalas();

        // Then
        assertEquals(2, relatorio.getTaxaAbsenteismo().scale());
        assertEquals(2, relatorio.getIccMedio().scale());
    }

    @Test
    void naoDeveLancarExcecaoQuandoValoresNulos() {
        // Given
        relatorio.setTaxaAbsenteismo(null);
        relatorio.setIccMedio(null);

        // When & Then
        assertDoesNotThrow(() -> relatorio.ajustarEscalas());
    }

    @Test
    void deveArredondarCorretamente() {
        // Given
        relatorio.setTaxaAbsenteismo(new BigDecimal("10.125"));
        relatorio.setIccMedio(new BigDecimal("75.555"));

        // When
        relatorio.ajustarEscalas();

        // Then
        assertEquals(new BigDecimal("10.13"), relatorio.getTaxaAbsenteismo());
        assertEquals(new BigDecimal("75.56"), relatorio.getIccMedio());
    }

    @Test
    void deveManterValoresJaComEscalaCorreta() {
        // Given
        relatorio.setTaxaAbsenteismo(new BigDecimal("10.00"));
        relatorio.setIccMedio(new BigDecimal("75.50"));

        // When
        relatorio.ajustarEscalas();

        // Then
        assertEquals(new BigDecimal("10.00"), relatorio.getTaxaAbsenteismo());
        assertEquals(new BigDecimal("75.50"), relatorio.getIccMedio());
    }

    @Test
    void deveCriarRelatorioComConstrutorCompleto() {
        // Given
        PeriodoDomain periodo = new PeriodoDomain();
        OffsetDateTime dataGeracao = OffsetDateTime.now();

        // When
        RelatorioAbsenteismoDomain relatorio = new RelatorioAbsenteismoDomain(
                periodo,
                100,
                new BigDecimal("75.50"),
                500,
                50,
                new BigDecimal("10.00"),
                dataGeracao
        );

        // Then
        assertNotNull(relatorio);
        assertEquals(periodo, relatorio.getPeriodo());
        assertEquals(100, relatorio.getTotalPessoas());
        assertEquals(new BigDecimal("75.50"), relatorio.getIccMedio());
        assertEquals(500, relatorio.getTotalConsultas());
        assertEquals(50, relatorio.getTotalFaltas());
        assertEquals(new BigDecimal("10.00"), relatorio.getTaxaAbsenteismo());
        assertEquals(dataGeracao, relatorio.getDataGeracao());
    }
}
