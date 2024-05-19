package br.com.mslogisticaentrega.domain.valueObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteVo {
    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private EnderecoVo endereco;
}
