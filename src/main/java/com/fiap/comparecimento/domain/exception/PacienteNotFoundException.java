package com.fiap.comparecimento.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PacienteNotFoundException extends ResponseStatusException {

    private static final String MESSAGE = "Usuário não encontrado.";

    public PacienteNotFoundException() {
        super(HttpStatus.NOT_FOUND, MESSAGE);
    }
}
