package br.com.mspedidos.application.controller;

import br.com.mspedidos.application.request.PedidoRequestDTO;
import br.com.mspedidos.application.response.PedidoResponseDTO;
import br.com.mspedidos.domain.enums.StatusEnum;
import br.com.mspedidos.domain.service.PedidoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/{idCliente}")
    public ResponseEntity<PedidoResponseDTO> criarPedido(@PathVariable Long idCliente, @RequestBody PedidoRequestDTO pedidoRequestDTO) throws JsonProcessingException {
        return ResponseEntity.ok(pedidoService.criarPedido(idCliente, pedidoRequestDTO));
    }

    @PutMapping("/pagar/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> efetuarPagamentoPedido(@PathVariable String idPedido) {
        return ResponseEntity.ok(pedidoService.efetuarPagamentoPedido(idPedido));
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> buscarPedido(@PathVariable String idPedido) {
        return ResponseEntity.ok(pedidoService.buscarPedido(idPedido));
    }

    @PutMapping("/atualizar-status/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(@PathVariable String idPedido, @RequestParam StatusEnum status) {
        return ResponseEntity.ok(pedidoService.atualizarStatusPedido(idPedido, status));
    }
}
