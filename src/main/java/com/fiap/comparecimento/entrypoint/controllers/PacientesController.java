package com.fiap.comparecimento.entrypoint.controllers;

import com.fiap.comparecimentoDomain.PacientesApi;
import com.fiap.comparecimentoDomain.gen.model.IndiceComparecimentoResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1")
public class PacientesController implements PacientesApi {

    @Override
    public ResponseEntity<IndiceComparecimentoResponseDto> _pacientesCnsIndiceComparecimentoGet(String cns) {
        return null;
    }
}
