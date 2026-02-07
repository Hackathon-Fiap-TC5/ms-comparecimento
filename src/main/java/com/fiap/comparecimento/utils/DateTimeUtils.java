package com.fiap.comparecimento.utils;

import java.time.*;

public final class DateTimeUtils {

    private static final ZoneId ZONE_BRASIL = ZoneId.of("America/Sao_Paulo");

    private DateTimeUtils() {
        // util class
    }

    /**
     * Converte LocalDate para OffsetDateTime no início do dia
     */
    public static OffsetDateTime inicioDoDia(LocalDate data) {
        return data
                .atStartOfDay(ZONE_BRASIL)
                .toOffsetDateTime();
    }

    /**
     * Converte LocalDate para OffsetDateTime no fim do dia
     */
    public static OffsetDateTime fimDoDia(LocalDate data) {
        return data
                .atTime(LocalTime.MAX)
                .atZone(ZONE_BRASIL)
                .toOffsetDateTime();
    }
}