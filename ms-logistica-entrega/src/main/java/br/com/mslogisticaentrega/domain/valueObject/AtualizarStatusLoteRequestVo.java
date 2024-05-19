package br.com.mslogisticaentrega.domain.valueObject;

import br.com.mslogisticaentrega.domain.enums.StatusEnum;

import java.util.List;

public record AtualizarStatusLoteRequestVo(
        List<String> idsPedidos,
        StatusEnum status
) {
}
