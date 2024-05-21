package br.com.mspedidos.interfaces.cliente;

import br.com.mspedidos.interfaces.cliente.response.ClienteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-cliente", path="/cliente")
public interface ClienteInterface {

    @GetMapping("/buscar/{id}")
    ClienteResponseDTO buscarCliente(@PathVariable Long id);
}
