package br.com.mslogisticaentrega.domain.valueObject;

public record PedidoClienteVo (
        PedidoVo pedido,
        ClienteVo cliente
    )
{}
