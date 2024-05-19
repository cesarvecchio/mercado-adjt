package br.com.mslogisticaentrega.infra.config;

import br.com.mslogisticaentrega.infra.exceptions.NaoEncontradoException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            if(methodKey.contains("Cliente")){

                return new NaoEncontradoException("Cliente não encontrado");
            }

            return new NaoEncontradoException("Recurso não encontrado");
        }

        return new Exception("Erro genérico");
    }
}