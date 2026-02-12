package com.fiap.comparecimento.entrypoint.producer;

import com.fiap.comparecimento.infrastructure.producer.ComparecimentoProducer;
import com.fiap.comparecimentoDomain.gen.model.EventoComparecimentoMessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ComparecimentoProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ComparecimentoProducer producer;

    @Test
    void deveEnviarSugestoes() {
        EventoComparecimentoMessageDto dto = new EventoComparecimentoMessageDto();
        dto.setCns("123456789012345");
        dto.setIdAgendamento(1L);

        producer.sendSugestions(dto);

        ArgumentCaptor<EventoComparecimentoMessageDto> captor =
                ArgumentCaptor.forClass(EventoComparecimentoMessageDto.class);
        verify(rabbitTemplate).convertAndSend(
                eq("agendamento.exchange"),
                eq("agendamento.rollback"),
                captor.capture());
        assertEquals("123456789012345", captor.getValue().getCns());
        assertEquals(1L, captor.getValue().getIdAgendamento());
    }
}
