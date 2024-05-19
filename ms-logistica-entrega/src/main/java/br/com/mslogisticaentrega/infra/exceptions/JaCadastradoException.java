package br.com.mslogisticaentrega.infra.exceptions;

public class JaCadastradoException extends RuntimeException {
    public JaCadastradoException(String message) {
        super(message);
    }

    public JaCadastradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
