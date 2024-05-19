package br.com.mslogisticaentrega.domain.service;

import br.com.mslogisticaentrega.domain.valueObject.ClienteVo;
import br.com.mslogisticaentrega.infra.client.ClienteClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {
    private final ClienteClient clienteClient;

    public ClienteService(@Qualifier("clienteClientMock") ClienteClient clienteClient) {
        this.clienteClient = clienteClient;
    }

    public ClienteVo obterClientePorId(Long id) {
        ClienteVo clienteVo = clienteClient.obterClientePorId(id);
        return clienteVo;
    }
}
