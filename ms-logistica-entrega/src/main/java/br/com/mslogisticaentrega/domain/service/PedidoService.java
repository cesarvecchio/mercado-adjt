package br.com.mslogisticaentrega.domain.service;

import br.com.mslogisticaentrega.domain.enums.StatusEnum;
import br.com.mslogisticaentrega.domain.valueObject.ClienteVo;
import br.com.mslogisticaentrega.domain.valueObject.Email;
import br.com.mslogisticaentrega.domain.valueObject.PedidoVo;
import br.com.mslogisticaentrega.infra.client.PedidoClient;
import br.com.mslogisticaentrega.infra.exceptions.NaoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PedidoClient pedidoClient;
    private final ClienteService clienteService;
    private final EmailService emailService;

    public PedidoService(@Qualifier("pedidoClientMock") PedidoClient pedidoClient, ClienteService clienteService, EmailService emailService) {
        this.pedidoClient = pedidoClient;
        this.clienteService = clienteService;
        this.emailService = emailService;
    }

    public List<PedidoVo> buscarPedidoPago() {
        logger.info("Buscando Pedido com status:[{}]", StatusEnum.PAGO);
        List<PedidoVo> pedidoVoList = pedidoClient.buscarPedidoPago();

        if(pedidoVoList.isEmpty()){
            throw new NaoEncontradoException(
                    String.format("Nenhum pedido com status:[%s]", StatusEnum.PAGO));
        }

        return pedidoVoList;
    }

    public void atualizarPedidosAguardandoEntrega(List<String> idPedidoLista){
        pedidoClient.atualizarPedidosAguardandoEntrega(idPedidoLista);
    }

    public void atualizarPedidoEntregue(String idPedido){
        pedidoClient.atualizarPedidoEntregue(idPedido);

        ClienteVo clienteVo = clienteService.obterClientePorId(1L);

        Email email = new Email(clienteVo.getEmail(), "Pedido Entregue", "Ah entrega do seu pedido foi realizada com sucesso!");

        emailService.sendEmail(email);
    }
}
