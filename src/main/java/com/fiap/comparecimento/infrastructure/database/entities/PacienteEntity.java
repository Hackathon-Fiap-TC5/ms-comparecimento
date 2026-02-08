package com.fiap.comparecimento.infrastructure.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "tb_paciente")
public class PacienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cns")
    private String cns;

    private Integer icc;

    private String classificacao;

    @Column(name = "total_comparecimentos")
    private Integer totalComparecimentos;

    @Column(name = "total_faltas")
    private Integer totalFaltas;

    @Column(name = "total_confirmacoes")
    private Integer totalConfirmacoes;

    @Column(name = "total_cancelamentos")
    private Integer totalCancelamentos;

    @Column(name = "total_agendamentos")
    private Integer totalAgendamentos;

    @Column(name = "ultima_atualizacao")
    private OffsetDateTime ultimaAtualizacao;
}
