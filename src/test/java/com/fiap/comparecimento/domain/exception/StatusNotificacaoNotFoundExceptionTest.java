package com.fiap.comparecimento.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class StatusNotificacaoNotFoundExceptionTest {

    @Test
    void deveCriarExcecaoComMensagemCorreta() {
        // Given
        String statusInvalido = "INVALIDO";

        // When
        StatusNotificacaoNotFoundException exception = new StatusNotificacaoNotFoundException(statusInvalido);

        // Then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains(statusInvalido));
    }

    @Test
    void deveSerInstanciaDeResponseStatusException() {
        // Given
        String statusInvalido = "INVALIDO";

        // When
        StatusNotificacaoNotFoundException exception = new StatusNotificacaoNotFoundException(statusInvalido);

        // Then
        assertTrue(exception instanceof org.springframework.web.server.ResponseStatusException);
    }
}
