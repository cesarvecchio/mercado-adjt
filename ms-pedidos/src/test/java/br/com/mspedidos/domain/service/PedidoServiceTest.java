package br.com.mspedidos.domain.service;


import br.com.mspedidos.application.controller.exceptions.EstoqueException;
import br.com.mspedidos.application.controller.exceptions.NaoEncontradoException;
import br.com.mspedidos.application.controller.exceptions.PagamentoException;
import br.com.mspedidos.application.request.AtualizarStatusLoteRequestDTO;
import br.com.mspedidos.application.request.PedidoRequestDTO;
import br.com.mspedidos.application.request.ProdutoPedidoRequestDTO;
import br.com.mspedidos.application.response.PedidoResponseDTO;
import br.com.mspedidos.domain.entity.Pedido;
import br.com.mspedidos.domain.enums.PagamentoEnum;
import br.com.mspedidos.domain.enums.StatusEnum;
import br.com.mspedidos.domain.valueobject.Produto;
import br.com.mspedidos.infra.repository.PedidoRepository;
import br.com.mspedidos.application.controller.exceptions.StandardError;
import br.com.mspedidos.interfaces.cliente.ClienteInterface;
import br.com.mspedidos.interfaces.produto.ProdutoInterface;
import br.com.mspedidos.interfaces.produto.response.ProdutoResponseDTO;
import br.com.mspedidos.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoServiceTest {


    private PedidoService pedidoService;
    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteInterface clienteInterface;

    @Mock
    private ProdutoInterface produtoInterface;

    @Mock
    private Utils utils;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        pedidoService = new PedidoService(pedidoRepository, clienteInterface, produtoInterface, utils);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Nested
    class CriarPedido {

        @Test
        void deveCriarPedido() throws JsonProcessingException {
            Long idCliente = 123L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();

            String idPedido = ObjectId.get().toString();
            List<ProdutoResponseDTO> produtoResponseDTO = montaListProdutoResponseDTO();
            List<Produto> produtosEncontrados = produtoResponseDTO.stream().map(PedidoServiceTest.this::toValueObjectProduto).toList();
            Pedido pedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);
            pedido.setIdPedido(idPedido);

            when(produtoInterface.consultaEDaBaixaNoEstoque(pedidoRequestDTO.produtos()))
                    .thenReturn(produtoResponseDTO);
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            PedidoResponseDTO pedidoResponseDTO = pedidoService.criarPedido(idCliente, pedidoRequestDTO);

            verify(produtoInterface, times(1)).consultaEDaBaixaNoEstoque(pedidoRequestDTO.produtos());
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
            assertThat(pedidoResponseDTO).isEqualTo(toResponseDTO(pedido));
        }

        @Test
        void deveGerarExcecao_QuandoCriarPedido_IdClienteNaoExiste() throws JsonProcessingException {
            Long idCliente = 456L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();
            final String MSG_EXCEPTION = "Nao Encontrado Exception";

            StandardError error = StandardError.builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(MSG_EXCEPTION)
                    .message("Cliente com o id '456' não encontrado!")
                    .path("/cliente")
                    .build();

            when(clienteInterface.buscarCliente(idCliente)).thenThrow(new NaoEncontradoException(MSG_EXCEPTION));
            when(utils.getErrorMessageFeignIntegration(MSG_EXCEPTION)).thenReturn(error);

            assertThatThrownBy(() -> pedidoService.criarPedido(idCliente, pedidoRequestDTO)).isInstanceOf(NaoEncontradoException.class)
                    .hasMessage("Cliente com o id '456' não encontrado!");
            verify(clienteInterface, times(1)).buscarCliente(idCliente);
            verify(utils, times(1)).getErrorMessageFeignIntegration(MSG_EXCEPTION);
        }

        @Test
        void deveGerarExcecao_QuandoCriarPedido_IdProdutoNaoExiste() throws JsonProcessingException {
            Long idCliente = 456L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();
            final String MSG_EXCEPTION = "Nao Encontrado Exception";

            StandardError error = StandardError.builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(MSG_EXCEPTION)
                    .message("Produto com o id '2' não encontrado!")
                    .path("/produtos")
                    .build();

            when(produtoInterface.consultaEDaBaixaNoEstoque(pedidoRequestDTO.produtos())).thenThrow(new NaoEncontradoException(MSG_EXCEPTION));
            when(utils.getErrorMessageFeignIntegration(MSG_EXCEPTION)).thenReturn(error);

            assertThatThrownBy(() -> pedidoService.criarPedido(idCliente, pedidoRequestDTO)).isInstanceOf(NaoEncontradoException.class)
                    .hasMessage("Produto com o id '2' não encontrado!");
            verify(produtoInterface, times(1)).consultaEDaBaixaNoEstoque(pedidoRequestDTO.produtos());
            verify(utils, times(1)).getErrorMessageFeignIntegration(MSG_EXCEPTION);
        }

        @Test
        void deveGerarExcecao_QuandoCriarPedido_ProdutoForaEstoque() throws JsonProcessingException {
            Long idCliente = 456L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();
            final String MSG_EXCEPTION = "Produto fora de estoque";

            StandardError error = StandardError.builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                    .error(MSG_EXCEPTION)
                    .message("Produto com o id '2' está fora de estoque")
                    .path("/produtos")
                    .build();

            when(produtoInterface.consultaEDaBaixaNoEstoque(pedidoRequestDTO.produtos())).thenThrow(new EstoqueException(MSG_EXCEPTION));
            when(utils.getErrorMessageFeignIntegration(MSG_EXCEPTION)).thenReturn(error);

            assertThatThrownBy(() -> pedidoService.criarPedido(idCliente, pedidoRequestDTO)).isInstanceOf(EstoqueException.class)
                    .hasMessage("Produto com o id '2' está fora de estoque");
            verify(produtoInterface, times(1)).consultaEDaBaixaNoEstoque(pedidoRequestDTO.produtos());
            verify(utils, times(1)).getErrorMessageFeignIntegration(MSG_EXCEPTION);
        }

        @Test
        void deveGerarExcecaoGenerica_QuandoCriarPedido() throws JsonProcessingException {
            Long idCliente = 456L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();

            when(produtoInterface.consultaEDaBaixaNoEstoque(pedidoRequestDTO.produtos())).thenThrow(new RuntimeException("Exceção genérica"));

            PedidoResponseDTO pedidoResponseDTO = pedidoService.criarPedido(idCliente, pedidoRequestDTO);

            verify(produtoInterface, times(1)).consultaEDaBaixaNoEstoque(pedidoRequestDTO.produtos());
            assertThat(pedidoResponseDTO).isNull();
        }
    }

    @Nested
    class EfetuarPagamento {

        @Test
        void deveEfetuarPagamento() {
            Long idCliente = 123L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();

            String idPedido = ObjectId.get().toString();
            List<ProdutoResponseDTO> produtoResponseDTO = montaListProdutoResponseDTO();
            List<Produto> produtosEncontrados = produtoResponseDTO.stream().map(PedidoServiceTest.this::toValueObjectProduto).toList();
            Pedido pedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);
            pedido.setIdPedido(idPedido);
            Pedido pedidoAtualizado = new Pedido(idPedido, idCliente, pedido.getProdutos(), pedido.getValorTotal(), pedido.getFormaPagamento(), StatusEnum.PAGO);

            when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(pedido)).thenReturn(pedido);

            PedidoResponseDTO pedidoResponseDTO = pedidoService.efetuarPagamentoPedido(idPedido);

            verify(pedidoRepository, times(1)).findById(idPedido);
            verify(pedidoRepository, times(1)).save(pedido);
            assertThat(pedidoResponseDTO).isEqualTo(toResponseDTO(pedidoAtualizado));
        }

        @Test
        void deveGerarExcecao_QuandoEfetuarPagamento_IdPedidoNaoExiste() {
            String idPedido = ObjectId.get().toString();
            final String MSG_EXCEPTION = String.format("Pedido com o id '%s' não encontrado!", idPedido);


            when(pedidoRepository.findById(idPedido)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoService.efetuarPagamentoPedido(idPedido)).isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(MSG_EXCEPTION);
            verify(pedidoRepository, times(1)).findById(idPedido);
        }

        @Test
        void deveGerarExcecao_QuandoEfetuarPagamento_StatusDiferenteAguardandoPagamento() {
            Long idCliente = 123L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();

            String idPedido = ObjectId.get().toString();
            List<ProdutoResponseDTO> produtoResponseDTO = montaListProdutoResponseDTO();
            List<Produto> produtosEncontrados = produtoResponseDTO.stream().map(PedidoServiceTest.this::toValueObjectProduto).toList();
            Pedido pedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);
            pedido.setIdPedido(idPedido);
            pedido.setStatus(StatusEnum.PAGO);
            final String MSG_EXCEPTION = "Não é possível efetuar o pagamento de um pedido que não esteja aguardando pagamento!";

            when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));

            assertThatThrownBy(() -> pedidoService.efetuarPagamentoPedido(idPedido)).isInstanceOf(PagamentoException.class)
                    .hasMessage(MSG_EXCEPTION);
            verify(pedidoRepository, times(1)).findById(idPedido);
        }
    }

    @Nested
    class BuscarPedido {

        @Test
        void deveBuscarPedido() {
            Long idCliente = 123L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();

            String idPedido = ObjectId.get().toString();
            List<ProdutoResponseDTO> produtoResponseDTO = montaListProdutoResponseDTO();
            List<Produto> produtosEncontrados = produtoResponseDTO.stream().map(PedidoServiceTest.this::toValueObjectProduto).toList();
            Pedido pedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);
            pedido.setIdPedido(idPedido);

            when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));

            PedidoResponseDTO pedidoResponseDTO = pedidoService.buscarPedido(idPedido);

            verify(pedidoRepository, times(1)).findById(idPedido);
            assertThat(pedidoResponseDTO).isEqualTo(toResponseDTO(pedido));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPedido_IdPedidoNaoExiste() {
            Long idCliente = 123L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();

            String idPedido = ObjectId.get().toString();
            List<ProdutoResponseDTO> produtoResponseDTO = montaListProdutoResponseDTO();
            List<Produto> produtosEncontrados = produtoResponseDTO.stream().map(PedidoServiceTest.this::toValueObjectProduto).toList();
            Pedido pedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);
            pedido.setIdPedido(idPedido);
            final String MSG_EXCEPTION = String.format("Pedido com o id '%s' não encontrado!", idPedido);

            when(pedidoRepository.findById(idPedido)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoService.buscarPedido(idPedido)).isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(MSG_EXCEPTION);
            verify(pedidoRepository, times(1)).findById(idPedido);
        }

        @Test
        void deveBuscarPedidosPagos() {
            Long idCliente = 123L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();

            String idPedido = ObjectId.get().toString();
            List<ProdutoResponseDTO> produtoResponseDTO = montaListProdutoResponseDTO();
            List<Produto> produtosEncontrados = produtoResponseDTO.stream().map(PedidoServiceTest.this::toValueObjectProduto).toList();
            Pedido pedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);
            pedido.setIdPedido(idPedido);
            pedido.setStatus(StatusEnum.PAGO);
            List<Pedido> pedidos = List.of(pedido);

            when(pedidoRepository.findByStatus(StatusEnum.PAGO)).thenReturn(pedidos);

            List<PedidoResponseDTO> pedidosResponse = pedidoService.buscarPedidosPagos();

            verify(pedidoRepository, times(1)).findByStatus(StatusEnum.PAGO);
            assertThat(pedidosResponse).isEqualTo(pedidos.stream().map(PedidoServiceTest.this::toResponseDTO).toList());
        }
    }

    @Nested
    class AtualizarStatusPedido {

        @Test
        void deveAtualizarStatusPedido() {
            Long idCliente = 123L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();

            String idPedido = ObjectId.get().toString();
            List<ProdutoResponseDTO> produtoResponseDTO = montaListProdutoResponseDTO();
            List<Produto> produtosEncontrados = produtoResponseDTO.stream().map(PedidoServiceTest.this::toValueObjectProduto).toList();
            Pedido pedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);
            pedido.setIdPedido(idPedido);
            Pedido pedidoAtualizado = new Pedido(idPedido, idCliente, pedido.getProdutos(), pedido.getValorTotal(), pedido.getFormaPagamento(), StatusEnum.PAGO);

            when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(pedido)).thenReturn(pedido);

            PedidoResponseDTO pedidoResponseDTO = pedidoService.atualizarStatusPedido(idPedido, StatusEnum.PAGO);

            verify(pedidoRepository, times(1)).findById(idPedido);
            assertThat(pedidoResponseDTO).isEqualTo(toResponseDTO(pedidoAtualizado));
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarStatusPedido_IdPedidoNaoExiste() {
            Long idCliente = 123L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();

            String idPedido = ObjectId.get().toString();
            List<ProdutoResponseDTO> produtoResponseDTO = montaListProdutoResponseDTO();
            List<Produto> produtosEncontrados = produtoResponseDTO.stream().map(PedidoServiceTest.this::toValueObjectProduto).toList();
            Pedido pedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);
            pedido.setIdPedido(idPedido);
            final String MSG_EXCEPTION = String.format("Pedido com o id '%s' não encontrado!", idPedido);

            when(pedidoRepository.findById(idPedido)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoService.atualizarStatusPedido(idPedido, StatusEnum.PAGO)).isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(MSG_EXCEPTION);
            verify(pedidoRepository, times(1)).findById(idPedido);
        }

        @Test
        void deveAtualizarStatusPedidoEmLote() {
            Long idCliente = 123L;
            PedidoRequestDTO pedidoRequestDTO = montaPedidoRequestDTO();

            String idPrimeiroPedido = ObjectId.get().toString();
            String idSegundoPedido = ObjectId.get().toString();

            List<ProdutoResponseDTO> produtoResponseDTO = montaListProdutoResponseDTO();
            List<Produto> produtosEncontrados = produtoResponseDTO.stream().map(PedidoServiceTest.this::toValueObjectProduto).toList();
            Pedido primeiroPedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);
            primeiroPedido.setIdPedido(idPrimeiroPedido);
            Pedido segundoPedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);
            segundoPedido.setIdPedido(idSegundoPedido);

            List<Pedido> pedidosAtualizados = List.of(
                    new Pedido(idPrimeiroPedido, idCliente, primeiroPedido.getProdutos(), primeiroPedido.getValorTotal(), primeiroPedido.getFormaPagamento(), StatusEnum.PAGO),
                    new Pedido(idSegundoPedido, idCliente, segundoPedido.getProdutos(), segundoPedido.getValorTotal(), segundoPedido.getFormaPagamento(), StatusEnum.PAGO)
            );

            AtualizarStatusLoteRequestDTO atualizarStatusLoteRequestDTO = new AtualizarStatusLoteRequestDTO(
                    List.of(idPrimeiroPedido, idSegundoPedido),
                    StatusEnum.PAGO
            );

            when(pedidoRepository.findById(idPrimeiroPedido)).thenReturn(Optional.of(primeiroPedido));
            when(pedidoRepository.findById(idSegundoPedido)).thenReturn(Optional.of(segundoPedido));
            when(pedidoRepository.saveAll(List.of(primeiroPedido, segundoPedido))).thenReturn(pedidosAtualizados);

            List<PedidoResponseDTO> pedidosResponse = pedidoService.atualizarStatusPedidoEmLote(atualizarStatusLoteRequestDTO);

            verify(pedidoRepository, times(1)).findById(idPrimeiroPedido);
            verify(pedidoRepository, times(1)).findById(idSegundoPedido);
            verify(pedidoRepository, times(1)).saveAll(List.of(primeiroPedido, segundoPedido));
            assertThat(pedidosResponse).isEqualTo(pedidosAtualizados.stream().map(PedidoServiceTest.this::toResponseDTO).toList());
        }
    }


    private PedidoRequestDTO montaPedidoRequestDTO() {
        return new PedidoRequestDTO(
                List.of(
                        new ProdutoPedidoRequestDTO(1, 1),
                        new ProdutoPedidoRequestDTO(2, 2)
                ), PagamentoEnum.PIX
        );
    }

    private List<ProdutoResponseDTO> montaListProdutoResponseDTO() {
        return List.of(
                new ProdutoResponseDTO(1, "Produto um", 1, BigDecimal.valueOf(10.0)),
                new ProdutoResponseDTO(2, "Produto dois", 2, BigDecimal.valueOf(20.99))
        );
    }

    public Produto toValueObjectProduto(ProdutoResponseDTO produtoResponseDTO) {
        return new Produto(produtoResponseDTO.id(),
                produtoResponseDTO.descricao(),
                produtoResponseDTO.quantidade(),
                produtoResponseDTO.valor()
        );
    }

    private Pedido getPedido(Long idCliente, PedidoRequestDTO pedidoRequestDTO, List<Produto> produtosEncontrados) {

        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Produto produtoEncontrado : produtosEncontrados) {
            valorTotal = valorTotal.add(produtoEncontrado.valor().multiply(BigDecimal.valueOf(produtoEncontrado.quantidade())));
        }

        return new Pedido(idCliente,
                produtosEncontrados,
                valorTotal,
                pedidoRequestDTO.formaPagamento(),
                StatusEnum.AGUARDANDO_PAGAMENTO
        );
    }

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        return new PedidoResponseDTO(
                pedido.getIdPedido(),
                pedido.getIdCliente(),
                pedido.getProdutos(),
                pedido.getValorTotal(),
                pedido.getFormaPagamento(),
                pedido.getStatus()
        );
    }
}
