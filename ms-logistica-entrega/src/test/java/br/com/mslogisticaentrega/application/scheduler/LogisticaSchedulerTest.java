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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

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
        PedidoVo pedidoVo = pedido();
        ClienteVo cliente = cliente();
        EntregadorResponse entregador = entregador();

        when(pedidoService.buscarPedidosPagos()).thenReturn(List.of(pedidoVo));
        when(clienteService.obterClientePorId(pedidoVo.idCliente())).thenReturn(cliente);
        when(entregadorService.buscarTodos()).thenReturn(List.of(entregador));

        logisticaScheduler.processarLogistica();

    }

    private PedidoVo pedido(){
        return new PedidoVo(
                "664a6870d1f54475a57ed77a",
                1L,
                List.of(produto()),
                BigDecimal.ONE,
                PagamentoEnum.PIX,
                StatusEnum.PAGO
        );
    }

    private ProdutoVo produto(){
        return new ProdutoVo(
                1,
                "Descricao",
                BigDecimal.ONE
        );
    }

    private ClienteVo cliente(){
        return new ClienteVo(
                1L,
                "Nome",
                "11111111111",
                "222222222",
                "teste@gmail.com",
                new EnderecoVo(
                        "33333333",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        1.0,
                        1.0
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
