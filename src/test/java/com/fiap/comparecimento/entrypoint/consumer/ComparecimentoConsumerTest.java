package com.fiap.comparecimento.entrypoint.consumer;

import com.fiap.comparecimento.application.usecase.calcula.comparecimento.ProcessarComparecimentoUseCase;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimentoDomain.gen.model.EventoAgendamentoMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComparecimentoConsumerTest {

    @Mock
    private ProcessarComparecimentoUseCase processarComparecimentoUseCase;

    @InjectMocks
    private ComparecimentoConsumer consumer;

    private EventoAgendamentoMessageDto dto;

    @BeforeEach
    void setUp() {
        dto = new EventoAgendamentoMessageDto();
        dto.setCns("123456789012345");
        dto.setStatusConsulta("REALIZADO");
        dto.setStatusNotificacao("ENTREGUE");
    }

    @Test
    void deveProcessarMensagemComSucesso() {
        // Given
        doNothing().when(processarComparecimentoUseCase).processaComparecimento(any(EventoAgendamentoMessageDomain.class));

        // When
        consumer.consumer(dto);

        // Then
        verify(processarComparecimentoUseCase, times(1)).processaComparecimento(any(EventoAgendamentoMessageDomain.class));
    }

    @Test
    void deveConverterDtoParaDomain() {
        // Given
        doNothing().when(processarComparecimentoUseCase).processaComparecimento(any(EventoAgendamentoMessageDomain.class));

        // When
        consumer.consumer(dto);

        // Then
        verify(processarComparecimentoUseCase).processaComparecimento(any(EventoAgendamentoMessageDomain.class));
    }

    @Test
    void deveProcessarDiferentesStatusConsulta() {
        // Given
        dto.setStatusConsulta("FALTA");
        dto.setStatusNotificacao("EXPIRADA");
        doNothing().when(processarComparecimentoUseCase).processaComparecimento(any(EventoAgendamentoMessageDomain.class));

        // When
        consumer.consumer(dto);

        // Then
        verify(processarComparecimentoUseCase).processaComparecimento(any(EventoAgendamentoMessageDomain.class));
    }

    @Test
    void deveProcessarDiferentesStatusNotificacao() {
        // Given
        dto.setStatusConsulta("CONFIRMADO");
        dto.setStatusNotificacao("CONFIRMOU_48H_ANTECEDENCIA");
        doNothing().when(processarComparecimentoUseCase).processaComparecimento(any(EventoAgendamentoMessageDomain.class));

        // When
        consumer.consumer(dto);

        // Then
        verify(processarComparecimentoUseCase).processaComparecimento(any(EventoAgendamentoMessageDomain.class));
    }
}
