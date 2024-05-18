package br.com.mspedidos.interfaces.produto.response;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Integer id,
        String descricao,
        Integer quantidade,
        BigDecimal valor
) {
}
