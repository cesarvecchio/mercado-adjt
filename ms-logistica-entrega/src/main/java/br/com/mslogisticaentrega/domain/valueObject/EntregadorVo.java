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
        this.id = entregadorResponse.id();
        this.nome = entregadorResponse.nome();
        this.cpf = entregadorResponse.cpf();
        this.email = entregadorResponse.email();
    }
}
