package br.com.mslogisticaentrega.domain.valueObject;

public record Email(
        String destinatario,
        String assunto,
        String corpo
) {
}
