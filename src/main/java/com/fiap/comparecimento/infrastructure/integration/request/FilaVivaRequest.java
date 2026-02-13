package com.fiap.comparecimento.infrastructure.integration.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class FilaVivaRequest {

    private Long idAgendamento;
    private String cns;
    private String sugestaoConduta;
    private String perfilPaciente;
    private String justificativa;

}
