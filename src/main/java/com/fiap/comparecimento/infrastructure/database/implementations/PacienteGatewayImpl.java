package com.fiap.comparecimento.infrastructure.database.implementations;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.domain.exception.PacienteNotFoundException;
import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.domain.model.PeriodoDomain;
import com.fiap.comparecimento.domain.model.RelatorioAbsenteismoDomain;
import com.fiap.comparecimento.infrastructure.database.entities.PacienteEntity;
import com.fiap.comparecimento.infrastructure.database.mappers.PacienteEntityMapper;
import com.fiap.comparecimento.infrastructure.database.projection.RelatorioAbsenteismoProjection;
import com.fiap.comparecimento.infrastructure.database.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PacienteGatewayImpl implements PacienteGateway {

    private final PacienteRepository pacienteRepository;

    @Override
    public PacienteDomain consultar(String cns) {
        return pacienteRepository.getByCns(cns)
                .map(PacienteEntityMapper.INSTANCE::toPacienteDomain)
                .orElseThrow(PacienteNotFoundException::new);
    }

    @Override
    public Optional<PacienteDomain> verificaExistenciaPaciente(String cns) {
        return pacienteRepository.getByCns(cns)
                .map(PacienteEntityMapper.INSTANCE::toPacienteDomain);
    }

    @Override
    public void criaOuAtualizarInformacoesPaciente(PacienteDomain domain) {
        PacienteEntity entity = PacienteEntityMapper.INSTANCE.toPacienteEntity(domain);
        pacienteRepository.save(entity);
    }

    @Override
    public RelatorioAbsenteismoDomain consultarRelatorioAbsenteismo(OffsetDateTime dataInicio, OffsetDateTime dataFim) {
        RelatorioAbsenteismoProjection projection = pacienteRepository.consultarRelatorioAbsenteismo(dataInicio, dataFim);
        return buildRelatorioAbsenteismoDomain(dataInicio, dataFim, projection);
    }

    private static RelatorioAbsenteismoDomain buildRelatorioAbsenteismoDomain(OffsetDateTime dataInicio, OffsetDateTime dataFim, RelatorioAbsenteismoProjection p) {
        RelatorioAbsenteismoDomain domain = new RelatorioAbsenteismoDomain();
        PeriodoDomain periodo = buildPeriodoDomain(dataInicio, dataFim);

        domain.setPeriodo(periodo);
        domain.setTotalPessoas(p.getTotalPessoas());
        domain.setIccMedio(p.getIccMedio());
        domain.setTotalConsultas(p.getTotalConsultas());
        domain.setTotalFaltas(p.getTotalFaltas());
        domain.setTaxaAbsenteismo(p.getTaxaAbsenteismo());
        domain.setDataGeracao(OffsetDateTime.now());

        domain.ajustarEscalas();
        return domain;
    }

    private static PeriodoDomain buildPeriodoDomain(OffsetDateTime dataInicio, OffsetDateTime dataFim) {
        PeriodoDomain periodo = new PeriodoDomain();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        return periodo;
    }
}
