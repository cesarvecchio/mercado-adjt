package br.com.mslogisticaentrega.domain.valueObject;

public record EnderecoVo (
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        String numero,
        Double latitude,
        Double longitude
){}
