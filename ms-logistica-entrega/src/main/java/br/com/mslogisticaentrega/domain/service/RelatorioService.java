package br.com.mslogisticaentrega.domain.service;

import br.com.mslogisticaentrega.domain.valueObject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelatorioService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final EmailService emailService;

    public RelatorioService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void gerarRelatorio(EntregadorVo entregadorVo, List<String> idPedidoList){
        logger.info("Gerando Relatorio...");
        List<PedidoClienteVo> pedidoClienteList = entregadorVo.getPedidoClienteList();

        StringBuilder relatorio = new StringBuilder("\n");

        for (PedidoClienteVo pedidoClienteVo : pedidoClienteList){
            PedidoVo pedido = pedidoClienteVo.pedido();
            ClienteVo cliente = pedidoClienteVo.cliente();

            relatorio.append(String.format("""
                            ========================================
                            Cliente
                            %s
                                
                            Pedidos
                            ID: %s
                            %s
                                
                                 Valor total: R$%.2f
                            
                            
                            """,
                    fomatarClienteRelatorio(cliente),
                    pedido.idPedido(),
                    formatarProdutosRelatorio(pedido.produtos()),
                    pedido.valorTotal()));

            idPedidoList.add(pedido.idPedido());
        }

        Email email = new Email(entregadorVo.getEmail(), "Pedidos a ser entregue", relatorio.toString());

        logger.info("Relatorio Gerado...");

        emailService.sendEmail(email);
    }

    private String fomatarClienteRelatorio(ClienteVo cliente) {
        return String.format("""
                Id: %d    |Nome Cliente: %s    |Cpf:%s    |Email:%s    |Cep:%s
                """,
                cliente.id(),
                cliente.nome(),
                cliente.cpf(),
                cliente.email(),
                cliente.endereco().cep());
    }

    private String formatarProdutosRelatorio(List<ProdutoVo> produtoVoList){
        StringBuilder relatorioPrdutos = new StringBuilder();

        for (ProdutoVo produtoVo : produtoVoList) {
            relatorioPrdutos.append(String.format("""
                    Descricao: %s     Valor: R$%.2f
                    """,
                    produtoVo.descricao(),
                    produtoVo.valor()));
        }

        return relatorioPrdutos.toString();
    }
}
