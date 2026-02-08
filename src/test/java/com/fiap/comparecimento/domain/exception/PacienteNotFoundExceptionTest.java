package com.fiap.comparecimento.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PacienteNotFoundExceptionTest {

    @Test
    void deveCriarExcecaoComStatusNotFound() {
        // When
        PacienteNotFoundException exception = new PacienteNotFoundException();

        // Then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Usuário não encontrado.", exception.getReason());
    }

    @Test
    void deveSerInstanciaDeResponseStatusException() {
        // When
        PacienteNotFoundException exception = new PacienteNotFoundException();

        // Then
        assertTrue(exception instanceof org.springframework.web.server.ResponseStatusException);
    }
}
