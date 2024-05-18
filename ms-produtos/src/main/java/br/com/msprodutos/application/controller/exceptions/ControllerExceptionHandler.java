package br.com.msprodutos.application.controller.exceptions;

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

        StandardError standardError = StandardError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error("Nao Encontrado Exception")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(EstoqueException.class)
    public ResponseEntity<StandardError> estoqueException(EstoqueException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        StandardError standardError = StandardError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error("Produto fora de estoque")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(standardError);
    }
}
