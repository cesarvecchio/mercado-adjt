package br.com.mspedidos.domain.service;

import br.com.mspedidos.application.controller.exceptions.EstoqueException;
import br.com.mspedidos.application.controller.exceptions.NaoEncontradoException;
import br.com.mspedidos.application.controller.exceptions.PagamentoException;
import br.com.mspedidos.application.request.AtualizarStatusLoteRequestDTO;
import br.com.mspedidos.application.request.PedidoRequestDTO;
import br.com.mspedidos.application.response.PedidoResponseDTO;
import br.com.mspedidos.domain.entity.Pedido;
import br.com.mspedidos.domain.enums.StatusEnum;
import br.com.mspedidos.domain.valueobject.Produto;
import br.com.mspedidos.infra.repository.PedidoRepository;
import br.com.mspedidos.interfaces.StandardError;
import br.com.mspedidos.interfaces.cliente.ClienteInterface;
import br.com.mspedidos.interfaces.produto.ProdutoInterface;
import br.com.mspedidos.interfaces.produto.response.ProdutoResponseDTO;
import br.com.mspedidos.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteInterface clienteInterface;
    private final ProdutoInterface produtoInterface;
    private final Utils utils;

    private static final String MSG_PEDIDO_NAO_ENCONTRADO = "Pedido com o id '%s' não encontrado!";

    public PedidoService(PedidoRepository pedidoRepository, ClienteInterface clienteInterface, ProdutoInterface produtoInterface, Utils utils) {
        this.pedidoRepository = pedidoRepository;
        this.clienteInterface = clienteInterface;
        this.produtoInterface = produtoInterface;
        this.utils = utils;
    }

    public PedidoResponseDTO criarPedido(Long idCliente, PedidoRequestDTO pedidoRequestDTO) throws JsonProcessingException {

        try {
            clienteInterface.buscarCliente(idCliente);
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("Nao Encontrado Exception".toLowerCase())) {
                StandardError error = this.utils.getErrorMessageFeignIntegration(e.getMessage());
                throw new NaoEncontradoException(error.getMessage());
            }
        }

        try {
            List<Produto> produtosEncontrados = produtoInterface.consultaEDaBaixaNoEstoque(pedidoRequestDTO.produtos())
                    .stream()
                    .map(this::toValueObjectProduto)
                    .toList();

            Pedido pedido = getPedido(idCliente, pedidoRequestDTO, produtosEncontrados);

            return toResponseDTO(pedidoRepository.save(pedido));

        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("Nao Encontrado Exception".toLowerCase())) {
                StandardError error = this.utils.getErrorMessageFeignIntegration(e.getMessage());
                throw new NaoEncontradoException(error.getMessage());
            }
            if (e.getMessage().toLowerCase().contains("Produto fora de estoque".toLowerCase())) {
                StandardError error = this.utils.getErrorMessageFeignIntegration(e.getMessage());
                throw new EstoqueException(error.getMessage());
            }
        }

        return null;
    }

    public PedidoResponseDTO efetuarPagamentoPedido(String idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(() ->
                new NaoEncontradoException(String
                        .format(MSG_PEDIDO_NAO_ENCONTRADO, idPedido)
                ));
        if (pedido.getStatus() != StatusEnum.AGUARDANDO_PAGAMENTO) {
            throw new PagamentoException("Não é possível efetuar o pagamento de um pedido que não esteja aguardando pagamento!");
        }
        pedido.setStatus(StatusEnum.PAGO);
        return toResponseDTO(pedidoRepository.save(pedido));
    }

    public PedidoResponseDTO buscarPedido(String idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(() ->
                new NaoEncontradoException(String
                        .format(MSG_PEDIDO_NAO_ENCONTRADO, idPedido)
                ));
        return toResponseDTO(pedido);
    }

    public List<PedidoResponseDTO> buscarPedidosPagos() {
        List<Pedido> pedido = pedidoRepository.findByStatus(StatusEnum.PAGO);
        return pedido.stream().map(this::toResponseDTO).toList();
    }

    public PedidoResponseDTO atualizarStatusPedido(String idPedido, StatusEnum status) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(() ->
                new NaoEncontradoException(String
                        .format(MSG_PEDIDO_NAO_ENCONTRADO, idPedido)
                ));
        pedido.setStatus(status);
        return toResponseDTO(pedidoRepository.save(pedido));
    }

    public List<PedidoResponseDTO> atualizarStatusPedidoEmLote(AtualizarStatusLoteRequestDTO atualizarStatusLoteRequestDTO) {
        List<Pedido> pedidosASeremAtualizados = new ArrayList<>();
        for (String idPedido : atualizarStatusLoteRequestDTO.idsPedidos()) {
            Optional<Pedido> pedidoOptional = pedidoRepository.findById(idPedido);
            if (pedidoOptional.isPresent()) {
                pedidoOptional.get().setStatus(atualizarStatusLoteRequestDTO.status());
                pedidosASeremAtualizados.add(pedidoOptional.get());
            }
        }

        return pedidoRepository.saveAll(pedidosASeremAtualizados).stream().map(this::toResponseDTO).toList();
    }

    private static Pedido getPedido(Long idCliente, PedidoRequestDTO pedidoRequestDTO, List<Produto> produtosEncontrados) {

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

    public Produto toValueObjectProduto(ProdutoResponseDTO produtoResponseDTO) {
        return new Produto(produtoResponseDTO.id(),
                produtoResponseDTO.descricao(),
                produtoResponseDTO.quantidade(),
                produtoResponseDTO.valor()
        );
    }

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        return new PedidoResponseDTO(
                pedido.getIdPedido(),
                pedido.getProdutos(),
                pedido.getValorTotal(),
                pedido.getFormaPagamento(),
                pedido.getStatus()
        );
    }
}
