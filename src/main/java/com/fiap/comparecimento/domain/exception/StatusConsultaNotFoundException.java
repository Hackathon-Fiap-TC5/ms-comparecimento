package com.fiap.comparecimento.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class StatusConsultaNotFoundException extends ResponseStatusException {

    private static final String MESSAGE = "Status Consulta não encontrado. Valor inválido: ";

    public StatusConsultaNotFoundException(String value) {
        super(HttpStatus.NOT_FOUND, MESSAGE + value);
    }
}
