package br.com.mslogisticaentrega.domain.entity;

import br.com.mslogisticaentrega.domain.enums.PagamentoEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_LOGISTICA_ENTREGA")
public class LogisticaEntregaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String idPedido;
    private PagamentoEnum formaPagamento;

    @ManyToOne
    private EntregadorEntity entregadorService;
}
