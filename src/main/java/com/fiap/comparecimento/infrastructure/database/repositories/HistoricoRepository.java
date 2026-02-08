package com.fiap.comparecimento.infrastructure.database.repositories;

import com.fiap.comparecimento.infrastructure.database.entities.HistoricoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoRepository extends JpaRepository<HistoricoEntity, Long> {

    List<HistoricoEntity> findByIdAgendamentoAndCns(Long idAgendamento, String cns);
}
