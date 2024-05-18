package br.com.msprodutos.application.request;

import java.math.BigDecimal;

public record ProdutoRequestDTO(
        String descricao,
        Integer quantidadeEstoque,
        BigDecimal valor
) {
}
