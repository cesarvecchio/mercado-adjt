package br.com.mslogisticaentrega.infra.client.mock;

import br.com.mslogisticaentrega.domain.enums.PagamentoEnum;
import br.com.mslogisticaentrega.domain.enums.StatusEnum;
import br.com.mslogisticaentrega.domain.valueObject.PedidoVo;
import br.com.mslogisticaentrega.domain.valueObject.ProdutoVo;
import br.com.mslogisticaentrega.infra.client.PedidoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class PedidoClientMock implements PedidoClient {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public List<PedidoVo> buscarPedidoPago() {

        List<ProdutoVo> produtoVoList = new ArrayList<>();
        produtoVoList.add(new ProdutoVo(1, "Açucar", BigDecimal.valueOf(20.00), "União"));
        produtoVoList.add(new ProdutoVo(1, "Pasta de dente", BigDecimal.valueOf(5.00), "Oral-B"));

        List<PedidoVo> pedidoVoList = new ArrayList<>();

        pedidoVoList.add(new PedidoVo("1", 1, produtoVoList,
                BigDecimal.valueOf(25.00), PagamentoEnum.PIX, StatusEnum.PAGO
        ));

        pedidoVoList.add(new PedidoVo("2", 2, produtoVoList,
                BigDecimal.valueOf(25.00), PagamentoEnum.PIX, StatusEnum.PAGO
        ));

        pedidoVoList.add(new PedidoVo("4", 4, produtoVoList,
                BigDecimal.valueOf(25.00), PagamentoEnum.PIX, StatusEnum.PAGO
        ));

        pedidoVoList.add(new PedidoVo("3", 3, produtoVoList,
                BigDecimal.valueOf(25.00), PagamentoEnum.PIX, StatusEnum.PAGO
        ));

        pedidoVoList.add(new PedidoVo("5", 3, produtoVoList,
                BigDecimal.valueOf(25.00), PagamentoEnum.PIX, StatusEnum.PAGO
        ));


        return pedidoVoList;
    }

    @Override
    public void atualizarPedidosAguardandoEntrega(List<String> idPedidoList) {
        logger.info("Pedidos com ids:[{}] atualizados para o status AGUARDANDO_ENTREGA", idPedidoList);
    }

    @Override
    public void atualizarPedidoEntregue(String idPedido) {
        logger.info("Pedido com id:[{}] atualizado para o status ENTREGUE", idPedido);
    }
}
