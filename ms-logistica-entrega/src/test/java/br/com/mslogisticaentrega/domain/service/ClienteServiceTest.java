package br.com.mslogisticaentrega.domain.service;

import br.com.mslogisticaentrega.domain.valueObject.ClienteVo;
import br.com.mslogisticaentrega.infra.client.ClienteClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClienteServiceTest {
    @Mock
    private ClienteClient clienteClient;
    private ClienteService clienteService;
    AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        clienteService = new ClienteService(clienteClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void deveObterClientePorId(){
        ClienteVo cliente = cliente();
        Long id = cliente.id();

        when(clienteClient.obterClientePorId(id)).thenReturn(cliente);

        var resultado = clienteService.obterClientePorId(id);

        assertEquals(cliente, resultado);

        verify(clienteClient).obterClientePorId(id);
    }

    private ClienteVo cliente(){
        return new ClienteVo(
                1L,
                "Teste",
                "cpf",
                "telefone",
                "email",
                null
        );
    }
}
