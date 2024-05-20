package br.com.mslogisticaentrega.domain.valueObject;

import java.math.BigDecimal;

public record ProdutoVo(
        Integer idProduto,
        String descricao,
        BigDecimal valor
){
}
