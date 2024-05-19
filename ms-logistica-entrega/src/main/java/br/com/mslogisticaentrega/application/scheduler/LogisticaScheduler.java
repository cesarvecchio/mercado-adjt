package br.com.mslogisticaentrega.application.scheduler;

import br.com.mslogisticaentrega.application.controller.response.EntregadorResponse;
import br.com.mslogisticaentrega.domain.service.ClienteService;
import br.com.mslogisticaentrega.domain.service.EntregadorService;
import br.com.mslogisticaentrega.domain.service.PedidoService;
import br.com.mslogisticaentrega.domain.service.RelatorioService;
import br.com.mslogisticaentrega.domain.valueObject.*;
import br.com.mslogisticaentrega.infra.exceptions.NaoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LogisticaScheduler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final EntregadorService entregadorService;
    private final RelatorioService relatorioService;

    public LogisticaScheduler(PedidoService pedidoService, ClienteService clienteService,
                              EntregadorService entregadorService, RelatorioService relatorioService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.entregadorService = entregadorService;
        this.relatorioService = relatorioService;
    }

//    @Scheduled(initialDelay = 1000, fixedDelay = 300000)
    public void processarLogistica() {
        try {
            logger.info("==== Processo de Logistica Iniciado =====");

            List<PedidoClienteVo> pedidoClienteList = new ArrayList<>();
            List<PedidoVo> pedidoList = pedidoService.buscarPedidoPago();

            vincularPedidoCliente(pedidoClienteList, pedidoList);

            Map<String, List<PedidoClienteVo>> grupoPedidos =  pedidoClienteList.stream().collect(Collectors.groupingBy(pc -> {
                return String.valueOf(pc.getCliente().getEndereco().getCep().toCharArray()[3]);
            }));

            List<EntregadorResponse> entregadorList = entregadorService.buscarTodos();

            List<EntregadorVo> entregadorVoList = atribuirPedidosEntregador(entregadorList, grupoPedidos);

            List<String> idPedidoList = new ArrayList<>();

            for (EntregadorVo entregadorVo : entregadorVoList) {
                relatorioService.gerarRelatorio(entregadorVo, idPedidoList);
            }

            pedidoService.atualizarPedidosAguardandoEntrega(idPedidoList);

        } catch (NaoEncontradoException e) {
            logger.info(e.getMessage());
        }

        logger.info("==== Processo de Logistica Finalizado =====");
    }

    private void vincularPedidoCliente(List<PedidoClienteVo> pedidoClienteList, List<PedidoVo> pedidoList){
        logger.info("Iniciando Vinculacao de Pedidos aos Clientes");

        for(PedidoVo pedido : pedidoList) {
            ClienteVo cliente = clienteService.obterClientePorId(Long.valueOf(pedido.getIdCliente()));

            pedidoClienteList.add(new PedidoClienteVo(pedido, cliente));
        }
        logger.info("Finalizando Vinculacao de Pedidos aos Clientes");
    }

    private List<EntregadorVo> atribuirPedidosEntregador(List<EntregadorResponse> entregadorList, Map<String, List<PedidoClienteVo>> grupoPedidos){
        logger.info("Iniciando Atribuicao Pedidos aos Entregadores");

        int qtdEntregadores = entregadorList.size();

        List<EntregadorVo> entregadorVoList = new ArrayList<>();

        int posicaoEntregador = 1;

        for (List<PedidoClienteVo> listaPedidos: grupoPedidos.values()){
            EntregadorVo entregadorVo = new EntregadorVo(entregadorList.get(posicaoEntregador-1));

            entregadorVo.setPedidoClienteList(listaPedidos);

            entregadorVoList.add(entregadorVo);

            if(posicaoEntregador == qtdEntregadores){
                posicaoEntregador = 1;
                continue;
            }
            posicaoEntregador++;
        }
        logger.info("Finalizando Atribuicao Pedidos aos Entregadores");

        return entregadorVoList;
    }

}
