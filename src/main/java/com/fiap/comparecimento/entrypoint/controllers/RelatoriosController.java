package com.fiap.comparecimento.entrypoint.controllers;

import com.fiap.comparecimento.application.usecase.relatorios.ConsultarIndicadoresPorPeriodoUseCase;
import com.fiap.comparecimento.domain.model.RelatorioAbsenteismoDomain;
import com.fiap.comparecimento.entrypoint.controllers.presenter.RelatoriosPresenter;
import com.fiap.comparecimentoDomain.RelatoriosApi;
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

    private final ConsultarIndicadoresPorPeriodoUseCase consultarIndicadoresPorPeriodoUseCase;

    public RelatoriosController(ConsultarIndicadoresPorPeriodoUseCase consultarIndicadoresPorPeriodoUseCase) {
        this.consultarIndicadoresPorPeriodoUseCase = consultarIndicadoresPorPeriodoUseCase;
    }

    @Override
    public ResponseEntity<RelatorioAbsenteismoResponseDto> _relatorioAbsenteismoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        RelatorioAbsenteismoDomain domain = consultarIndicadoresPorPeriodoUseCase.consultar(dataInicio, dataFim);
        RelatorioAbsenteismoResponseDto responseDto = RelatoriosPresenter.toRelatorioAbsenteismoResponseDto(domain);
        return ResponseEntity.ok(responseDto);
    }
}
