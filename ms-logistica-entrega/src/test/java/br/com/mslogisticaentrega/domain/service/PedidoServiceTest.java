package br.com.mslogisticaentrega.domain.service;

import br.com.mslogisticaentrega.domain.enums.PagamentoEnum;
import br.com.mslogisticaentrega.domain.enums.StatusEnum;
import br.com.mslogisticaentrega.domain.valueObject.*;
import br.com.mslogisticaentrega.infra.client.PedidoClient;
import br.com.mslogisticaentrega.infra.exceptions.NaoEncontradoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PedidoServiceTest {
    @Mock
    private PedidoClient pedidoClient;
    @Mock
    private ClienteService clienteService;
    @Mock
    private EmailService emailService;

    private PedidoService pedidoService;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        pedidoService = new PedidoService(pedidoClient, clienteService, emailService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Nested
    class BuscarPedidoPago {
        @Test
        void deveBuscarPedidoPago() {
            List<PedidoVo> pedidoList = List.of(new PedidoVo(
                    "1", 1L,
                    List.of(new ProdutoVo(1, "Pasta de Dente",
                            BigDecimal.ONE)),
                    BigDecimal.ONE, PagamentoEnum.PIX, StatusEnum.PAGO
            ));

            when(pedidoClient.buscarPedidosPagos()).thenReturn(pedidoList);

            var resultado = pedidoService.buscarPedidosPagos();

            assertEquals(pedidoList, resultado);

            verify(pedidoClient).buscarPedidosPagos();
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPedidoPago() {
            when(pedidoClient.buscarPedidosPagos()).thenReturn(List.of());

            assertThatThrownBy(() -> pedidoService.buscarPedidosPagos())
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(
                            String.format("Nenhum pedido com status:[%s]", StatusEnum.PAGO));

            verify(pedidoClient).buscarPedidosPagos();
        }
    }

    @Nested
    class AtualizarPedidosAguardandoEntrega{
        @Test
        void deveAtualizarPedidosAguardandoEntrega(){
            doNothing().when(pedidoClient).atualizarPedidosAguardandoEntrega(any(AtualizarStatusLoteRequestVo.class));

            pedidoService.atualizarPedidosAguardandoEntrega(anyList());

            verify(pedidoClient).atualizarPedidosAguardandoEntrega(any(AtualizarStatusLoteRequestVo.class));
        }
    }

    @Nested
    class AtualizaPedidoEntregue{
        @Test
        void deveAtualizarPedidoEntregue(){
            String idPedido = "664a6870d1f54475a57ed77a";
            PedidoVo pedido = pedido();
            ClienteVo cliente = cliente();

            doNothing().when(pedidoClient).atualizarPedidoEntregue(idPedido, StatusEnum.ENTREGUE);
            when(pedidoClient.buscarPedido(idPedido)).thenReturn(pedido);
            when(clienteService.obterClientePorId(pedido.idCliente())).thenReturn(cliente);
            doNothing().when(emailService).sendEmail(
                    new Email(cliente.email(),
                            "Pedido Entregue",
                            "Ah entrega do seu pedido foi realizada com sucesso!"));

            pedidoService.atualizarPedidoEntregue(idPedido);

            verify(pedidoClient).atualizarPedidoEntregue(anyString(), any(StatusEnum.class));
            verify(pedidoClient).buscarPedido(anyString());
            verify(clienteService).obterClientePorId(anyLong());
            verify(emailService).sendEmail(any(Email.class));
        }
    }

    private PedidoVo pedido(){
        return new PedidoVo(
                "664a6870d1f54475a57ed77a",
                1L,
                List.of(new ProdutoVo(
                        1,
                        "Produto",
                        BigDecimal.ONE
                )),
                BigDecimal.ONE,
                PagamentoEnum.PIX,
                StatusEnum.ENTREGUE
        );
    }

    private ClienteVo cliente(){
        return new ClienteVo(
                1L,
                "Teste",
                "11111111111",
                "11111111",
                "teste@gmail.com",
                new EnderecoVo(
                        "cep",
                        "logradouro",
                        "complemento",
                        "bairro",
                        "cidade",
                        "uf",
                        "numero",
                        1.0,
                        1.0
                )
        );
    }
}
