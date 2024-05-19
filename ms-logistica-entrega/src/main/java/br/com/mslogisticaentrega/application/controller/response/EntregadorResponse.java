package br.com.mslogisticaentrega.application.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import br.com.mslogisticaentrega.domain.entity.EntregadorEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntregadorResponse {
    private Integer id;
    private String nome;
    private String cpf;
    private String email;

    public EntregadorResponse(EntregadorEntity entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.cpf = entity.getCpf();
        this.email = entity.getEmail();
    }
}
