package com.fiap.comparecimento.infrastructure.integration.implementations;

import com.fiap.comparecimento.application.gateway.FilaVivaGateway;
import com.fiap.comparecimento.domain.model.FilaVivaDomain;
import com.fiap.comparecimento.infrastructure.integration.client.FilaVivaClient;
import com.fiap.comparecimento.infrastructure.integration.mappers.FilaVivaRequestMapper;
import com.fiap.comparecimento.infrastructure.integration.request.FilaVivaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FilaVivaGatewayImpl implements FilaVivaGateway {

    private final FilaVivaClient filaVivaClient;

    @Override
    public void publicarFilaViva(FilaVivaDomain filaVivaDomain) {
        FilaVivaRequest request = FilaVivaRequestMapper.INSTANCE.toFilaVivaRequest(filaVivaDomain);
        filaVivaClient.enviarPacienteFilaViva(request);
    }
}
