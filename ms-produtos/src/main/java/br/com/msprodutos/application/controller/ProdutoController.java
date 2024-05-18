package br.com.msprodutos.application.controller;


import br.com.msprodutos.application.request.ProdutoPedidoRequestDTO;
import br.com.msprodutos.application.request.ProdutoRequestDTO;
import br.com.msprodutos.application.response.ConsultaBaixaEstoqueProdutoResponseDTO;
import br.com.msprodutos.application.response.ProdutoResponseDTO;
import br.com.msprodutos.domain.service.ProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrarProdutoIndividualmente(@RequestBody ProdutoRequestDTO produto) {
        return new ResponseEntity<>(produtoService.cadastrarProdutoIndividualmente(produto),
                HttpStatus.CREATED);
    }

    @PostMapping("/lote")
    public ResponseEntity<List<ProdutoResponseDTO>> cadastrarProdutoEmLote(@RequestBody List<ProdutoRequestDTO> produtos) {
        return new ResponseEntity<>(produtoService.cadastrarProdutoEmLote(produtos),
                HttpStatus.CREATED);
    }

    @PostMapping("/consulta-e-da-baixa-estoque")
    public ResponseEntity<List<ConsultaBaixaEstoqueProdutoResponseDTO>> consultaEDaBaixaNoEstoque(@RequestBody List<ProdutoPedidoRequestDTO> produtos) {
        return ResponseEntity.ok(produtoService.consultaEDaBaixaNoEstoque(produtos));
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listaProdutos(@PageableDefault(size = 10, page = 0, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(produtoService.listaProdutos(pageable));
    }

    @PutMapping("/{idProduto}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Integer idProduto, @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        return ResponseEntity.ok(produtoService.atualizarProduto(idProduto, produtoRequestDTO));
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        produtoService.uploadProdutoFile(file);
        return "Processamento iniciado";
    }

}
