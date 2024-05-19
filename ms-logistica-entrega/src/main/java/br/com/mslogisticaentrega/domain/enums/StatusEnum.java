package br.com.mslogisticaentrega.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    AGUARDANDO_PAGAMENTO, PAGO, AGUARDANDO_ENTREGA, ENTREGUE;
}
