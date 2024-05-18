package br.com.mspedidos.application.request;

import br.com.mspedidos.domain.enums.StatusEnum;

import java.util.List;

public record AtualizarStatusLoteRequestDTO(
        List<String> idsPedidos,
        StatusEnum status
) {
}
