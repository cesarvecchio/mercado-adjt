package br.com.mslogisticaentrega.infra.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<StandardError> naoEncontradoException(NaoEncontradoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError standardError = new StandardError(
                Instant.now(),
                status.value(),
                "Nao Encontrado Exception",
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(JaCadastradoException.class)
    public ResponseEntity<StandardError> jaCadastradoException(JaCadastradoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        StandardError standardError = new StandardError(
                Instant.now(),
                status.value(),
                "Ja Cadastrado Exception",
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(standardError);
    }
}
