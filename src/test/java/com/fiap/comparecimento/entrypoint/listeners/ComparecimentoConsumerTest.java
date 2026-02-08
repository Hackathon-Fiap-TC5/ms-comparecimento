package com.fiap.comparecimento.entrypoint.listeners;

import com.fiap.comparecimento.application.usecase.calcula.comparecimento.CalculaComparecimentoUseCase;
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
    private CalculaComparecimentoUseCase calculaComparecimentoUseCase;

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
        doNothing().when(calculaComparecimentoUseCase).calculaComparecimento(any(EventoAgendamentoMessageDomain.class));

        // When
        consumer.consumer(dto);

        // Then
        verify(calculaComparecimentoUseCase, times(1)).calculaComparecimento(any(EventoAgendamentoMessageDomain.class));
    }

    @Test
    void deveConverterDtoParaDomain() {
        // Given
        doNothing().when(calculaComparecimentoUseCase).calculaComparecimento(any(EventoAgendamentoMessageDomain.class));

        // When
        consumer.consumer(dto);

        // Then
        verify(calculaComparecimentoUseCase).calculaComparecimento(any(EventoAgendamentoMessageDomain.class));
    }

    @Test
    void deveProcessarDiferentesStatusConsulta() {
        // Given
        dto.setStatusConsulta("FALTA");
        dto.setStatusNotificacao("EXPIRADA");
        doNothing().when(calculaComparecimentoUseCase).calculaComparecimento(any(EventoAgendamentoMessageDomain.class));

        // When
        consumer.consumer(dto);

        // Then
        verify(calculaComparecimentoUseCase).calculaComparecimento(any(EventoAgendamentoMessageDomain.class));
    }

    @Test
    void deveProcessarDiferentesStatusNotificacao() {
        // Given
        dto.setStatusConsulta("CONFIRMADO");
        dto.setStatusNotificacao("CONFIRMOU_48H_ANTECEDENCIA");
        doNothing().when(calculaComparecimentoUseCase).calculaComparecimento(any(EventoAgendamentoMessageDomain.class));

        // When
        consumer.consumer(dto);

        // Then
        verify(calculaComparecimentoUseCase).calculaComparecimento(any(EventoAgendamentoMessageDomain.class));
    }
}
