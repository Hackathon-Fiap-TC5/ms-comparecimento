package com.fiap.comparecimento.entrypoint.producer.mappers;

import com.fiap.comparecimento.domain.model.EventoComparecimentoMessageDomain;
import com.fiap.comparecimentoDomain.gen.model.EventoComparecimentoMessageDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComparecimentoProducerMapperTest {

    @Test
    void deveMapearDomainParaDto() {
        EventoComparecimentoMessageDomain domain = new EventoComparecimentoMessageDomain(
                1L,
                "123456789012345",
                "MONITORAR",
                85,
                "Justificativa teste"
        );

        EventoComparecimentoMessageDto dto = ComparecimentoProducerMapper.INSTANCE.toEventoComparecimentoMessageDto(domain);

        assertEquals(1L, dto.getIdAgendamento());
        assertEquals("123456789012345", dto.getCns());
        assertEquals("MONITORAR", dto.getSugestaoConduta());
        assertEquals(85, dto.getIccScore());
        assertEquals("Justificativa teste", dto.getJustificativa());
    }

    @Test
    void deveMapearDtoParaDomain() {
        EventoComparecimentoMessageDto dto = new EventoComparecimentoMessageDto();
        dto.setIdAgendamento(2L);
        dto.setCns("987654321098765");
        dto.setSugestaoConduta("REALOCACAO_IMEDIATA");
        dto.setIccScore(25);
        dto.setJustificativa("Paciente critico");

        EventoComparecimentoMessageDomain domain = ComparecimentoProducerMapper.INSTANCE.toEventoComparecimentoMessageDomain(dto);

        assertEquals(2L, domain.getIdAgendamento());
        assertEquals("987654321098765", domain.getCns());
        assertEquals("REALOCACAO_IMEDIATA", domain.getSugestaoConduta());
        assertEquals(25, domain.getIccScore());
        assertEquals("Paciente critico", domain.getJustificativa());
    }
}
