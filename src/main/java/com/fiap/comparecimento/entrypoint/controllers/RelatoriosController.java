package com.fiap.comparecimento.entrypoint.controllers;

import com.fiap.comparecimentoDomain.RelatoriosApi;
import com.fiap.comparecimentoDomain.gen.model.IccMedioResponseDto;
import com.fiap.comparecimentoDomain.gen.model.RelatorioAbsenteismoResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/v1")
public class RelatoriosController implements RelatoriosApi {

    @Override
    public ResponseEntity<RelatorioAbsenteismoResponseDto> _relatoriosAbsenteismoGet(LocalDate dataInicio, LocalDate dataFim) {
        return null;
    }

    @Override
    public ResponseEntity<IccMedioResponseDto> _relatoriosIccMedioGet(LocalDate dataInicio, LocalDate dataFim) {
        return null;
    }
}
