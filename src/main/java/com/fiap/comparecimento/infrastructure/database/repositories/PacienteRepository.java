package com.fiap.comparecimento.infrastructure.database.repositories;

import com.fiap.comparecimento.infrastructure.database.entities.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<PacienteEntity, Long> {

    Optional<PacienteEntity> getByCns(String cns);
}
