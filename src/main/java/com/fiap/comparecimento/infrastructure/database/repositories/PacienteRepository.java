package com.fiap.comparecimento.infrastructure.database.repositories;

import com.fiap.comparecimento.infrastructure.database.entities.PacienteEntity;
import com.fiap.comparecimento.infrastructure.database.projection.RelatorioAbsenteismoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<PacienteEntity, Long> {

    Optional<PacienteEntity> getByCns(String cns);

    @Query(value = """
        SELECT
            COUNT(*) AS total_pessoas,
            COALESCE(AVG(icc), 0) AS icc_medio,
            COALESCE(SUM(total_agendamentos), 0) AS total_consultas,
            COALESCE(SUM(total_faltas), 0) AS total_faltas,
            COALESCE(
                (SUM(total_faltas) * 1.0 / NULLIF(SUM(total_agendamentos), 0)) * 100,
                0
            ) AS taxa_absenteismo
        FROM tb_paciente
        WHERE ultima_atualizacao >= :dataInicio
          AND ultima_atualizacao <  :dataFim
    """, nativeQuery = true)
    RelatorioAbsenteismoProjection consultarRelatorioAbsenteismo(
            @Param("dataInicio") OffsetDateTime dataInicio,
            @Param("dataFim") OffsetDateTime dataFim
    );
}
