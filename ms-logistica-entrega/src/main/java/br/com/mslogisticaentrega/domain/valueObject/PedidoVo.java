package br.com.mslogisticaentrega.domain.valueObject;

import br.com.mslogisticaentrega.domain.enums.PagamentoEnum;
import br.com.mslogisticaentrega.domain.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoVo {
    private String idPedido;
    private Integer idCliente;
    private List<ProdutoVo> produtos;
    private BigDecimal valorTotal;
    private PagamentoEnum formaPagamento;
    private StatusEnum status;
}
