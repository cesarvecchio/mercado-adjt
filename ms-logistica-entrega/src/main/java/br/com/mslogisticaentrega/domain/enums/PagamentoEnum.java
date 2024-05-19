package br.com.mslogisticaentrega.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PagamentoEnum {
    CREDITO, DEBITO, PIX;
}
