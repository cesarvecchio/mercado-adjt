package br.com.mslogisticaentrega.application.controller;

import br.com.mslogisticaentrega.application.controller.request.EntregadorRequest;
import br.com.mslogisticaentrega.application.controller.response.EntregadorResponse;
import br.com.mslogisticaentrega.domain.service.EntregadorService;
import br.com.mslogisticaentrega.infra.exceptions.ControllerExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EntregadorControllerTest {
    private MockMvc mockMvc;
    @Mock
    private EntregadorService entregadorService;
    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);

        EntregadorController avaliacaoController = new EntregadorController(entregadorService);

        mockMvc = MockMvcBuilders.standaloneSetup(avaliacaoController)
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
    void deveBuscarTodos() throws Exception {
        var listaRetorno = List.of(entregadorResponse());

        when(entregadorService.buscarTodos()).thenReturn(listaRetorno);

        mockMvc.perform(get("/entregadores")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(entregadorService).buscarTodos();
    }

    @Test
    void deveCriar() throws Exception {
        var entregadorRequest = entregadorRequest();
        var entregadorEntity = entregadorRequest.toEntity();
        entregadorEntity.setId(1);
        var entregadorResponse = entregadorService.toResponse(entregadorEntity);

        when(entregadorService.criar(entregadorRequest)).thenReturn(entregadorResponse);

        mockMvc.perform(post("/entregadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(entregadorRequest))
                )
                .andExpect(status().isCreated());

        verify(entregadorService).criar(entregadorRequest);
    }

    @Test
    void deveBuscar() throws Exception {
        var entregadorResponse = entregadorResponse();

        when(entregadorService.buscarPorId(entregadorResponse.id())).thenReturn(entregadorResponse);

        mockMvc.perform(get("/entregadores/{id}", entregadorResponse.id())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(entregadorService).buscarPorId(entregadorResponse.id());
    }

    @Test
    void deveAtualizar() throws Exception {
        var entregadorRequest = entregadorRequest();
        var entregadorEntity = entregadorRequest.toEntity();
        entregadorEntity.setId(1);
        var entregadorResponse = entregadorService.toResponse(entregadorEntity);

        when(entregadorService.atualizar(entregadorEntity.getId(), entregadorRequest)).thenReturn(entregadorResponse);

        mockMvc.perform(put("/entregadores/{id}", entregadorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(entregadorRequest))
                )
                .andExpect(status().isOk());

        verify(entregadorService).atualizar(entregadorEntity.getId(), entregadorRequest);
    }

    @Test
    void deveDeletar() throws Exception {
        var id = 1;

        doNothing().when(entregadorService).deletar(id);

        mockMvc.perform(delete("/entregadores/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        verify(entregadorService).deletar(id);
    }

    public static String asJsonString(Object object) throws JsonProcessingException {
        return new ObjectMapper().findAndRegisterModules().writeValueAsString(object);
    }

    private EntregadorRequest entregadorRequest(){
        return new EntregadorRequest(
                "Albert",
                "11111111111",
                "albert@gmail.com"
        );
    }

    private EntregadorResponse entregadorResponse(){
        return new EntregadorResponse(
                1, "Albert", "1111111111", "albert@gmail.com"
        );
    }
}
