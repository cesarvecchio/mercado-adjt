package br.com.mspedidos.application.request;

import br.com.mspedidos.domain.enums.PagamentoEnum;

import java.util.List;

public record PedidoRequestDTO(
    List<ProdutoPedidoRequestDTO> produtos,
    PagamentoEnum formaPagamento
) {
}
