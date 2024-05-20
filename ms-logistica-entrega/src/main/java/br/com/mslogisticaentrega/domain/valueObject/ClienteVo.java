package br.com.mslogisticaentrega.domain.valueObject;

public record ClienteVo (
        Long id,
        String nome,
        String cpf,
        String telefone,
        String email,
        EnderecoVo endereco
        )
{}
