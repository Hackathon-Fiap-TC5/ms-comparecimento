package com.fiap.comparecimento.entrypoint.controllers.mappers;

import com.fiap.comparecimento.domain.model.RelatorioAbsenteismoDomain;
import com.fiap.comparecimentoDomain.gen.model.RelatorioAbsenteismoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)

public interface RelatoriosMapper {

    RelatoriosMapper INSTANCE = Mappers.getMapper(RelatoriosMapper.class);

    @Mapping(source = "periodo", target = "periodo")
    RelatorioAbsenteismoResponseDto toRelatorioAbsenteismoResponseDto(RelatorioAbsenteismoDomain domain);

    default LocalDate toLocalDate(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }
}
