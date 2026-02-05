package com.fiap.comparecimento.entrypoint.controllers.mappers;

import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimentoDomain.gen.model.IndiceComparecimentoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PacienteDtoMapper {

    PacienteDtoMapper INSTANCE = Mappers.getMapper(PacienteDtoMapper.class);

    IndiceComparecimentoResponseDto toIndiceComparecimentoResponseDto(PacienteDomain pacienteDomain);
}
