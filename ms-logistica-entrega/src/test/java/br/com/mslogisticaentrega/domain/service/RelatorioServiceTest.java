package br.com.mslogisticaentrega.domain.service;

import br.com.mslogisticaentrega.domain.enums.PagamentoEnum;
import br.com.mslogisticaentrega.domain.enums.StatusEnum;
import br.com.mslogisticaentrega.domain.valueObject.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RelatorioServiceTest {
    @Mock
    private EmailService emailService;
    private RelatorioService relatorioService;
    AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        relatorioService = new RelatorioService(emailService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void deveGerarRelatorio(){
        List<String> idPedidoList = new ArrayList<>();

        doNothing().when(emailService).sendEmail(any(Email.class));

        relatorioService.gerarRelatorio(entregador(), idPedidoList);

        assertEquals(List.of("664a6870d1f54475a57ed77a"), idPedidoList);

        verify(emailService).sendEmail(any(Email.class));
    }

    private EntregadorVo entregador(){
        return new EntregadorVo(
                1,
                "Nome",
                "cpf",
                "email",
                List.of(
                        pedidoCliente()
                )
        );
    }

    private PedidoClienteVo pedidoCliente(){
        return new PedidoClienteVo(
                pedido(),
                cliente()
        );
    }

    private PedidoVo pedido(){
        return new PedidoVo(
                "664a6870d1f54475a57ed77a",
                1L,
                List.of(
                        produto()
                ),
                BigDecimal.ONE,
                PagamentoEnum.PIX,
                StatusEnum.AGUARDANDO_ENTREGA
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
                "nome",
                "cpf",
                "telefone",
                "email",
                endereco()
        );
    }

    private EnderecoVo endereco(){
        return new EnderecoVo(
                "cep",
                "logradouro",
                "complemento",
                "bairro",
                "cidade",
                "uf",
                "numero",
                1.0,
                1.0
        );
    }
}
