package br.com.msprodutos.domain.service;

import br.com.msprodutos.application.controller.exceptions.EstoqueException;
import br.com.msprodutos.application.controller.exceptions.NaoEncontradoException;
import br.com.msprodutos.application.request.ProdutoPedidoRequestDTO;
import br.com.msprodutos.application.request.ProdutoRequestDTO;
import br.com.msprodutos.application.response.ConsultaBaixaEstoqueProdutoResponseDTO;
import br.com.msprodutos.application.response.ProdutoResponseDTO;
import br.com.msprodutos.domain.enitity.Produto;
import br.com.msprodutos.repository.ProdutoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProdutoServiceIT {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoService produtoService;

    @BeforeEach
    void populaTabelaProduto() {
        List<Produto> produtos = List.of(
                new Produto("Produto um", 10, BigDecimal.valueOf(149.99)),
                new Produto("Produto dois", 27, BigDecimal.valueOf(1200)),
                new Produto("Produto três", 2, BigDecimal.valueOf(2538.50)),
                new Produto("Produto quatro", 1, BigDecimal.valueOf(10)),
                new Produto("Produto cinco", 7, BigDecimal.valueOf(1.99))
        );
        produtoRepository.saveAll(produtos);
    }

    @AfterEach
    void dropTabelaProduto() {
        produtoRepository.deleteAll();
    }

    @Nested
    class CadastrarProduto {

        @Test
        void deveCadastrarProdutoIndividualmente() {
            ProdutoRequestDTO produtoRequestDTO = montaRequestProdutoDTO();

            ProdutoResponseDTO produtoCadastrado = produtoService.cadastrarProdutoIndividualmente(produtoRequestDTO);

            assertThat(produtoCadastrado)
                    .isNotNull()
                    .isInstanceOf(ProdutoResponseDTO.class);
            assertThat(produtoCadastrado.id())
                    .isNotNull();
            assertThat(produtoCadastrado.descricao())
                    .isNotNull()
                    .isEqualTo(produtoRequestDTO.descricao());
            assertThat(produtoCadastrado.quantidadeEstoque())
                    .isNotNull()
                    .isEqualTo(produtoRequestDTO.quantidadeEstoque());
            assertThat(produtoCadastrado.valor())
                    .isNotNull()
                    .isEqualTo(produtoRequestDTO.valor());
        }

        @Test
        void deveCadastrarProdutoEmLote() {
            List<ProdutoRequestDTO> produtosRequest = montaListRequestProdutoDTO();
            ProdutoRequestDTO primeiroProdutoRequest = produtosRequest.get(0);
            ProdutoRequestDTO segundoProdutoRequest = produtosRequest.get(1);

            List<ProdutoResponseDTO> produtosCadastrados = produtoService.cadastrarProdutoEmLote(produtosRequest);
            ProdutoResponseDTO primeiroProdutoCadastrado = produtosCadastrados.get(0);
            ProdutoResponseDTO segundoProdutoCadastrado = produtosCadastrados.get(1);

            assertThat(produtosCadastrados)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(2);

            assertThat(primeiroProdutoCadastrado)
                    .isNotNull()
                    .isInstanceOf(ProdutoResponseDTO.class);
            assertThat(primeiroProdutoCadastrado.id())
                    .isNotNull();
            assertThat(primeiroProdutoCadastrado.descricao())
                    .isNotNull()
                    .isEqualTo(primeiroProdutoRequest.descricao());
            assertThat(primeiroProdutoCadastrado.quantidadeEstoque())
                    .isNotNull()
                    .isEqualTo(primeiroProdutoRequest.quantidadeEstoque());
            assertThat(primeiroProdutoCadastrado.valor())
                    .isNotNull()
                    .isEqualTo(primeiroProdutoRequest.valor());

            assertThat(segundoProdutoCadastrado)
                    .isNotNull()
                    .isInstanceOf(ProdutoResponseDTO.class);
            assertThat(segundoProdutoCadastrado.id())
                    .isNotNull();
            assertThat(segundoProdutoCadastrado.descricao())
                    .isNotNull()
                    .isEqualTo(segundoProdutoRequest.descricao());
            assertThat(segundoProdutoCadastrado.quantidadeEstoque())
                    .isNotNull()
                    .isEqualTo(segundoProdutoRequest.quantidadeEstoque());
            assertThat(segundoProdutoCadastrado.valor())
                    .isNotNull()
                    .isEqualTo(segundoProdutoRequest.valor());
        }
    }

    @Nested
    class ConsultaEDaBaixaNoEstoque {

        @Test
        void consultaEDaBaixaNoEstoque() {
            ProdutoPedidoRequestDTO produtoPedidoRequestUm = new ProdutoPedidoRequestDTO(1, 1);
            ProdutoPedidoRequestDTO produtoPedidoRequestDois = new ProdutoPedidoRequestDTO(2, 2);

            List<ProdutoPedidoRequestDTO> produtoPedidoRequest = List.of(produtoPedidoRequestUm,
                    produtoPedidoRequestDois
            );

            List<ConsultaBaixaEstoqueProdutoResponseDTO> consultaBaixaEstoqueProdutoResponseDTO = produtoService.consultaEDaBaixaNoEstoque(produtoPedidoRequest);
            List<Produto> produtosAtualizados = produtoRepository.findAllById(List.of(produtoPedidoRequestUm.idProduto(), produtoPedidoRequestDois.idProduto()));

            assertThat(consultaBaixaEstoqueProdutoResponseDTO).isEqualTo(montaRetornoConsultaEDaBaixaNoEstoque(produtosAtualizados, produtoPedidoRequest));
        }

        @Test
        void deveGerarExcecao_QuandoConsultaEDaBaixaNoEstoque_IdProdutoNaoEncontrado() {
            ProdutoPedidoRequestDTO produtoPedidoRequestUm = new ProdutoPedidoRequestDTO(10, 1);
            ProdutoPedidoRequestDTO produtoPedidoRequestDois = new ProdutoPedidoRequestDTO(2, 2);

            List<ProdutoPedidoRequestDTO> produtoPedidoRequest = List.of(produtoPedidoRequestUm,
                    produtoPedidoRequestDois
            );

            assertThatThrownBy(() -> produtoService.consultaEDaBaixaNoEstoque(produtoPedidoRequest))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage("Produto com o id '10' não encontrado");
        }

        @Test
        void deveGerarExcecao_QuandoConsultaEDaBaixaNoEstoque_EstoqueInsuficiente() {
            ProdutoPedidoRequestDTO produtoPedidoRequestUm = new ProdutoPedidoRequestDTO(1, 15);
            ProdutoPedidoRequestDTO produtoPedidoRequestDois = new ProdutoPedidoRequestDTO(2, 2);

            List<ProdutoPedidoRequestDTO> produtoPedidoRequest = List.of(produtoPedidoRequestUm,
                    produtoPedidoRequestDois
            );

            assertThatThrownBy(() -> produtoService.consultaEDaBaixaNoEstoque(produtoPedidoRequest))
                    .isInstanceOf(EstoqueException.class)
                    .hasMessage("Produto com o id '1' está fora de estoque");
        }
    }

    @Nested
    class ListarProduto {

        @Test
        void deveListarProdutos() {
            Pageable pageable = Pageable.ofSize(2);
            long totalProdutos = produtoRepository.count();
            List<Produto> produtosEsperados = List.of(
                    new Produto("Produto um", 10, BigDecimal.valueOf(149.99)),
                    new Produto("Produto dois", 27, BigDecimal.valueOf(1200))
            );
            Produto primeiroProdutoEsperado = produtosEsperados.get(0);
            Produto segundoProdutoEsperado = produtosEsperados.get(1);

            Page<ProdutoResponseDTO> produtoResponseDTO = produtoService.listaProdutos(pageable);
            ProdutoResponseDTO primeiroProdutoRetorno = produtoResponseDTO.getContent().get(0);
            ProdutoResponseDTO segundoProdutoRetorno = produtoResponseDTO.getContent().get(1);

            assertThat(produtoResponseDTO.getContent()).hasSize(pageable.getPageSize());
            assertThat(produtoResponseDTO.getTotalElements()).isEqualTo(totalProdutos);

            assertThat(primeiroProdutoRetorno)
                    .isNotNull()
                    .isInstanceOf(ProdutoResponseDTO.class);
            assertThat(primeiroProdutoRetorno.id())
                    .isNotNull();
            assertThat(primeiroProdutoRetorno.descricao())
                    .isNotNull()
                    .isEqualTo(primeiroProdutoEsperado.getDescricao());
            assertThat(primeiroProdutoRetorno.quantidadeEstoque())
                    .isNotNull()
                    .isEqualTo(primeiroProdutoEsperado.getQuantidadeEstoque());
            assertThat(primeiroProdutoRetorno.valor())
                    .isNotNull()
                    .isEqualTo(primeiroProdutoEsperado.getValor());

            assertThat(segundoProdutoRetorno)
                    .isNotNull()
                    .isInstanceOf(ProdutoResponseDTO.class);
            assertThat(segundoProdutoRetorno.id())
                    .isNotNull();
            assertThat(segundoProdutoRetorno.descricao())
                    .isNotNull()
                    .isEqualTo(segundoProdutoEsperado.getDescricao());
            assertThat(segundoProdutoRetorno.quantidadeEstoque())
                    .isNotNull()
                    .isEqualTo(segundoProdutoEsperado.getQuantidadeEstoque());
            assertThat(segundoProdutoRetorno.valor())
                    .isNotNull()
                    .isEqualTo(segundoProdutoEsperado.getValor());
        }
    }

    @Nested
    class AtualizarProduto {

        @Test
        void deveAtualizarProduto() {
            ProdutoRequestDTO produtoASerAtualizado = new ProdutoRequestDTO(null, null, BigDecimal.valueOf(155.55));

            ProdutoResponseDTO produtoResponseDTO = produtoService.atualizarProduto(1, produtoASerAtualizado);

            Optional<Produto> produtoAposAtualizacao = produtoRepository.findById(1);

            assertThat(produtoResponseDTO)
                    .isNotNull()
                    .isInstanceOf(ProdutoResponseDTO.class);
            assertThat(produtoResponseDTO.id()).isEqualTo(1);
            assertThat(produtoAposAtualizacao)
                    .isNotNull()
                    .isNotEmpty();
            if (produtoAposAtualizacao.isPresent()) {
                assertThat(produtoResponseDTO.descricao())
                        .isEqualTo(produtoAposAtualizacao.get().getDescricao());
                assertThat(produtoResponseDTO.quantidadeEstoque())
                        .isEqualTo(produtoAposAtualizacao.get().getQuantidadeEstoque());
            }
            assertThat(produtoResponseDTO.valor())
                    .isEqualTo(produtoASerAtualizado.valor());
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarProduto_IdProdutoNaoExiste() {
            ProdutoRequestDTO produtoASerAtualizado = new ProdutoRequestDTO(null, null, BigDecimal.valueOf(155.55));

            assertThatThrownBy(() -> produtoService.atualizarProduto(10, produtoASerAtualizado))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage("Produto com o id '10' não encontrado");
        }
    }

    private List<ConsultaBaixaEstoqueProdutoResponseDTO> montaRetornoConsultaEDaBaixaNoEstoque(List<Produto> produtos,
                                                                                               List<ProdutoPedidoRequestDTO> produtosDoPedido) {
        List<ConsultaBaixaEstoqueProdutoResponseDTO> response = new ArrayList<>();
        for (Produto produto : produtos) {
            Optional<ProdutoPedidoRequestDTO> produtoDoPedido = produtosDoPedido
                    .stream()
                    .filter(prod -> Objects.equals(prod.idProduto(), produto.getId()))
                    .findFirst();
            if (produtoDoPedido.isPresent()) {
                ConsultaBaixaEstoqueProdutoResponseDTO consultaBaixaEstoqueProdutoResponseDTO = new ConsultaBaixaEstoqueProdutoResponseDTO(produto.getId(),
                        produto.getDescricao(),
                        produtoDoPedido.get().quantidade(),
                        produto.getValor());
                response.add(consultaBaixaEstoqueProdutoResponseDTO);
            }
        }
        return response;
    }

    private ProdutoRequestDTO montaRequestProdutoDTO() {
        return new ProdutoRequestDTO("Produto novo",
                55,
                BigDecimal.valueOf(78)
        );
    }

    private List<ProdutoRequestDTO> montaListRequestProdutoDTO() {
        return List.of(
                new ProdutoRequestDTO("Produto novo", 9, BigDecimal.valueOf(99.99)),
                new ProdutoRequestDTO("Produto mais novo ainda", 47, BigDecimal.valueOf(75))
        );
    }

    public ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getDescricao(),
                produto.getQuantidadeEstoque(),
                produto.getValor()
        );
    }
}
