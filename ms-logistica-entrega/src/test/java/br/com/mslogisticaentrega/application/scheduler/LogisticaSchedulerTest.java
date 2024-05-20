package br.com.mslogisticaentrega.application.scheduler;

import br.com.mslogisticaentrega.application.controller.response.EntregadorResponse;
import br.com.mslogisticaentrega.domain.enums.PagamentoEnum;
import br.com.mslogisticaentrega.domain.enums.StatusEnum;
import br.com.mslogisticaentrega.domain.service.ClienteService;
import br.com.mslogisticaentrega.domain.service.EntregadorService;
import br.com.mslogisticaentrega.domain.service.PedidoService;
import br.com.mslogisticaentrega.domain.service.RelatorioService;
import br.com.mslogisticaentrega.domain.valueObject.ClienteVo;
import br.com.mslogisticaentrega.domain.valueObject.EnderecoVo;
import br.com.mslogisticaentrega.domain.valueObject.PedidoVo;
import br.com.mslogisticaentrega.domain.valueObject.ProdutoVo;
import br.com.mslogisticaentrega.infra.exceptions.NaoEncontradoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class LogisticaSchedulerTest {
    @Mock
    private PedidoService pedidoService;
    @Mock
    private ClienteService clienteService;
    @Mock
    private EntregadorService entregadorService;
    @Mock
    private RelatorioService relatorioService;
    private LogisticaScheduler logisticaScheduler;
    AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        logisticaScheduler = new LogisticaScheduler(pedidoService, clienteService, entregadorService, relatorioService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void deveProcessarLogistica(){
        List<PedidoVo> pedidoVo = pedido();
        List<ClienteVo> cliente = cliente();
        EntregadorResponse entregador = entregador();

        when(pedidoService.buscarPedidosPagos()).thenReturn(pedidoVo);
        when(clienteService.obterClientePorId(pedidoVo.get(0).idCliente())).thenReturn(cliente.get(0));
        when(clienteService.obterClientePorId(pedidoVo.get(1).idCliente())).thenReturn(cliente.get(1));
        when(clienteService.obterClientePorId(pedidoVo.get(2).idCliente())).thenReturn(cliente.get(2));
        when(clienteService.obterClientePorId(pedidoVo.get(3).idCliente())).thenReturn(cliente.get(3));
        when(entregadorService.buscarTodos()).thenReturn(List.of(entregador, entregador));

        logisticaScheduler.processarLogistica();

    }

    @Test
    void deveGerarExcecao_QuandoProcessarLogistica(){
        doThrow(
                new NaoEncontradoException(String.format("Nenhum pedido com status:[%s]",
                        StatusEnum.PAGO)))
                .when(pedidoService).buscarPedidosPagos();

        logisticaScheduler.processarLogistica();

    }

    private List<PedidoVo> pedido(){
        return List.of(
                new PedidoVo(
                        "664a6870d1f54475a57ed77a",
                        1L,
                        List.of(produto()),
                        BigDecimal.ONE,
                        PagamentoEnum.PIX,
                        StatusEnum.PAGO
                ),
                new PedidoVo(
                        "664a6870d1f54475a57ed77a",
                        2L,
                        List.of(produto()),
                        BigDecimal.ONE,
                        PagamentoEnum.PIX,
                        StatusEnum.PAGO
                ),
                new PedidoVo(
                        "664a6870d1f54475a57ed77a",
                        3L,
                        List.of(produto()),
                        BigDecimal.ONE,
                        PagamentoEnum.PIX,
                        StatusEnum.PAGO
                ),
                new PedidoVo(
                        "664a6870d1f54475a57ed77a",
                        4L,
                        List.of(produto()),
                        BigDecimal.ONE,
                        PagamentoEnum.PIX,
                        StatusEnum.PAGO
                )
        );
    }

    private ProdutoVo produto(){
        return new ProdutoVo(
                1,
                "Descricao",
                BigDecimal.ONE
        );
    }

    private List<ClienteVo> cliente(){
        return List.of(
                new ClienteVo(
                        1L,
                        "Nome",
                        "11111111111",
                        "222222222",
                        "teste@gmail.com",
                        new EnderecoVo(
                                "11111111111",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                1.0,
                                1.0
                        )
                ),
                new ClienteVo(
                        2L,
                        "Nome",
                        "11111111111",
                        "222222222",
                        "teste@gmail.com",
                        new EnderecoVo(
                                "22222222222",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                1.0,
                                1.0
                        )
                ),
                new ClienteVo(
                        3L,
                        "Nome",
                        "11111111111",
                        "33333333333",
                        "teste@gmail.com",
                        new EnderecoVo(
                                "22222222222",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                1.0,
                                1.0
                        )
                ),
                new ClienteVo(
                        4L,
                        "Nome",
                        "44444444444",
                        "222222222",
                        "teste@gmail.com",
                        new EnderecoVo(
                                "22222222222",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                1.0,
                                1.0
                        )
                )
        );
    }

    private EntregadorResponse entregador(){
        return new EntregadorResponse(
                1,
                "Entregador",
                "44444444444",
                "entregador@gmail.com"
        );
    }

}
