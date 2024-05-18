package br.com.msprodutos.application.response;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Integer id,
        String descricao,
        Integer quantidadeEstoque,
        BigDecimal valor
) {
}
