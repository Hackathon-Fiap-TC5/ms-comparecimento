package com.fiap.comparecimento.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "tb_historico")
public class HistoricoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgendamento;

    @Column(name = "cns")
    private String cns;

    @Column(name = "status_consulta")
    private String statusConsulta;

    @Column(name = "status_notificacao")
    private String statusNotificacao;

    @Column(name = "data_evento")
    private OffsetDateTime dataEvento;
}
