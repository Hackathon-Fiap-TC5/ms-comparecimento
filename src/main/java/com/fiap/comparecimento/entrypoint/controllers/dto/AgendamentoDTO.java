package com.fiap.comparecimento.entrypoint.controllers.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AgendamentoDTO {

    private Long idAgendamento;
    private String cns;
    private String statusConsulta;
    private String statusNotificacao;
    private Date dataEvento;
    private Date dataAgendamento;
    private String origem;
}
