package com.fiap.comparecimento.infrastructure.database.implementations;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.domain.exception.PacienteNotFoundException;
import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.infrastructure.database.entities.PacienteEntity;
import com.fiap.comparecimento.infrastructure.database.mappers.PacienteEntityMapper;
import com.fiap.comparecimento.infrastructure.database.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public void atualizarInformacoesPaciente(PacienteDomain domain) {
        PacienteEntity entity = PacienteEntityMapper.INSTANCE.toPacienteEntity(domain);
        pacienteRepository.save(entity);
    }
}
