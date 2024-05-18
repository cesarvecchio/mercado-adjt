package br.com.cadastrocliente.mscadastrocliente.application.controller;

import br.com.cadastrocliente.mscadastrocliente.application.controller.exception.ControllerExceptionHandler;
import br.com.cadastrocliente.mscadastrocliente.application.controller.exception.CpfException;
import br.com.cadastrocliente.mscadastrocliente.application.controller.exception.NaoEncontradoException;
import br.com.cadastrocliente.mscadastrocliente.application.request.ClienteRequestDTO;
import br.com.cadastrocliente.mscadastrocliente.application.response.ClienteResponseDTO;
import br.com.cadastrocliente.mscadastrocliente.domain.service.ClienteService;
import br.com.cadastrocliente.mscadastrocliente.domain.valueObject.Endereco;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClienteControllerTest {

    MockMvc mockMvc;

    AutoCloseable mock;
    @Mock
    private ClienteService clienteService;

    @BeforeEach
    public void setup() {
        mock = MockitoAnnotations.openMocks(this);

        ClienteController clienteController = new ClienteController(clienteService);

        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class ObterClientes {

        @Test
        void obterTodos() throws Exception {

            var clientesResponse = buildClienteResponseDTO();
            when(clienteService.obterTodos()).thenReturn(List.of(clientesResponse));

            mockMvc.perform(get("/cliente")).andExpect(status().isOk());

            verify(clienteService, times(1)).obterTodos();
        }

        @Test
        void obterTodosVazio() throws Exception {

            when(clienteService.obterTodos()).thenReturn(List.of());

            mockMvc.perform(get("/cliente")).andExpect(status().isOk());

            verify(clienteService, times(1)).obterTodos();
        }

        @Test
        void obterClientePorId() throws Exception {

            var clienteResponse = buildClienteResponseDTO(3L);

            when(clienteService.obterClienteResponsePorId(3L)).thenReturn(clienteResponse);

            mockMvc.perform(get("/cliente/buscar/{id}", 3L))
                    .andExpect(status().isOk());

            verify(clienteService, times(1)).obterClienteResponsePorId(3L);
        }

        @Test
        void deveGerarExcessaoQuandoIdNaoEncontrado() throws Exception {

            when(clienteService.obterClienteResponsePorId(6L))
                    .thenThrow(NaoEncontradoException.class);

            mockMvc.perform(get("/cliente/buscar/{id}", 6L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(6L)))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("Nao Encontrado Exception");
                    });

            verify(clienteService, times(1)).obterClienteResponsePorId(6L);
        }

    }

    @Nested
    class CadastrarCliente {
        @Test
        void cadastrarCliente() throws Exception {

            var clienteRequestDTO = buildClienteRequestDTO();
            ClienteResponseDTO clienteResponseDTO = clienteService
                    .toResponseDTO(clienteService.toEntity(clienteRequestDTO));

            when(clienteService.cadastrarCliente(clienteRequestDTO)).thenReturn(clienteResponseDTO);

            mockMvc.perform(post("/cliente/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(clienteRequestDTO)))
                    .andExpect(status().isCreated());

            verify(clienteService, times(1)).cadastrarCliente(clienteRequestDTO);
        }

        @Test
        void deveGerarExcessaoQuandoCpfJaCadastrado() throws Exception {
            var clienteRequestDTO = buildClienteRequestDTO();
            when(clienteService.cadastrarCliente(clienteRequestDTO)).thenThrow(CpfException.class);

            mockMvc.perform(post("/cliente/cadastrar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(clienteRequestDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("Cpf Exception");
                    });
        }
    }

    @Nested
    class AtualizarCliente {

        @Test
        void atualizarCliente() throws Exception {

            var clienteRequestDTO = buildClienteRequestDTO();
            ClienteResponseDTO clienteResponseDTO = clienteService
                    .toResponseDTO(clienteService.toEntity(clienteRequestDTO));

            when(clienteService.atualizarCliente(1L, clienteRequestDTO)).thenReturn(clienteResponseDTO);

            mockMvc.perform(put("/cliente/atualizar/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteRequestDTO)))
                    .andExpect(status().isOk());

            verify(clienteService, times(1)).atualizarCliente(1L, clienteRequestDTO);

        }

        @Test
        void deveGerarExcessaoQuandoCpfJaCadastrado() throws Exception {

            var clienteRequestDTO = buildClienteRequestDTO();

            when(clienteService.atualizarCliente(1L ,clienteRequestDTO)).thenThrow(CpfException.class);

            mockMvc.perform(put("/cliente/atualizar/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteRequestDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("Cpf Exception");
                    });

            verify(clienteService, times(1)).atualizarCliente(1L, clienteRequestDTO);
        }

        /*@Test
        void deveGerarExcessaoQuandoIdNaoEncontrado() throws Exception {

            var clienteRequestDTO = buildClienteRequestDTO();

            when(clienteService.atualizarCliente(4L ,clienteRequestDTO))
                    .thenThrow(NaoEncontradoException.class);

            mockMvc.perform(put("/cliente/atualizar/{id}", 4L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(4L)))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("Nao Encontrado Exception");
                    });

            verify(clienteService, times(1)).atualizarCliente(4L, clienteRequestDTO);

        }*/
    }

    @Nested
    class DeletarCliente {

        @Test
        void deletarCliente() throws Exception {

            doNothing().when(clienteService).deletarCliente(1L);


            mockMvc.perform(delete("/cliente/deletar/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(clienteService, times(1)).deletarCliente(1L);
        }
    }



    private ClienteResponseDTO buildClienteResponseDTO() {
        return new ClienteResponseDTO(
                1L,
                "Guilherme Matos de Carvalho",
                "12345678910",
                "11987654321",
                "8Xa5I@example.com",
                    new Endereco(
                            "14785236",
                            "123",
                            "Casa",
                            "Centro",
                            "São Paulo",
                            "SP",
                            "12345678",
                            1111.54551,
                            111122.226
                    )
                );
    }

    private ClienteResponseDTO buildClienteResponseDTO(Long id) {
        return new ClienteResponseDTO(
                id,
                "Guilherme Matos de Carvalho",
                "12345678910",
                "11987654321",
                "8Xa5I@example.com",
                new Endereco(
                        "14785236",
                        "123",
                        "Casa",
                        "Centro",
                        "São Paulo",
                        "SP",
                        "12345678",
                        1111.54551,
                        111122.226
                )
        );
    }

    private ClienteRequestDTO buildClienteRequestDTO() {
        return new ClienteRequestDTO(
                "Guilherme Matos de Carvalho",
                "8Xa5I@example.com",
                "Sdsadwd21321@#$",
                "11987654321",
                "12345678910",
                "14785236",
                new Endereco(
                        "14785236",
                        "123",
                        "Casa",
                        "Centro",
                        "São Paulo",
                        "SP",
                        "12345678",
                        1111.54551,
                        111122.226
                )
        );
    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
