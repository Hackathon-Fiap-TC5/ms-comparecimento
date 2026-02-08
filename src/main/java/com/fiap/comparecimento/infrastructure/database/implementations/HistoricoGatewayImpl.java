package com.fiap.comparecimento.infrastructure.database.implementations;

import com.fiap.comparecimento.application.gateway.HistoricoGateway;
import com.fiap.comparecimento.domain.model.HistoricoDomain;
import com.fiap.comparecimento.infrastructure.database.entities.HistoricoEntity;
import com.fiap.comparecimento.infrastructure.database.mappers.HistoricoEntityMapper;
import com.fiap.comparecimento.infrastructure.database.repositories.HistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricoGatewayImpl implements HistoricoGateway {

    private final HistoricoRepository historicoRepository;

    @Override
    public List<HistoricoDomain> getHistorico(Long idAgendamento, String cns) {
        return historicoRepository.findByIdAgendamentoAndCns(idAgendamento, cns)
                .stream().map(HistoricoEntityMapper.INSTANCE::toHistoricoDomain)
                .toList();
    }

    @Override
    public void adiciona(HistoricoDomain historicoDomain) {
        HistoricoEntity entity = HistoricoEntityMapper.INSTANCE.toHistoricoEntity(historicoDomain);
        historicoRepository.save(entity);
    }
}
