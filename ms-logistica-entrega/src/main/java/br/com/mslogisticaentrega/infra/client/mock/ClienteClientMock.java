package br.com.mslogisticaentrega.infra.client.mock;

import br.com.mslogisticaentrega.domain.valueObject.ClienteVo;
import br.com.mslogisticaentrega.domain.valueObject.EnderecoVo;
import br.com.mslogisticaentrega.infra.client.ClienteClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClienteClientMock implements ClienteClient {
    @Override
    public ClienteVo obterClientePorId(Long id) {
        List<String> listCep = List.of("08431370", "08456754");

        return new ClienteVo(1L, "Cesar", "22222222222", "333333333", "calvesvecchio@gmail.com",
                new EnderecoVo(listCep.get(id % 2 == 0 ? 1 : 0), "", "", "",
                        "", "", "", 1.0, 3.0));
    }
}
