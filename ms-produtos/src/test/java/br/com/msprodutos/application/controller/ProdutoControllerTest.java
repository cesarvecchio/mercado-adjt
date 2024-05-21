package br.com.msprodutos.application.controller;

import br.com.msprodutos.application.controller.exceptions.ControllerExceptionHandler;
import br.com.msprodutos.application.controller.exceptions.EstoqueException;
import br.com.msprodutos.application.controller.exceptions.NaoEncontradoException;
import br.com.msprodutos.application.request.ProdutoPedidoRequestDTO;
import br.com.msprodutos.application.request.ProdutoRequestDTO;
import br.com.msprodutos.application.response.ConsultaBaixaEstoqueProdutoResponseDTO;
import br.com.msprodutos.application.response.ProdutoResponseDTO;
import br.com.msprodutos.domain.enitity.Produto;
import br.com.msprodutos.domain.service.ProdutoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProdutoControllerTest {

    MockMvc mockMvc;

    AutoCloseable mock;

    @Mock
    private ProdutoService produtoService;

    @BeforeEach
    public void setup() {
        mock = MockitoAnnotations.openMocks(this);

        ProdutoController produtoController = new ProdutoController(produtoService);

        mockMvc = MockMvcBuilders.standaloneSetup(produtoController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class CadastrarProduto {

        @Test
        void deveCadastrarProdutoIndividualmente() throws Exception {
            ProdutoRequestDTO produtoRequestDTO = montaRequestProdutoDTO();
            Produto produto = toEntity(produtoRequestDTO);

            when(produtoService.cadastrarProdutoIndividualmente(produtoRequestDTO)).thenReturn(toResponseDTO(produto));

            mockMvc.perform(post("/produtos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(produtoRequestDTO)))
                    .andExpect(status().isCreated());

            verify(produtoService, times(1)).cadastrarProdutoIndividualmente(produtoRequestDTO);
        }

        @Test
        void deveCadastrarProdutoEmLote() throws Exception {
            List<ProdutoRequestDTO> produtosRequestDTO = montaListRequestProdutoDTO();
            List<Produto> produtos = produtosRequestDTO.stream().map(ProdutoControllerTest.this::toEntity).toList();

            when(produtoService.cadastrarProdutoEmLote(produtosRequestDTO)).thenReturn(produtos.stream().map(ProdutoControllerTest.this::toResponseDTO).toList());

            mockMvc.perform(post("/produtos/lote")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(produtosRequestDTO)))
                    .andExpect(status().isCreated());

            verify(produtoService, times(1)).cadastrarProdutoEmLote(produtosRequestDTO);
        }
    }

    @Nested
    class ConsultaEDaBaixaNoEstoque {

        @Test
        void consultaEDaBaixaNoEstoque() throws Exception {
            ProdutoPedidoRequestDTO produtoPedidoRequestUm = new ProdutoPedidoRequestDTO(1, 1);
            ProdutoPedidoRequestDTO produtoPedidoRequestDois = new ProdutoPedidoRequestDTO(2, 2);
            List<ProdutoPedidoRequestDTO> produtoPedidoRequest = List.of(produtoPedidoRequestUm,
                    produtoPedidoRequestDois
            );
            List<Produto> produtosAtualizados = List.of(
                    new Produto(1, "Produto um", 13, BigDecimal.valueOf(1500.0)),
                    new Produto(2, "Produto dois", 6, BigDecimal.valueOf(18.99)
                    )
            );

            when(produtoService.consultaEDaBaixaNoEstoque(produtoPedidoRequest)).thenReturn(montaRetornoConsultaEDaBaixaNoEstoque(produtosAtualizados, produtoPedidoRequest));

            mockMvc.perform(post("/produtos/consulta-e-da-baixa-estoque")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(produtoPedidoRequest)))
                    .andExpect(status().isOk());

            verify(produtoService, times(1)).consultaEDaBaixaNoEstoque(produtoPedidoRequest);
        }

        @Test
        void deveGerarExcecao_QuandoConsultaEDaBaixaNoEstoque_IdProdutoNaoExiste() throws Exception {
            ProdutoPedidoRequestDTO produtoPedidoRequestUm = new ProdutoPedidoRequestDTO(75, 1);
            ProdutoPedidoRequestDTO produtoPedidoRequestDois = new ProdutoPedidoRequestDTO(2, 2);
            List<ProdutoPedidoRequestDTO> produtoPedidoRequest = List.of(produtoPedidoRequestUm,
                    produtoPedidoRequestDois
            );
            final String MSG_EXCEPTION = "Produto com o id '75' não encontrado";

            when(produtoService.consultaEDaBaixaNoEstoque(produtoPedidoRequest)).thenThrow(new NaoEncontradoException(MSG_EXCEPTION));

            mockMvc.perform(post("/produtos/consulta-e-da-baixa-estoque")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(produtoPedidoRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("Nao Encontrado Exception");
                        assertThat(json).contains(MSG_EXCEPTION);
                    });

            verify(produtoService, times(1)).consultaEDaBaixaNoEstoque(produtoPedidoRequest);
        }

        @Test
        void deveGerarExcecao_QuandoConsultaEDaBaixaNoEstoque_EstoqueInsuficiente() throws Exception {
            ProdutoPedidoRequestDTO produtoPedidoRequestUm = new ProdutoPedidoRequestDTO(1, 999);
            ProdutoPedidoRequestDTO produtoPedidoRequestDois = new ProdutoPedidoRequestDTO(2, 2);
            List<ProdutoPedidoRequestDTO> produtoPedidoRequest = List.of(produtoPedidoRequestUm,
                    produtoPedidoRequestDois
            );
            final String MSG_EXCEPTION = "Produto com o id '1' está fora de estoque";

            when(produtoService.consultaEDaBaixaNoEstoque(produtoPedidoRequest)).thenThrow(new EstoqueException(MSG_EXCEPTION));

            mockMvc.perform(post("/produtos/consulta-e-da-baixa-estoque")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(produtoPedidoRequest)))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("Produto fora de estoque");
                        assertThat(json).contains(MSG_EXCEPTION);
                    });

            verify(produtoService, times(1)).consultaEDaBaixaNoEstoque(produtoPedidoRequest);
        }
    }

    @Nested
    class AtualizarProduto {

        @Test
        void deveAtualizarProduto() throws Exception {
            ProdutoRequestDTO produtoRequestDTO = montaRequestProdutoDTO();
            ProdutoRequestDTO produtoRequestAtualizado = new ProdutoRequestDTO(produtoRequestDTO.descricao(), produtoRequestDTO.quantidadeEstoque(), BigDecimal.valueOf(1200.0));
            Produto produtoAtualizado = toEntity(produtoRequestAtualizado);


            when(produtoService.atualizarProduto(1, produtoRequestAtualizado)).thenReturn(toResponseDTO(produtoAtualizado));

            mockMvc.perform(put("/produtos/{idProduto}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(produtoRequestAtualizado)))
                    .andExpect(status().isOk());

            verify(produtoService, times(1)).atualizarProduto(1, produtoRequestAtualizado);
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarProduto_IdProdutoNaoExiste() throws Exception {
            ProdutoRequestDTO produtoRequestDTO = montaRequestProdutoDTO();
            ProdutoRequestDTO produtoRequestAtualizado = new ProdutoRequestDTO(produtoRequestDTO.descricao(), produtoRequestDTO.quantidadeEstoque(), BigDecimal.valueOf(1200.0));
            Produto produtoAtualizado = toEntity(produtoRequestAtualizado);
            final String MSG_EXCEPTION = "Produto com o id '75' não encontrado";

            when(produtoService.atualizarProduto(75, produtoRequestAtualizado)).thenThrow(new NaoEncontradoException(MSG_EXCEPTION));

            mockMvc.perform(put("/produtos/{idProduto}", 75)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(produtoRequestAtualizado)))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("Nao Encontrado Exception");
                        assertThat(json).contains(MSG_EXCEPTION);
                    });

            verify(produtoService, times(1)).atualizarProduto(75, produtoRequestAtualizado);
        }
    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
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
