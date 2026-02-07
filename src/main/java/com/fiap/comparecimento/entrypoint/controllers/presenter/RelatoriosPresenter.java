package com.fiap.comparecimento.entrypoint.controllers.presenter;

import com.fiap.comparecimento.domain.model.RelatorioAbsenteismoDomain;
import com.fiap.comparecimento.entrypoint.controllers.mappers.RelatoriosMapper;
import com.fiap.comparecimentoDomain.gen.model.RelatorioAbsenteismoResponseDto;

public class RelatoriosPresenter {

    public static RelatorioAbsenteismoResponseDto toRelatorioAbsenteismoResponseDto(RelatorioAbsenteismoDomain domain) {
        return RelatoriosMapper.INSTANCE.toRelatorioAbsenteismoResponseDto(domain);
    }
}