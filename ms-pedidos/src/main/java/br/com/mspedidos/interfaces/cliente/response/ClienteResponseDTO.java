package br.com.mspedidos.interfaces.cliente.response;

import br.com.mspedidos.interfaces.cliente.valueobject.Endereco;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpf,
        String telefone,
        String email,
        Endereco endereco
) {
}