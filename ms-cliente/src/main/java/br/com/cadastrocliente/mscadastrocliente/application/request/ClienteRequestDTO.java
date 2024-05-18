package br.com.cadastrocliente.mscadastrocliente.application.request;

import br.com.cadastrocliente.mscadastrocliente.domain.valueObject.Endereco;

public record ClienteRequestDTO(
    String nome,
    String email,
    String senha,
    String telefone,
    String cpf,
    String rg,

    Endereco endereco
) {
}
