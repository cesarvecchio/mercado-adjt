package br.com.mslogisticaentrega.domain.valueObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoVo {
    private Integer idProduto;
    private String descricao;
    private BigDecimal valor;
    private String marca;
}
