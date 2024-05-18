package br.com.mspedidos.domain.valueobject;

import java.math.BigDecimal;

public record Produto(
        Integer idProduto,
        String descricao,
        Integer quantidade,
        BigDecimal valor
) {
}
