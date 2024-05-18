package br.com.mspedidos.interfaces.produto;

import br.com.mspedidos.application.request.ProdutoPedidoRequestDTO;
import br.com.mspedidos.interfaces.produto.response.ProdutoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "produto", url="http://localhost:8082/produtos")
public interface ProdutoInterface {

    @PostMapping("/consulta-e-da-baixa-estoque")
    List<ProdutoResponseDTO> consultaEDaBaixaNoEstoque(@RequestBody List<ProdutoPedidoRequestDTO> produtos);
}
