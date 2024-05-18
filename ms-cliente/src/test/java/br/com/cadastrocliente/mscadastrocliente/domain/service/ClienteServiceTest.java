package br.com.cadastrocliente.mscadastrocliente.domain.service;

import br.com.cadastrocliente.mscadastrocliente.application.controller.exception.CpfException;
import br.com.cadastrocliente.mscadastrocliente.application.controller.exception.NaoEncontradoException;
import br.com.cadastrocliente.mscadastrocliente.application.request.ClienteRequestDTO;
import br.com.cadastrocliente.mscadastrocliente.application.response.ClienteResponseDTO;
import br.com.cadastrocliente.mscadastrocliente.domain.entity.Cliente;
import br.com.cadastrocliente.mscadastrocliente.domain.valueObject.Endereco;
import br.com.cadastrocliente.mscadastrocliente.infra.repository.ClienteRespository;
import br.com.cadastrocliente.mscadastrocliente.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ClienteServiceTest {

    private ClienteService clienteService;

    @Mock
    private ClienteRespository clienteRespository;


    @Mock
    private Utils utils;
    AutoCloseable autoCloseable;

    @BeforeEach
    public void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        clienteService = new ClienteService(clienteRespository, utils);
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }


    @Nested
    class BuscarCliente {

        @Test
        void deveRetornarClientePorId() {
            // Configurar um cliente existente na base de dados
            var clienteExistente = clienteService.toEntity(buildClienteRequestDTO()); // Substitua pelo construtor real do Cliente
            when(clienteRespository.findById(1L))
                    .thenReturn(Optional.of(clienteExistente));

            var clienteResponseExistente = clienteService.toResponseDTO(clienteExistente);
            // Chamar o método a ser testado
            var clienteResponseDTO = clienteService.obterClienteResponsePorId(1L);


            // Verificar o resultado
            assertEquals(clienteResponseExistente, clienteResponseDTO);

            verify(clienteRespository, times(1)).findById(1L);
        }

        @Test
        void deveGerarExcessaoQuandoIdNaoEncontrado() {

            when(clienteRespository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.obterClienteResponsePorId(1L))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(String.format("Cliente com o id '%d' não encontrado", 1L));

            verify(clienteRespository, times(1)).findById(1L);
        }

        @Test
        void deveBuscarTodosOsClientes() {
            // Configurar um cliente existente na base de dados
            var clienteExistente = clienteService.toEntity(buildClienteRequestDTO());
            when(clienteRespository.findAll())
                    .thenReturn(List.of(clienteExistente));

            var clienteExistenteResponse = clienteService.toResponseDTO(clienteExistente);
            // Chamar o método a ser testado
            var clienteResponseDTO = clienteService.obterTodos();

            assertEquals(List.of(clienteExistenteResponse), clienteResponseDTO);


            verify(clienteRespository, times(1)).findAll();

        }

        @Test
        void deveRetornarVazioQuandoNenhumClienteEncontrado() {

            when(clienteRespository.findAll())
                    .thenReturn(List.of());

            var clienteResponseDTO = clienteService.obterTodos();

            assertEquals(List.of(), clienteResponseDTO);

            verify(clienteRespository, times(1)).findAll();

        }
    }
    @Nested
    class CadastrarCliente {

        @Test
        void deveGerarExcessaoQuandoCpfJaCadastrado() {
            // Configurar um cliente com um CPF específico que já existe na base de dados
            String cpfExistente = "12345678910";
            var clienteExistente = clienteService.toEntity(buildClienteRequestDTO()); // Substitua pelo construtor real do Cliente
            clienteExistente.setCpf(cpfExistente);
            when(clienteRespository.existsByCpf(cpfExistente))
                    .thenReturn(true);

            // Cliente com o mesmo CPF que já está cadastrado
            ClienteRequestDTO clienteRequestDTO = new ClienteRequestDTO(
                    "Guilherme Matos de Carvalho",
                    "8Xa5I@example.com",
                    "Sdsadwd21321@#$",
                    "11987654321",
                    cpfExistente,
                    "9",
                    new Endereco(
                            "9",
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

            // Verificar se uma exceção é lançada ao tentar cadastrar o cliente
            assertThatThrownBy(() -> clienteService.cadastrarCliente(clienteRequestDTO))
                    .isInstanceOf(CpfException.class)
                    .hasMessage("Esse cpf já está sendo utilizado");

            // Verificar se o método cadastrarCliente não foi chamado
            verify(clienteRespository, never()).save(any());

        }

        @Test
        void deveCadastrarCliente() {
            var clienteRequestDTO = buildClienteRequestDTO();
            var cliente = clienteService.toEntity(clienteRequestDTO);

            // Configura o mock para retornar o cliente quando o método save for chamado
            when(clienteRespository.save(any(Cliente.class))).thenAnswer(invocation -> {
                Cliente savedCliente = invocation.getArgument(0);
                //savedCliente.setId(1L); // Define um ID para o cliente salvo, simulando a persistência
                return savedCliente;
            });

            var clienteResponse = clienteService.toResponseDTO(cliente);
            var clienteResponseRecebido = clienteService.cadastrarCliente(clienteRequestDTO);

            assertEquals(clienteResponse, clienteResponseRecebido);

            // Verifica se o método save foi chamado com algum objeto do tipo Cliente
            verify(clienteRespository, times(1)).save(any(Cliente.class));
        }
    }

    @Nested
    class AtualizarCliente {

        @Test
        void deveAtualizarCliente() {
            var idCliente = 1L;
            var clienteRequestDTO = clienteService.toEntity(buildClienteRequestDTO());
            var clienteRequestAtualizado = clienteService.toEntity(buildClienteRequestDTO());

            clienteRequestAtualizado.setNome("Guilherme Mattos de Carvalho");
            clienteRequestAtualizado.setSenha("123456");
            clienteRequestAtualizado.setTelefone("11987654321");
            clienteRequestAtualizado.getEndereco().setCep("9");
            clienteRequestAtualizado.getEndereco().setLogradouro("Casa");
            clienteRequestAtualizado.getEndereco().setComplemento("Centro");
            clienteRequestAtualizado.getEndereco().setCidade("São Paulo");
            clienteRequestAtualizado.getEndereco().setUf("SP");
            clienteRequestAtualizado.getEndereco().setCep("12345678");
            clienteRequestAtualizado.getEndereco().setLatitude(1111.54551);
            clienteRequestAtualizado.getEndereco().setLongitude(111122.226);

            var clienteResponse = clienteService.toResponseDTO(clienteRequestDTO);

            when(clienteRespository.findById(idCliente)).thenReturn(Optional.of(clienteRequestAtualizado));

            when(clienteRespository.save(clienteRequestAtualizado)).thenReturn(clienteRequestAtualizado);

            var clienteAtualizado = clienteService.atualizarCliente(idCliente, clienteService.toRequestDTO(clienteRequestAtualizado));

            assertEquals(clienteResponse.id(), clienteAtualizado.id());
            assertEquals(clienteResponse.cpf(), clienteAtualizado.cpf());
            assertEquals(clienteResponse.email(), clienteAtualizado.email());

            verify(clienteRespository, times(1)).findById(idCliente);
            verify(clienteRespository, times(1)).save(clienteRequestAtualizado);
        }

        @Test
        void deveGerarExcessaoQuandoIdNaoEncontrado() {
            var id = 2L;

            when(clienteRespository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    clienteService.atualizarCliente(id, buildClienteRequestDTO()))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(String.format("Cliente com o id '%d' não encontrado", id));

            verify(clienteRespository, times(1)).findById(id);

        }

        @Test
        void deveGerarExcessaoQuandoCpfJaCadastrado() {
            var id = 1L;
            String cpfExistente = "55555144171";
            var clienteExistente = clienteService.toEntity(buildClienteRequestDTO());
            var clienteAtualizado = clienteService.toEntity(buildClienteRequestDTO());
            clienteAtualizado.setCpf(cpfExistente);

            when(clienteRespository.existsByCpf(cpfExistente))
                    .thenReturn(true);

            when(clienteRespository.findById(id))
                    .thenReturn(Optional.of(clienteExistente));

            var client = clienteService.toRequestDTO(clienteAtualizado);

            // Verificar se uma exceção é lançada ao tentar cadastrar o cliente
            assertThatThrownBy(() -> clienteService.atualizarCliente(id, client))
                    .isInstanceOf(CpfException.class)
                    .hasMessage("Esse cpf já está sendo utilizado");

            // Verificar se o método cadastrarCliente não foi chamado
            verify(clienteRespository, never()).save(any());
        }
    }

    @Nested
    class DeletarCliente {

        @Test
        void deveDeletarCliente() {
            var id = 1L;
            var clienteExistente = clienteService.toEntity(buildClienteRequestDTO());

            when(clienteRespository.existsById(id))
                    .thenReturn(true);

            doNothing().when(clienteRespository).deleteById(id);

            clienteService.deletarCliente(id);

            verify(clienteRespository, times(1)).existsById(id);
            verify(clienteRespository, times(1)).deleteById(id);

        }

        @Test
        void deveGerarExcessaoQuandoIdNaoEncontrado() {
            var id = 2L;

            when(clienteRespository.existsById(id))
                    .thenReturn(false);

            assertThatThrownBy(() ->
                    clienteService.deletarCliente(id))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(String.format("Cliente com o id '%d' não encontrado", id));

            verify(clienteRespository, times(1)).existsById(id);
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
                        "9",
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
                        "9",
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
                "9",
                new Endereco(
                        "9",
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
