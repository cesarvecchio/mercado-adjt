package br.com.cadastrocliente.mscadastrocliente.domain.valueObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Endereco {

    private String cep;

    private String logradouro;

    private String complemento;

    private String bairro;

    private String cidade;

    private String uf;

    private String numero;

    private Double latitude;

    private Double longitude;
}
