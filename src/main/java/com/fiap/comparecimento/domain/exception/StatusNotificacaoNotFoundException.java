package com.fiap.comparecimento.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class StatusNotificacaoNotFoundException extends ResponseStatusException {

    private static final String MESSAGE = "Status Notificação não encontrado. Valor inválido: ";

    public StatusNotificacaoNotFoundException(String value) {
        super(HttpStatus.NOT_FOUND, MESSAGE + value);
    }
}
