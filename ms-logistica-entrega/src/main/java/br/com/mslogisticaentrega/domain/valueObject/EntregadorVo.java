package br.com.mslogisticaentrega.domain.valueObject;

import br.com.mslogisticaentrega.application.controller.response.EntregadorResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntregadorVo {
    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private List<PedidoClienteVo> pedidoClienteList;

    public EntregadorVo(EntregadorResponse entregadorResponse) {
        this.id = entregadorResponse.getId();
        this.nome = entregadorResponse.getNome();
        this.cpf = entregadorResponse.getCpf();
        this.email = entregadorResponse.getEmail();
    }
}
