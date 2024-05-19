package br.com.mslogisticaentrega.infra.client;

import br.com.mslogisticaentrega.domain.enums.StatusEnum;
import br.com.mslogisticaentrega.domain.valueObject.AtualizarStatusLoteRequestVo;
import br.com.mslogisticaentrega.domain.valueObject.PedidoVo;
import br.com.mslogisticaentrega.infra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-pedidos", configuration = FeignConfig.class, path = "/pedidos")
public interface PedidoClient {
    @GetMapping("/pagos")
    List<PedidoVo> buscarPedidosPagos();

    @GetMapping("/{idPedido}")
    PedidoVo buscarPedido(@PathVariable String idPedido);

    @PutMapping("/atualizar-status-em-lote")
    void atualizarPedidosAguardandoEntrega(@RequestBody AtualizarStatusLoteRequestVo atualizarStatusLoteRequestDTO);

    @PutMapping("/atualizar-status/{idPedido}")
    void atualizarPedidoEntregue(@PathVariable String idPedido, @RequestParam StatusEnum status);
}
