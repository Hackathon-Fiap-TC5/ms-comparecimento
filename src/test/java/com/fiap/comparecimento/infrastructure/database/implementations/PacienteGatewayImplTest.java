package com.fiap.comparecimento.infrastructure.database.implementations;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fiap.comparecimento.domain.exception.PacienteNotFoundException;
import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.domain.model.RelatorioAbsenteismoDomain;
import com.fiap.comparecimento.infrastructure.database.entities.PacienteEntity;
import com.fiap.comparecimento.infrastructure.database.projection.RelatorioAbsenteismoProjection;
import com.fiap.comparecimento.infrastructure.database.repositories.PacienteRepository;

@ExtendWith(MockitoExtension.class)
class PacienteGatewayImplTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteGatewayImpl gateway;

    private PacienteEntity pacienteEntity;
    private PacienteDomain pacienteDomain;
    private RelatorioAbsenteismoProjection projection;

    @BeforeEach
    void setUp() {
        pacienteEntity = new PacienteEntity();
        pacienteEntity.setCns("123456789012345");
        pacienteEntity.setIcc(85);
        pacienteEntity.setClassificacao("CONFIAVEL");
        pacienteEntity.setTotalComparecimentos(17);
        pacienteEntity.setTotalFaltas(3);
        pacienteEntity.setTotalConfirmacoes(15);
        pacienteEntity.setTotalCancelamentos(2);
        pacienteEntity.setTotalAgendamentos(20);
        pacienteEntity.setUltimaAtualizacao(OffsetDateTime.now());

        pacienteDomain = new PacienteDomain();
        pacienteDomain.setCns("123456789012345");
        pacienteDomain.setIcc(85);
        pacienteDomain.setClassificacao("CONFIAVEL");
        pacienteDomain.setTotalComparecimentos(17);
        pacienteDomain.setTotalFaltas(3);
        pacienteDomain.setTotalConfirmacoes(15);
        pacienteDomain.setTotalCancelamentos(2);
        pacienteDomain.setTotalAgendamentos(20);
        pacienteDomain.setUltimaAtualizacao(OffsetDateTime.now());
    }

    @Test
    void deveConsultarPacientePorCnsComSucesso() {
        // Given
        String cns = "123456789012345";
        when(pacienteRepository.getByCns(cns)).thenReturn(Optional.of(pacienteEntity));

        // When
        PacienteDomain result = gateway.consultar(cns);

        // Then
        assertNotNull(result);
        assertEquals(cns, result.getCns());
        assertEquals(85, result.getIcc());
        assertEquals("CONFIAVEL", result.getClassificacao());
        verify(pacienteRepository, times(1)).getByCns(cns);
    }

    @Test
    void deveLancarExcecaoQuandoPacienteNaoEncontrado() {
        // Given
        String cns = "999999999999999";
        when(pacienteRepository.getByCns(cns)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PacienteNotFoundException.class, () -> gateway.consultar(cns));
        verify(pacienteRepository).getByCns(cns);
    }

    @Test
    void deveAtualizarInformacoesPaciente() {
        // Given
        when(pacienteRepository.save(any(PacienteEntity.class))).thenReturn(pacienteEntity);

        // When
        gateway.atualizarInformacoesPaciente(pacienteDomain);

        // Then
        verify(pacienteRepository, times(1)).save(any(PacienteEntity.class));
    }

    @Test
    void deveConsultarRelatorioAbsenteismo() {
        // Given
        OffsetDateTime dataInicio = OffsetDateTime.now().minusDays(30);
        OffsetDateTime dataFim = OffsetDateTime.now();

        projection = new RelatorioAbsenteismoProjection() {
            @Override
            public Integer getTotalPessoas() {
                return 100;
            }

            @Override
            public BigDecimal getIccMedio() {
                return new BigDecimal("75.50");
            }

            @Override
            public Integer getTotalConsultas() {
                return 500;
            }

            @Override
            public Integer getTotalFaltas() {
                return 50;
            }

            @Override
            public BigDecimal getTaxaAbsenteismo() {
                return new BigDecimal("10.00");
            }
        };

        when(pacienteRepository.consultarRelatorioAbsenteismo(dataInicio, dataFim)).thenReturn(projection);

        // When
        RelatorioAbsenteismoDomain result = gateway.consultarRelatorioAbsenteismo(dataInicio, dataFim);

        // Then
        assertNotNull(result);
        assertEquals(100, result.getTotalPessoas());
        assertEquals(new BigDecimal("75.50"), result.getIccMedio());
        assertEquals(500, result.getTotalConsultas());
        assertEquals(50, result.getTotalFaltas());
        assertEquals(new BigDecimal("10.00"), result.getTaxaAbsenteismo());
        assertNotNull(result.getPeriodo());
        assertNotNull(result.getDataGeracao());
        verify(pacienteRepository).consultarRelatorioAbsenteismo(dataInicio, dataFim);
    }

    @Test
    void deveConstruirRelatorioComPeriodoCorreto() {
        // Given
        OffsetDateTime dataInicio = OffsetDateTime.now().minusDays(7);
        OffsetDateTime dataFim = OffsetDateTime.now();

        projection = new RelatorioAbsenteismoProjection() {
            @Override
            public Integer getTotalPessoas() {
                return 50;
            }

            @Override
            public BigDecimal getIccMedio() {
                return new BigDecimal("80.00");
            }

            @Override
            public Integer getTotalConsultas() {
                return 200;
            }

            @Override
            public Integer getTotalFaltas() {
                return 20;
            }

            @Override
            public BigDecimal getTaxaAbsenteismo() {
                return new BigDecimal("10.00");
            }
        };

        when(pacienteRepository.consultarRelatorioAbsenteismo(dataInicio, dataFim)).thenReturn(projection);

        // When
        RelatorioAbsenteismoDomain result = gateway.consultarRelatorioAbsenteismo(dataInicio, dataFim);

        // Then
        assertNotNull(result.getPeriodo());
        assertEquals(dataInicio, result.getPeriodo().getDataInicio());
        assertEquals(dataFim, result.getPeriodo().getDataFim());
    }

    @Test
    void deveAjustarEscalasNoRelatorio() {
        // Given
        OffsetDateTime dataInicio = OffsetDateTime.now().minusDays(30);
        OffsetDateTime dataFim = OffsetDateTime.now();

        projection = new RelatorioAbsenteismoProjection() {
            @Override
            public Integer getTotalPessoas() {
                return 100;
            }

            @Override
            public BigDecimal getIccMedio() {
                return new BigDecimal("75.555555");
            }

            @Override
            public Integer getTotalConsultas() {
                return 500;
            }

            @Override
            public Integer getTotalFaltas() {
                return 50;
            }

            @Override
            public BigDecimal getTaxaAbsenteismo() {
                return new BigDecimal("10.123456");
            }
        };

        when(pacienteRepository.consultarRelatorioAbsenteismo(dataInicio, dataFim)).thenReturn(projection);

        // When
        RelatorioAbsenteismoDomain result = gateway.consultarRelatorioAbsenteismo(dataInicio, dataFim);

        // Then
        assertNotNull(result.getIccMedio());
        assertEquals(2, result.getIccMedio().scale());
        assertNotNull(result.getTaxaAbsenteismo());
        assertEquals(2, result.getTaxaAbsenteismo().scale());
    }

    @Test
    void deveConsultarRelatorioComValoresNulos() {
        // Given
        OffsetDateTime dataInicio = OffsetDateTime.now().minusDays(30);
        OffsetDateTime dataFim = OffsetDateTime.now();

        projection = new RelatorioAbsenteismoProjection() {
            @Override
            public Integer getTotalPessoas() {
                return 0;
            }

            @Override
            public BigDecimal getIccMedio() {
                return null;
            }

            @Override
            public Integer getTotalConsultas() {
                return 0;
            }

            @Override
            public Integer getTotalFaltas() {
                return 0;
            }

            @Override
            public BigDecimal getTaxaAbsenteismo() {
                return null;
            }
        };

        when(pacienteRepository.consultarRelatorioAbsenteismo(dataInicio, dataFim)).thenReturn(projection);

        // When
        RelatorioAbsenteismoDomain result = gateway.consultarRelatorioAbsenteismo(dataInicio, dataFim);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalPessoas());
        assertNull(result.getIccMedio());
        assertEquals(0, result.getTotalConsultas());
        assertEquals(0, result.getTotalFaltas());
        assertNull(result.getTaxaAbsenteismo());
    }
}
