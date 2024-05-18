package br.com.msprodutos.application.response;

import java.math.BigDecimal;

public record ConsultaBaixaEstoqueProdutoResponseDTO(
        Integer id,
        String descricao,
        Integer quantidade,
        BigDecimal valor
) {
}
