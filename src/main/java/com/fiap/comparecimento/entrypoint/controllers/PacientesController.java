package com.fiap.comparecimento.entrypoint.controllers;

import com.fiap.comparecimento.application.usecase.pacientes.ConsultarIndiceComparecimentoPacienteUseCase;
import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.entrypoint.controllers.presenter.IndiceComparecimentoPacientePresenter;
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

    private final ConsultarIndiceComparecimentoPacienteUseCase consultarIndiceComparecimentoPacienteUseCase;

    public PacientesController(ConsultarIndiceComparecimentoPacienteUseCase consultarIndiceComparecimentoPacienteUseCase) {
        this.consultarIndiceComparecimentoPacienteUseCase = consultarIndiceComparecimentoPacienteUseCase;
    }

    @Override
    public ResponseEntity<IndiceComparecimentoResponseDto> _consultarIndiceComparecimentoPaciente(String cns) {
        PacienteDomain domain = consultarIndiceComparecimentoPacienteUseCase.consultar(cns);
        IndiceComparecimentoResponseDto dto = IndiceComparecimentoPacientePresenter.toIndiceComparecimentoResponseDto(domain);
        return ResponseEntity.ok(dto);
    }
}
