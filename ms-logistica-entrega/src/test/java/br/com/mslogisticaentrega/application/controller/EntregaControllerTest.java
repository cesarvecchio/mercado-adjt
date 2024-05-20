package br.com.mslogisticaentrega.application.controller;

import br.com.mslogisticaentrega.domain.service.PedidoService;
import br.com.mslogisticaentrega.infra.exceptions.ControllerExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EntregaControllerTest {
    private MockMvc mockMvc;
    @Mock
    private PedidoService pedidoService;
    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);

        EntregaController entregaController = new EntregaController(pedidoService);

        mockMvc = MockMvcBuilders.standaloneSetup(entregaController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    void deveAtualizarPedidoEntregue() throws Exception {
        var idPedido = "664a6870d1f54475a57ed77a";

        doNothing().when(pedidoService).atualizarPedidoEntregue(idPedido);

        mockMvc.perform(put("/entregas/{idPedido}", idPedido)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        verify(pedidoService).atualizarPedidoEntregue(idPedido);
    }

}
