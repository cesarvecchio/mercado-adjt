package br.com.cadastrocliente.mscadastrocliente.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.cadastrocliente.mscadastrocliente.application.controller.exception.CpfException;
import br.com.cadastrocliente.mscadastrocliente.application.controller.exception.NaoEncontradoException;
import br.com.cadastrocliente.mscadastrocliente.application.request.ClienteRequestDTO;
import br.com.cadastrocliente.mscadastrocliente.application.response.ClienteResponseDTO;
import br.com.cadastrocliente.mscadastrocliente.domain.entity.Cliente;
import br.com.cadastrocliente.mscadastrocliente.domain.valueObject.Endereco;
import br.com.cadastrocliente.mscadastrocliente.infra.repository.ClienteRespository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ClienteServiceIT {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRespository clienteRespository;

    private List<Cliente> clienteList;
    @BeforeEach
    void populateDatabase() {
        List<Cliente> clientes = getClientes();

        clienteList = clienteRespository.saveAll(clientes);
    }
    private List<Cliente> getClientes() {
        // Cliente 1
        Cliente cliente1 = new Cliente(
                "Guilherme Matos de Carvalho",
                "8Xa5I@example.com",
                "Sdsadwd21321@#$",
                "11923465432",
                "12345678910",
                "147852369",
                new Endereco(
                        "14752369",
                        "Rua A",
                        "Casa",
                        "Centro",
                        "São Paulo",
                        "SP",
                        "189",
                        1111.54551,
                        111122.226
                )
        );

        // Cliente 2
        Cliente cliente2 = new Cliente(
                "Fulano de Tal",
                "fulano@example.com",
                "senha123",
                "987654321",
                "11987654321",
                "987654321",
                new Endereco(
                        "98764321",
                        "Rua B",
                        "Apartamento",
                        "Bairro Novo",
                        "Rio de Janeiro",
                        "RJ",
                        "876",
                        2222.98765,
                        333333.333
                )
        );

        // Cliente 3
        Cliente cliente3 = new Cliente(
                "Ciclano da Silva",
                "ciclano@example.com",
                "senha456",
                "369258147",
                "11987654321",
                "369258147",
                new Endereco(
                        "36925847",
                        "Rua C",
                        "Sobrado",
                        "Centro",
                        "Curitiba",
                        "PR",
                        "369",
                        4444.12345,
                        555555.555
                )
        );

        // Cliente 4
        Cliente cliente4 = new Cliente(
                "Beltrano Oliveira",
                "beltrano@example.com",
                "senha789",
                "654123987",
                "11987654321",
                "654123987",
                new Endereco(
                        "65412397",
                        "Rua D",
                        "Casa",
                        "Periferia",
                        "Salvador",
                        "BA",
                        "123",
                        6666.78901,
                        777777.777
                )
        );

        // Cliente 5
        Cliente cliente5 = new Cliente(
                "Maria Souza",
                "maria@example.com",
                "senhaabc",
                "852147369",
                "11987654321",
                "852147369",
                new Endereco(
                        "85214736",
                        "Rua E",
                        "Apartamento",
                        "Praia",
                        "Fortaleza",
                        "CE",
                        "987",
                        8888.13579,
                        999999.999
                )
        );

        return List.of(cliente1, cliente2, cliente3, cliente4, cliente5);
    }

    @AfterEach
    void dropDatabase() {
        clienteList = null;
        clienteRespository.deleteAll();
    }

    @Nested
    class buscarTodos {

        @Test
        void deveRetornarTodosOsClientes() {
            List<ClienteResponseDTO> clientes = clienteService.obterTodos();

            assertThat(clientes).hasSize(5);

            // Cliente 1
            assertThat(clientes.get(0).id())
                    .isNotNull();
            assertThat(clientes.get(0).nome())
                    .isNotNull()
                    .isEqualTo("Guilherme Matos de Carvalho");
            assertThat(clientes.get(0).email())
                    .isNotNull()
                    .isEqualTo("8Xa5I@example.com");
            assertThat(clientes.get(0).telefone())
                    .isEqualTo("11923465432");
            assertThat(clientes.get(0).cpf())
                    .isNotNull()
                    .isEqualTo("12345678910");
            assertThat(clientes.get(0).endereco().getCep())
                    .isNotNull()
                    .isEqualTo("14752369");
            assertThat(clientes.get(0).endereco().getLogradouro())
                    .isNotNull()
                    .isEqualTo("Rua A");
            assertThat(clientes.get(0).endereco().getNumero())
                    .isNotNull()
                    .isEqualTo("189");
            assertThat(clientes.get(0).endereco().getComplemento())
                    .isNotNull()
                    .isEqualTo("Casa");
            assertThat(clientes.get(0).endereco().getBairro())
                    .isNotNull()
                    .isEqualTo("Centro");
            assertThat(clientes.get(0).endereco().getCidade())
                    .isNotNull()
                    .isEqualTo("São Paulo");
            assertThat(clientes.get(0).endereco().getUf())
                    .isNotNull()
                    .isEqualTo("SP");

// Cliente 2
            assertThat(clientes.get(1).id())
                    .isNotNull();
            assertThat(clientes.get(1).nome())
                    .isNotNull()
                    .isEqualTo("Fulano de Tal");
            assertThat(clientes.get(1).email())
                    .isNotNull()
                    .isEqualTo("fulano@example.com");
            assertThat(clientes.get(1).telefone())
                    .isEqualTo("987654321");
            assertThat(clientes.get(1).cpf())
                    .isNotNull()
                    .isEqualTo("11987654321");
            assertThat(clientes.get(1).endereco().getCep())
                    .isNotNull()
                    .isEqualTo("98764321");
            assertThat(clientes.get(1).endereco().getLogradouro())
                    .isNotNull()
                    .isEqualTo("Rua B");
            assertThat(clientes.get(1).endereco().getNumero())
                    .isNotNull()
                    .isEqualTo("876");
            assertThat(clientes.get(1).endereco().getComplemento())
                    .isNotNull()
                    .isEqualTo("Apartamento");
            assertThat(clientes.get(1).endereco().getBairro())
                    .isNotNull()
                    .isEqualTo("Bairro Novo");
            assertThat(clientes.get(1).endereco().getCidade())
                    .isNotNull()
                    .isEqualTo("Rio de Janeiro");
            assertThat(clientes.get(1).endereco().getUf())
                    .isNotNull()
                    .isEqualTo("RJ");


            // Cliente 3
            assertThat(clientes.get(2).id())
                    .isNotNull();
            assertThat(clientes.get(2).nome())
                    .isNotNull()
                    .isEqualTo("Ciclano da Silva");
            assertThat(clientes.get(2).email())
                    .isNotNull()
                    .isEqualTo("ciclano@example.com");
            assertThat(clientes.get(2).telefone())
                    .isEqualTo("369258147");
            assertThat(clientes.get(2).cpf())
                    .isNotNull()
                    .isEqualTo("11987654321");
            assertThat(clientes.get(2).endereco().getCep())
                    .isNotNull()
                    .isEqualTo("36925847");
            assertThat(clientes.get(2).endereco().getLogradouro())
                    .isNotNull()
                    .isEqualTo("Rua C");
            assertThat(clientes.get(2).endereco().getNumero())
                    .isNotNull()
                    .isEqualTo("369");
            assertThat(clientes.get(2).endereco().getComplemento())
                    .isNotNull()
                    .isEqualTo("Sobrado");
            assertThat(clientes.get(2).endereco().getBairro())
                    .isNotNull()
                    .isEqualTo("Centro");
            assertThat(clientes.get(2).endereco().getCidade())
                    .isNotNull()
                    .isEqualTo("Curitiba");
            assertThat(clientes.get(2).endereco().getUf())
                    .isNotNull()
                    .isEqualTo("PR");

// Cliente 4
            assertThat(clientes.get(3).id())
                    .isNotNull();
            assertThat(clientes.get(3).nome())
                    .isNotNull()
                    .isEqualTo("Beltrano Oliveira");
            assertThat(clientes.get(3).email())
                    .isNotNull()
                    .isEqualTo("beltrano@example.com");
            assertThat(clientes.get(3).telefone())
                    .isEqualTo("654123987");
            assertThat(clientes.get(3).cpf())
                    .isNotNull()
                    .isEqualTo("11987654321");
            assertThat(clientes.get(3).endereco().getCep())
                    .isNotNull()
                    .isEqualTo("65412397");
            assertThat(clientes.get(3).endereco().getLogradouro())
                    .isNotNull()
                    .isEqualTo("Rua D");
            assertThat(clientes.get(3).endereco().getNumero())
                    .isNotNull()
                    .isEqualTo("123");
            assertThat(clientes.get(3).endereco().getComplemento())
                    .isNotNull()
                    .isEqualTo("Casa");
            assertThat(clientes.get(3).endereco().getBairro())
                    .isNotNull()
                    .isEqualTo("Periferia");
            assertThat(clientes.get(3).endereco().getCidade())
                    .isNotNull()
                    .isEqualTo("Salvador");
            assertThat(clientes.get(3).endereco().getUf())
                    .isNotNull()
                    .isEqualTo("BA");

// Cliente 5
            assertThat(clientes.get(4).id())
                    .isNotNull();
            assertThat(clientes.get(4).nome())
                    .isNotNull()
                    .isEqualTo("Maria Souza");
            assertThat(clientes.get(4).email())
                    .isNotNull()
                    .isEqualTo("maria@example.com");
            assertThat(clientes.get(4).telefone())
                    .isEqualTo("852147369");
            assertThat(clientes.get(4).cpf())
                    .isNotNull()
                    .isEqualTo("11987654321");
            assertThat(clientes.get(4).endereco().getCep())
                    .isNotNull()
                    .isEqualTo("85214736");
            assertThat(clientes.get(4).endereco().getLogradouro())
                    .isNotNull()
                    .isEqualTo("Rua E");
            assertThat(clientes.get(4).endereco().getNumero())
                    .isNotNull()
                    .isEqualTo("987");
            assertThat(clientes.get(4).endereco().getComplemento())
                    .isNotNull()
                    .isEqualTo("Apartamento");
            assertThat(clientes.get(4).endereco().getBairro())
                    .isNotNull()
                    .isEqualTo("Praia");
            assertThat(clientes.get(4).endereco().getCidade())
                    .isNotNull()
                    .isEqualTo("Fortaleza");
            assertThat(clientes.get(4).endereco().getUf())
                    .isNotNull()
                    .isEqualTo("CE");
        }
    }

    @Nested
    class ObterClienteResponsePorId {

        @Test
        void deveRetornarClienteResponsePorId() {
            // Arrange
            Long id = clienteList.get(0).getId();

            // Act
            var clienteResponse = clienteService.obterClienteResponsePorId(id);

            // Assert
            Assertions.assertThat(clienteResponse)
                    .isNotNull()
                    .isInstanceOf(ClienteResponseDTO.class);
            AssertionsForClassTypes.assertThat(clienteResponse.id())
                    .isNotNull()
                    .isEqualTo(id);
            AssertionsForClassTypes.assertThat(clienteResponse.nome())
                    .isNotNull()
                    .isEqualTo("Guilherme Matos de Carvalho");
            AssertionsForClassTypes.assertThat(clienteResponse.email())
                    .isNotNull()
                    .isEqualTo("8Xa5I@example.com");
            AssertionsForClassTypes.assertThat(clienteResponse.telefone())
                    .isEqualTo("11923465432");
            AssertionsForClassTypes.assertThat(clienteResponse.cpf())
                    .isNotNull()
                    .isEqualTo("12345678910");
            AssertionsForClassTypes.assertThat(clienteResponse.endereco().getCep())
                    .isNotNull()
                    .isEqualTo("14752369");
            AssertionsForClassTypes.assertThat(clienteResponse.endereco().getLogradouro())
                    .isNotNull()
                    .isEqualTo("Rua A");
            AssertionsForClassTypes.assertThat(clienteResponse.endereco().getNumero())
                    .isNotNull()
                    .isEqualTo("189");
            AssertionsForClassTypes.assertThat(clienteResponse.endereco().getComplemento())
                    .isNotNull()
                    .isEqualTo("Casa");
            AssertionsForClassTypes.assertThat(clienteResponse.endereco().getBairro())
                    .isNotNull()
                    .isEqualTo("Centro");
            AssertionsForClassTypes.assertThat(clienteResponse.endereco().getCidade())
                    .isNotNull()
                    .isEqualTo("São Paulo");
            AssertionsForClassTypes.assertThat(clienteResponse.endereco().getUf())
                    .isNotNull()
                    .isEqualTo("SP");
        }

        @Test
        void deveGerarExcecaoQuandoIdNaoEncontrado() {

            Long id = 10L;

            Assertions.assertThatThrownBy(() -> clienteService.obterClienteResponsePorId(id))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(String.format("Cliente com o id '%d' não encontrado", id));
        }


    }

    @Nested
    class ObterClientePorId {

        @Test
        void deveRetornarClientePorId() {
            // Arrange
            Long id = clienteList.get(0).getId();;

            // Act
            var cliente = clienteService.obterClientePorId(id);

            // Assert
            assertThat(cliente)
                    .isNotNull()
                    .isInstanceOf(Cliente.class);
            assertThat(cliente.getId())
                    .isNotNull()
                    .isEqualTo(id);
            assertThat(cliente.getNome())
                    .isNotNull()
                    .isEqualTo("Guilherme Matos de Carvalho");
            assertThat(cliente.getEmail())
                    .isNotNull()
                    .isEqualTo("8Xa5I@example.com");
            assertThat(cliente.getSenha())
                    .isEqualTo("Sdsadwd21321@#$");
            assertThat(cliente.getTelefone())
                    .isEqualTo("11923465432");
            assertThat(cliente.getCpf())
                    .isNotNull()
                    .isEqualTo("12345678910");
            assertThat(cliente.getRg())
                    .isNotNull()
                    .isEqualTo("147852369");
            assertThat(cliente.getEndereco().getCep())
                    .isNotNull()
                    .isEqualTo("14752369");
            assertThat(cliente.getEndereco().getLogradouro())
                    .isNotNull()
                    .isEqualTo("Rua A");
            assertThat(cliente.getEndereco().getNumero())
                    .isNotNull()
                    .isEqualTo("189");
            assertThat(cliente.getEndereco().getComplemento())
                    .isNotNull()
                    .isEqualTo("Casa");
            assertThat(cliente.getEndereco().getBairro())
                    .isNotNull()
                    .isEqualTo("Centro");
            assertThat(cliente.getEndereco().getCidade())
                    .isNotNull()
                    .isEqualTo("São Paulo");
            assertThat(cliente.getEndereco().getUf())
                    .isNotNull()
                    .isEqualTo("SP");
            assertThat(cliente.getEndereco().getLatitude())
                    .isNotNull()
                    .isEqualTo(1111.54551);
            assertThat(cliente.getEndereco().getLongitude())
                    .isNotNull()
                    .isEqualTo(111122.226);
        }

        @Test
        void deveGerarExcecaoQuandoIdNaoEncontrado() {

            Long id = 10L;

            Assertions.assertThatThrownBy(() -> clienteService.obterClientePorId(id))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(String.format("Cliente com o id '%d' não encontrado", id));
        }
    }


    @Nested
    class CadastrarCliente {

        @Test
        void deveCadastrarCliente() {

            // Arrange
            var clienteRequestDTO = new ClienteRequestDTO(
                    "Fulano de Tal",
                    "fulano@example.com",
                    "senha123",
                    "987654321",
                    "84562414541",
                    "369258147",
                    new Endereco(
                            "36925817",
                            "Rua dos indios",
                            "Apartamento",
                            "Nova Conquista",
                            "Rio de Janeiro",
                            "RJ",
                            "876",
                            2222.98765,
                            333333.333
                    )
            );

            // Act
            var cliente = clienteService.cadastrarCliente(clienteRequestDTO);

            // Assert
            assertThat(cliente)
                    .isNotNull()
                    .isInstanceOf(ClienteResponseDTO.class);
            assertThat(cliente.id())
                    .isNotNull();
            assertThat(cliente.nome())
                    .isNotNull()
                    .isEqualTo("Fulano de Tal");
            assertThat(cliente.email())
                    .isNotNull()
                    .isEqualTo("fulano@example.com");
            assertThat(cliente.cpf())
                    .isNotNull()
                    .isEqualTo("84562414541");
            assertThat(cliente.telefone())
                    .isEqualTo("987654321");
            assertThat(cliente.endereco().getCep())
                    .isNotNull()
                    .isEqualTo("36925817");
            assertThat(cliente.endereco().getLogradouro())
                    .isNotNull()
                    .isEqualTo("Rua dos indios");
            assertThat(cliente.endereco().getNumero())
                    .isNotNull()
                    .isEqualTo("876");
            assertThat(cliente.endereco().getComplemento())
                    .isNotNull()
                    .isEqualTo("Apartamento");
            assertThat(cliente.endereco().getBairro())
                    .isNotNull()
                    .isEqualTo("Nova Conquista");
            assertThat(cliente.endereco().getCidade())
                    .isNotNull()
                    .isEqualTo("Rio de Janeiro");
            assertThat(cliente.endereco().getUf())
                    .isNotNull()
                    .isEqualTo("RJ");
            assertThat(cliente.endereco().getLatitude())
                    .isNotNull()
                    .isEqualTo(2222.98765);
            assertThat(cliente.endereco().getLongitude())
                    .isNotNull()
                    .isEqualTo(333333.333);

        }

        @Test
        void deveGerarExcessaoQuandoCpfJaCadastrado() {

            // Arrange
            var clienteRequestDTO = new ClienteRequestDTO(
                    "Fulano de Tal",
                    "fulano@example.com",
                    "Sdsadwd21321@#$",
                    "987654321",
                    "12345678910",
                    "369258147",
                    new Endereco(
                            "36925814",
                            "Rua dos indios",
                            "Apartamento",
                            "Nova Conquista",
                            "Rio de Janeiro",
                            "RJ",
                            "876",
                            2222.98765,
                            333333.333
                    )
            );

            // Act
            Assertions.assertThatThrownBy(() -> clienteService.cadastrarCliente(clienteRequestDTO))
                    .isInstanceOf(CpfException.class)
                    .hasMessage("Esse cpf já está sendo utilizado");

        }
    }

    @Nested
    class AtualizarCliente {

        @Test
        void deveAtualizarCliente() {
            Long id = clienteList.get(2).getId();
            // Arrange
            var clienteRequestDTO = new ClienteRequestDTO(
                    "Fulano de Tal",
                    "fulano@example.com",
                    "Sdsadwd21321@#$",
                    "987654321",
                    "11987654321",
                    "369258147",
                    new Endereco(
                            "36925817",
                            "Rua dos indios",
                            "Apartamento",
                            "Nova Conquista",
                            "Rio de Janeiro",
                            "RJ",
                            "876",
                            2222.98765,
                            333333.333
                    )
            );

            // Act
            var cliente = clienteService.atualizarCliente(id, clienteRequestDTO);

            // Assert
            assertThat(cliente)
                    .isNotNull()
                    .isInstanceOf(ClienteResponseDTO.class);
            assertThat(cliente.id())
                    .isNotNull()
                    .isEqualTo(id);
            assertThat(cliente.nome())
                    .isNotNull()
                    .isEqualTo("Fulano de Tal");
            assertThat(cliente.email())
                    .isNotNull()
                    .isEqualTo("fulano@example.com");
            assertThat(cliente.cpf())
                    .isNotNull()
                    .isEqualTo("11987654321");
            assertThat(cliente.telefone())
                    .isEqualTo("987654321");
            assertThat(cliente.endereco().getCep())
                    .isNotNull()
                    .isEqualTo("36925817");
            assertThat(cliente.endereco().getLogradouro())
                    .isNotNull()
                    .isEqualTo("Rua dos indios");
            assertThat(cliente.endereco().getNumero())
                    .isNotNull()
                    .isEqualTo("876");
            assertThat(cliente.endereco().getComplemento())
                    .isNotNull()
                    .isEqualTo("Apartamento");
            assertThat(cliente.endereco().getBairro())
                    .isNotNull()
                    .isEqualTo("Nova Conquista");
            assertThat(cliente.endereco().getCidade())
                    .isNotNull()
                    .isEqualTo("Rio de Janeiro");
            assertThat(cliente.endereco().getUf())
                    .isNotNull()
                    .isEqualTo("RJ");
            assertThat(cliente.endereco().getLatitude())
                    .isNotNull()
                    .isEqualTo(2222.98765);
            assertThat(cliente.endereco().getLongitude())
                    .isNotNull()
                    .isEqualTo(333333.333);
        }

        @Test
        void deveGerarExcessaoQuandoCpfJaCadastrado() {
            Long id = clienteList.get(2).getId();
            // Arrange
            var clienteRequestDTO = new ClienteRequestDTO(
                    "Fulano de Tal",
                    "fulano@example.com",
                    "Sdsadwd21321@#$",
                    "987654321",
                    "12345678910",
                    "369258147",
                    new Endereco(
                            "369258147",
                            "Rua dos indios",
                            "Apartamento",
                            "Nova Conquista",
                            "Rio de Janeiro",
                            "RJ",
                            "876",
                            2222.98765,
                            333333.333
                    )
            );

            // Act
            Assertions.assertThatThrownBy(() -> clienteService.atualizarCliente(id, clienteRequestDTO))
                    .isInstanceOf(CpfException.class)
                    .hasMessage("Esse cpf já está sendo utilizado");

        }

        @Test
        void deveGerarExcessaoQuandoNaoEncontrado() {
            Long id = 10L;
            // Arrange
            var clienteRequestDTO = new ClienteRequestDTO(
                    "Fulano de Tal",
                    "fulano@example.com",
                    "Sdsadwd21321@#$",
                    "987654321",
                    "12345678910",
                    "369258147",
                    new Endereco(
                            "369258147",
                            "Rua dos indios",
                            "Apartamento",
                            "Nova Conquista",
                            "Rio de Janeiro",
                            "RJ",
                            "876",
                            2222.98765,
                            333333.333
                    )
            );

            // Act
            Assertions.assertThatThrownBy(() -> clienteService.atualizarCliente(10L, clienteRequestDTO))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage("Cliente com o id '%d' não encontrado", id);
        }
    }

    @Nested
    class DeletarCliente {

        @Test
        void deveDeletarCliente() {
            Long id = clienteList.get(4).getId();
            // Act
            clienteService.deletarCliente(id);

            // Assert
            assertThat(clienteRespository.findById(id))
                    .isEmpty();


        }

        @Test

        void deveGerarExcessaoQuandoNaoEncontrado() {
            var idNaoExiste = 10L;

            // Act
            assertThatThrownBy(() -> clienteService.deletarCliente(idNaoExiste))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(String.format("Cliente com o id '%d' não encontrado", idNaoExiste));
        }
    }

}
