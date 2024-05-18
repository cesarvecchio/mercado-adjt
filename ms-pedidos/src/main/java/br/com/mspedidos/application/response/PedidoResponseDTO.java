package br.com.mspedidos.application.response;

import br.com.mspedidos.domain.enums.PagamentoEnum;
import br.com.mspedidos.domain.enums.StatusEnum;
import br.com.mspedidos.domain.valueobject.Produto;

import java.math.BigDecimal;
import java.util.List;

public record PedidoResponseDTO(
        String idPedido,
        List<Produto> produtos,
        BigDecimal valorTotal,
        PagamentoEnum formaPagamento,
        StatusEnum status
) {
}
