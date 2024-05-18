package br.com.msprodutos.domain.enitity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String descricao;
    private Integer quantidadeEstoque;
    private BigDecimal valor;

    public Produto(String descricao, Integer quantidadeEstoque, BigDecimal valor) {
        this.descricao = descricao;
        this.quantidadeEstoque = quantidadeEstoque;
        this.valor = valor;
    }
}
