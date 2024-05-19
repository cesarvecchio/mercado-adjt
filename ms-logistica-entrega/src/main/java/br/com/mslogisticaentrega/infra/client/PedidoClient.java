package br.com.mslogisticaentrega.infra.client;

import br.com.mslogisticaentrega.domain.valueObject.PedidoVo;
import br.com.mslogisticaentrega.infra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "ms-pedidos", configuration = FeignConfig.class)
public interface PedidoClient {
    @GetMapping
    List<PedidoVo> buscarPedidoPago();

    @PutMapping("/{idPedidoList}")
    void atualizarPedidosAguardandoEntrega(@PathVariable List<String> idPedidoList);

    @PutMapping("/{idPedido}")
    void atualizarPedidoEntregue(@PathVariable String idPedido);
}
