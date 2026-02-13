package com.fiap.comparecimento.infrastructure.integration.mappers;

import com.fiap.comparecimento.domain.model.FilaVivaDomain;
import com.fiap.comparecimento.infrastructure.integration.request.FilaVivaRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FilaVivaRequestMapper {

    FilaVivaRequestMapper INSTANCE = Mappers.getMapper(FilaVivaRequestMapper.class);

    FilaVivaRequest toFilaVivaRequest(FilaVivaDomain filaVivaDomain);
}

