package com.fiap.comparecimento.infrastructure.database.mappers;

import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.infrastructure.database.entities.PacienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PacienteEntityMapper {

    PacienteEntityMapper INSTANCE = Mappers.getMapper(PacienteEntityMapper.class);

    PacienteEntity toPacienteEntity(PacienteDomain pacienteDomain);

    PacienteDomain toPacienteDomain(PacienteEntity pacienteEntity);

}
