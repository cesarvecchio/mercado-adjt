package br.com.msprodutos.domain.service;

import br.com.msprodutos.application.controller.exceptions.EstoqueException;
import br.com.msprodutos.application.controller.exceptions.NaoEncontradoException;
import br.com.msprodutos.application.request.ProdutoPedidoRequestDTO;
import br.com.msprodutos.application.request.ProdutoRequestDTO;
import br.com.msprodutos.application.response.ConsultaBaixaEstoqueProdutoResponseDTO;
import br.com.msprodutos.application.response.ProdutoResponseDTO;
import br.com.msprodutos.domain.enitity.Produto;
import br.com.msprodutos.repository.ProdutoRepository;
import br.com.msprodutos.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    private ProdutoService produtoService;

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job job;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private Utils utils;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        produtoService = new ProdutoService(jobLauncher, job, produtoRepository, utils);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Nested
    class CadastrarProduto {

        @Test
        void deveCadastrarProdutoIndividualmente() {
            ProdutoRequestDTO produtoRequestDTO = montaRequestProdutoDTO();
            Produto produto = toEntity(produtoRequestDTO);

            when(produtoRepository.save(produto)).thenReturn(produto);

            ProdutoResponseDTO produtoResponseDTO = produtoService.cadastrarProdutoIndividualmente(produtoRequestDTO);

            verify(produtoRepository, times(1)).save(produto);
            assertThat(produtoResponseDTO).isEqualTo(toResponseDTO(produto));
        }

        @Test
        void deveCadastrarProdutosEmLote() {
            List<ProdutoRequestDTO> produtosRequestDTO = montaListRequestProdutoDTO();
            List<Produto> produtos = produtosRequestDTO.stream().map(ProdutoServiceTest.this::toEntity).toList();

            when(produtoRepository.saveAll(produtos)).thenReturn(produtos);

            List<ProdutoResponseDTO> produtosResponseDTO = produtoService.cadastrarProdutoEmLote(produtosRequestDTO);

            verify(produtoRepository, times(1)).saveAll(produtos);
            assertThat(produtosResponseDTO).isEqualTo(produtos.stream().map(ProdutoServiceTest.this::toResponseDTO).toList());
        }
    }

    @Nested
    class ConsultaEDaBaixaNoEstoque {

        @Test
        void consultaEDaBaixaNoEstoque() {
            Produto produtoUm = new Produto(1, "Produto um", 14, BigDecimal.valueOf(1500.0));
            Produto produtoDois = new Produto(2, "Produto dois", 8, BigDecimal.valueOf(18.99));
            ProdutoPedidoRequestDTO produtoPedidoRequestUm = new ProdutoPedidoRequestDTO(1, 1);
            ProdutoPedidoRequestDTO produtoPedidoRequestDois = new ProdutoPedidoRequestDTO(2, 2);
            List<Produto> produtosAtualizados = List.of(
                    new Produto(1, "Produto um", 13, BigDecimal.valueOf(1500.0)),
                    new Produto(2, "Produto dois", 6, BigDecimal.valueOf(18.99)
                    )
            );
            List<ProdutoPedidoRequestDTO> produtoPedidoRequest = List.of(produtoPedidoRequestUm,
                    produtoPedidoRequestDois
            );

            when(produtoRepository.findById(1)).thenReturn(Optional.of(produtoUm));
            when(produtoRepository.findById(2)).thenReturn(Optional.of(produtoDois));
            when(produtoRepository.saveAll(List.of(produtoUm, produtoDois))).thenReturn(produtosAtualizados);

            List<ConsultaBaixaEstoqueProdutoResponseDTO> consultaBaixaEstoqueProdutoResponseDTO = produtoService.consultaEDaBaixaNoEstoque(produtoPedidoRequest);

            verify(produtoRepository, times(1)).findById(1);
            verify(produtoRepository, times(1)).findById(2);
            verify(produtoRepository, times(1)).saveAll(List.of(produtoUm, produtoDois));
            assertThat(consultaBaixaEstoqueProdutoResponseDTO).isEqualTo(montaRetornoConsultaEDaBaixaNoEstoque(produtosAtualizados, produtoPedidoRequest));
        }

        @Test
        void deveGerarExcecao_QuandoConsultaEDaBaixaNoEstoque_IdProdutoNaoExiste() {
            ProdutoPedidoRequestDTO produtoPedidoRequestUm = new ProdutoPedidoRequestDTO(75, 1);
            ProdutoPedidoRequestDTO produtoPedidoRequestDois = new ProdutoPedidoRequestDTO(2, 2);
            List<ProdutoPedidoRequestDTO> produtoPedidoRequest = List.of(produtoPedidoRequestUm,
                    produtoPedidoRequestDois
            );
            final String MSG_EXCEPTION = "Produto com o id '75' não encontrado";

            when(produtoRepository.findById(75)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> produtoService.consultaEDaBaixaNoEstoque(produtoPedidoRequest)).isInstanceOf(NaoEncontradoException.class)
                            .hasMessage(MSG_EXCEPTION);
            verify(produtoRepository, times(1)).findById(75);
        }

        @Test
        void deveGerarExcecao_QuandoConsultaEDaBaixaNoEstoque_EstoqueInsuficiente() {
            Produto produtoUm = new Produto("Produto um", 0, BigDecimal.valueOf(1500.0));
            ProdutoPedidoRequestDTO produtoPedidoRequestUm = new ProdutoPedidoRequestDTO(1, 1);
            ProdutoPedidoRequestDTO produtoPedidoRequestDois = new ProdutoPedidoRequestDTO(2, 2);
            List<ProdutoPedidoRequestDTO> produtoPedidoRequest = List.of(produtoPedidoRequestUm,
                    produtoPedidoRequestDois
            );
            final String MSG_EXCEPTION = "Produto com o id '1' está fora de estoque";

            when(produtoRepository.findById(1)).thenReturn(Optional.of(produtoUm));

            assertThatThrownBy(() -> produtoService.consultaEDaBaixaNoEstoque(produtoPedidoRequest)).isInstanceOf(EstoqueException.class)
                            .hasMessage(MSG_EXCEPTION);
            verify(produtoRepository, times(1)).findById(1);
        }
    }

    @Nested
    class ListarProduto {

        @Test
        void deveListarProdutos() {
            Pageable pageable = Pageable.ofSize(10);
            List<ProdutoRequestDTO> produtosRequestDTO = montaListRequestProdutoDTO();
            List<Produto> produtos = produtosRequestDTO.stream().map(ProdutoServiceTest.this::toEntity).toList();

            when(produtoRepository.findAll(pageable)).thenReturn(new PageImpl<>(produtos));

            Page<ProdutoResponseDTO> produtoResponseDTO = produtoService.listaProdutos(pageable);
            assertThat(produtoResponseDTO).isEqualTo(new PageImpl<>(produtos).map(ProdutoServiceTest.this::toResponseDTO));
        }
    }

    @Nested
    class AtualizarProduto {

        @Test
        void deveAtualizarProduto() {
            ProdutoRequestDTO produtoRequestDTO = montaRequestProdutoDTO();
            ProdutoRequestDTO produtoRequestAtualizado = new ProdutoRequestDTO(produtoRequestDTO.descricao(), produtoRequestDTO.quantidadeEstoque(), BigDecimal.valueOf(1200.0));
            Produto produtoUm = toEntity(produtoRequestDTO);
            Produto produtoAtualizado = toEntity(produtoRequestAtualizado);

            when(produtoRepository.findById(1)).thenReturn(Optional.of(produtoUm));
            when(produtoRepository.save(produtoUm)).thenReturn(produtoAtualizado);

            ProdutoResponseDTO produtoResponseDTO = produtoService.atualizarProduto(1, produtoRequestAtualizado);

            verify(produtoRepository, times(1)).findById(1);
            verify(produtoRepository, times(1)).save(produtoUm);
            assertThat(produtoResponseDTO).isEqualTo(toResponseDTO(produtoAtualizado));
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarProduto_IdProdutoNaoExiste() {
            ProdutoRequestDTO produtoRequestDTO = montaRequestProdutoDTO();
            final String MSG_EXCEPTION = "Produto com o id '75' não encontrado";

            when(produtoRepository.findById(75)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> produtoService.atualizarProduto(75, produtoRequestDTO)).isInstanceOf(NaoEncontradoException.class)
                            .hasMessage(MSG_EXCEPTION);
            verify(produtoRepository, times(1)).findById(75);
        }
    }

    private ProdutoRequestDTO montaRequestProdutoDTO() {
        return new ProdutoRequestDTO("Produto um",
                14,
                BigDecimal.valueOf(1500.0)
        );
    }

    private List<ProdutoRequestDTO> montaListRequestProdutoDTO() {
        return List.of(
                new ProdutoRequestDTO("Produto um",14, BigDecimal.valueOf(1500.0)),
                new ProdutoRequestDTO("Produto dois",8, BigDecimal.valueOf(18.99))
        );
    }

    public List<ConsultaBaixaEstoqueProdutoResponseDTO> montaRetornoConsultaEDaBaixaNoEstoque(List<Produto> produtos,
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

    public Produto toEntity(ProdutoRequestDTO produtoRequestDTO) {
        return new Produto(produtoRequestDTO.descricao(),
                produtoRequestDTO.quantidadeEstoque(),
                produtoRequestDTO.valor()
        );
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getDescricao(),
                produto.getQuantidadeEstoque(),
                produto.getValor()
        );
    }
}
