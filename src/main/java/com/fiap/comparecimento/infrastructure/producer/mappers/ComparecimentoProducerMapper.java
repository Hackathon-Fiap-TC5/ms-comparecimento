package com.fiap.comparecimento.infrastructure.producer.mappers;

import com.fiap.comparecimento.domain.model.EventoComparecimentoMessageDomain;
import com.fiap.comparecimentoDomain.gen.model.EventoComparecimentoMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ComparecimentoProducerMapper {

    ComparecimentoProducerMapper INSTANCE = Mappers.getMapper(ComparecimentoProducerMapper.class);

    EventoComparecimentoMessageDomain toEventoComparecimentoMessageDomain(EventoComparecimentoMessageDto dto);

    EventoComparecimentoMessageDto toEventoComparecimentoMessageDto(EventoComparecimentoMessageDomain domain);
}
