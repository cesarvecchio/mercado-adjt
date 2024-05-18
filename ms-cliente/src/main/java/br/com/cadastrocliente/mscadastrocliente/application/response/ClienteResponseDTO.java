package br.com.cadastrocliente.mscadastrocliente.application.response;

import br.com.cadastrocliente.mscadastrocliente.domain.valueObject.Endereco;

public record ClienteResponseDTO(
    Long id,
    String nome,
    String cpf,
    String telefone,
    String email,
    Endereco endereco
) {
}
