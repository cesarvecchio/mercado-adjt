package br.com.mspedidos.application.request;

public record ProdutoPedidoRequestDTO(
        Integer idProduto,
        Integer quantidade
) {
}
