package br.com.mslogisticaentrega.application.controller;

import br.com.mslogisticaentrega.domain.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/entregas")
public class EntregaController {
    private final PedidoService pedidoService;

    public EntregaController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PutMapping("/{idPedido}")
    public ResponseEntity<Void> atualizarPedidoEntregue(@PathVariable String idPedido){

        pedidoService.atualizarPedidoEntregue(idPedido);

        return ResponseEntity.noContent().build();
    }
}
