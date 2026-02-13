package com.fiap.comparecimento.infrastructure.integration.client;

import com.fiap.comparecimento.infrastructure.integration.configuration.FilaVivaClientConfig;
import com.fiap.comparecimento.infrastructure.integration.request.FilaVivaRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "fifa-viva",
        url = "${fila.viva.url}",
        configuration = FilaVivaClientConfig.class
)
public interface FilaVivaClient {

    @PostMapping(consumes = "application/json")
    void enviarPacienteFilaViva(@RequestBody FilaVivaRequest request);
}
