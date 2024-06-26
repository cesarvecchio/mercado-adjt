package br.com.mslogisticaentrega.infra.client;

import br.com.mslogisticaentrega.domain.valueObject.ClienteVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-cadastrocliente", path = "/cliente")
public interface ClienteClient {

    @GetMapping("/buscar/{id}")
    public ClienteVo obterClientePorId(@PathVariable Long id);
}
