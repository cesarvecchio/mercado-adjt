package br.com.mslogisticaentrega.application.controller.response;

import br.com.mslogisticaentrega.domain.entity.EntregadorEntity;

public record EntregadorResponse (
        Integer id,
        String nome,
        String cpf,
        String email

){}
