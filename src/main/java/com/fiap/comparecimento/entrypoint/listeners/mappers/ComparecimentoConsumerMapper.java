package com.fiap.comparecimento.entrypoint.listeners.mappers;

import com.fiap.comparecimento.domain.enuns.StatusConsultaEnum;
import com.fiap.comparecimento.domain.enuns.StatusNotificacaoEnum;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimentoDomain.gen.model.EventoAgendamentoMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ComparecimentoConsumerMapper {

    ComparecimentoConsumerMapper INSTANCE = Mappers.getMapper(ComparecimentoConsumerMapper.class);

    @Mapping(target = "statusConsulta", source = "statusConsulta", qualifiedByName = "mapStatusConsulta")
    @Mapping(target = "statusNotificacao", source = "statusNotificacao", qualifiedByName = "mapStatusNotificacao")
    EventoAgendamentoMessageDomain toEventoAgendamentoMessageDomain(EventoAgendamentoMessageDto dto);

    @Named("mapStatusConsulta")
    default StatusConsultaEnum mapStatusConsulta(String value) {
        return StatusConsultaEnum.fromString(value);
    }

    @Named("mapStatusNotificacao")
    default StatusNotificacaoEnum mapStatusNotificacao(String value) {
        return StatusNotificacaoEnum.fromString(value);
    }
}
