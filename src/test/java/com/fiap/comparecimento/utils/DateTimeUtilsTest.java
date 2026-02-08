package com.fiap.comparecimento.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class DateTimeUtilsTest {

    private static final ZoneId ZONE_BRASIL = ZoneId.of("America/Sao_Paulo");

    @Test
    void deveConverterLocalDateParaInicioDoDia() {
        // Given
        LocalDate data = LocalDate.of(2024, 6, 15);

        // When
        OffsetDateTime result = DateTimeUtils.inicioDoDia(data);

        // Then
        assertNotNull(result);
        assertEquals(data.atStartOfDay(ZONE_BRASIL).toOffsetDateTime(), result);
        assertEquals(LocalTime.MIDNIGHT, result.toLocalTime());
    }

    @Test
    void deveConverterLocalDateParaFimDoDia() {
        // Given
        LocalDate data = LocalDate.of(2024, 6, 15);

        // When
        OffsetDateTime result = DateTimeUtils.fimDoDia(data);

        // Then
        assertNotNull(result);
        assertEquals(data.atTime(LocalTime.MAX).atZone(ZONE_BRASIL).toOffsetDateTime(), result);
        assertEquals(LocalTime.MAX, result.toLocalTime());
    }

    @Test
    void deveRetornarOffsetDateTimeComFusoHorarioBrasil() {
        // Given
        LocalDate data = LocalDate.of(2024, 12, 31);

        // When
        OffsetDateTime inicio = DateTimeUtils.inicioDoDia(data);
        OffsetDateTime fim = DateTimeUtils.fimDoDia(data);

        // Then
        assertEquals(data.atStartOfDay(ZONE_BRASIL).toOffsetDateTime().getOffset(), inicio.getOffset());
        assertEquals(data.atTime(LocalTime.MAX).atZone(ZONE_BRASIL).toOffsetDateTime().getOffset(), fim.getOffset());
    }

    @Test
    void deveRetornarMesmoDiaParaInicioEFim() {
        // Given
        LocalDate data = LocalDate.of(2024, 3, 20);

        // When
        OffsetDateTime inicio = DateTimeUtils.inicioDoDia(data);
        OffsetDateTime fim = DateTimeUtils.fimDoDia(data);

        // Then
        assertEquals(data, inicio.toLocalDate());
        assertEquals(data, fim.toLocalDate());
    }

    @Test
    void deveRetornarInicioDoDiaComHoraZero() {
        // Given
        LocalDate data = LocalDate.of(2024, 1, 1);

        // When
        OffsetDateTime result = DateTimeUtils.inicioDoDia(data);

        // Then
        assertEquals(0, result.getHour());
        assertEquals(0, result.getMinute());
        assertEquals(0, result.getSecond());
    }

    @Test
    void deveRetornarFimDoDiaComHoraMaxima() {
        // Given
        LocalDate data = LocalDate.of(2024, 1, 1);

        // When
        OffsetDateTime result = DateTimeUtils.fimDoDia(data);

        // Then
        assertEquals(23, result.getHour());
        assertEquals(59, result.getMinute());
        assertEquals(59, result.getSecond());
        assertEquals(999999999, result.getNano());
    }
}
