package br.com.mslogisticaentrega.domain.valueObject;

import br.com.mslogisticaentrega.domain.enums.PagamentoEnum;
import br.com.mslogisticaentrega.domain.enums.StatusEnum;

import java.math.BigDecimal;
import java.util.List;

public record PedidoVo(
    String idPedido,
    Long idCliente,
    List<ProdutoVo> produtos,
    BigDecimal valorTotal,
    PagamentoEnum formaPagamento,
    StatusEnum status
){}
