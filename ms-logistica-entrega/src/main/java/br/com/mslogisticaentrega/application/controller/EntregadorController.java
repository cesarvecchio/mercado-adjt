package br.com.mslogisticaentrega.application.controller;

import br.com.mslogisticaentrega.application.controller.request.EntregadorRequest;
import br.com.mslogisticaentrega.application.controller.response.EntregadorResponse;
import br.com.mslogisticaentrega.domain.service.ClienteService;
import br.com.mslogisticaentrega.domain.service.EntregadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entregadores")
public class EntregadorController {

    private final EntregadorService entregadorService;

    public EntregadorController(EntregadorService entregadorService) {
        this.entregadorService = entregadorService;
    }

    @GetMapping
    public ResponseEntity<List<EntregadorResponse>> buscarTOdos() {

        return ResponseEntity.ok(entregadorService.buscarTodos());
    }

    @PostMapping
    public ResponseEntity<EntregadorResponse> criar(@RequestBody EntregadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entregadorService.criar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntregadorResponse> buscar(@PathVariable Integer id) {

        return ResponseEntity.ok(entregadorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntregadorResponse> atualizar(@PathVariable Integer id, @RequestBody EntregadorRequest request) {
        return ResponseEntity.ok(entregadorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EntregadorResponse> deletar(@PathVariable Integer id) {
        entregadorService.deletar(id);

        return ResponseEntity.noContent().build();
    }
}
