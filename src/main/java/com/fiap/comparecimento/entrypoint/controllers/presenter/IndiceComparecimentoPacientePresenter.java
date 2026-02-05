package com.fiap.comparecimento.entrypoint.controllers.presenter;

import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.entrypoint.controllers.mappers.PacienteDtoMapper;
import com.fiap.comparecimentoDomain.gen.model.IndiceComparecimentoResponseDto;

public class IndiceComparecimentoPacientePresenter {

    public static IndiceComparecimentoResponseDto toIndiceComparecimentoResponseDto(PacienteDomain pacienteDomain) {
        return PacienteDtoMapper.INSTANCE.toIndiceComparecimentoResponseDto(pacienteDomain);
    }
}
