package br.com.cadastrocliente.mscadastrocliente.application.controller;

import br.com.cadastrocliente.mscadastrocliente.application.request.ClienteRequestDTO;
import br.com.cadastrocliente.mscadastrocliente.application.response.ClienteResponseDTO;
import br.com.cadastrocliente.mscadastrocliente.domain.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> obterTodos() {

        return ResponseEntity.ok(clienteService.obterTodos());
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@RequestBody ClienteRequestDTO clienteRequestDTO) {

        return ResponseEntity.status(201).body(clienteService.cadastrarCliente(clienteRequestDTO));
    }

    @GetMapping("buscar/{id}")
    public ResponseEntity<ClienteResponseDTO> obterClientePorId(@PathVariable Long id) {

        return ResponseEntity.ok(clienteService.obterClienteResponsePorId(id));
    }

    @DeleteMapping("deletar/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {

        clienteService.deletarCliente(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("atualizar/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Long id, @RequestBody ClienteRequestDTO clienteRequestDTO) {

        return ResponseEntity.ok(clienteService.atualizarCliente(id, clienteRequestDTO));
    }

}
