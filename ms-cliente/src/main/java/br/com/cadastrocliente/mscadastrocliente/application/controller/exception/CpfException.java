package br.com.cadastrocliente.mscadastrocliente.application.controller.exception;

public class CpfException extends RuntimeException {
    public CpfException(String message) {
        super(message);
    }

    public CpfException(String message, Throwable cause) {
        super(message, cause);
    }
}
