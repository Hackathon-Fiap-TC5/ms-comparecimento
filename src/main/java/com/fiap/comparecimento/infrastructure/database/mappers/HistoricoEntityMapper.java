package com.fiap.comparecimento.infrastructure.database.mappers;

import com.fiap.comparecimento.domain.model.HistoricoDomain;
import com.fiap.comparecimento.infrastructure.database.entities.HistoricoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HistoricoEntityMapper {

    HistoricoEntityMapper INSTANCE = Mappers.getMapper(HistoricoEntityMapper.class);

    HistoricoEntity toHistoricoEntity(HistoricoDomain historicoDomain);

    HistoricoDomain toHistoricoDomain(HistoricoEntity historicoEntity);
}
