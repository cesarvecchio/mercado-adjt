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
                    "1", 1,
                    List.of(new ProdutoVo(1, "Pasta de Dente",
                            BigDecimal.ONE, "Oral-B")),
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
            ClienteVo clienteVo = new ClienteVo(1L, "Teste", "11111111111",
                    "40028922", "teste@gmail.com", null);

            when(clienteService.obterClientePorId(anyLong())).thenReturn(clienteVo);
            doNothing().when(emailService).sendEmail(any(Email.class));

            pedidoService.atualizarPedidoEntregue(anyString());

            verify(clienteService).obterClientePorId(anyLong());
            verify(clienteService).obterClientePorId(anyLong());
        }
    }
}
