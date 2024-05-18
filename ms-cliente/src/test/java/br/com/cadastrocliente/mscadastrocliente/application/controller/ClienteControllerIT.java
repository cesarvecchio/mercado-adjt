package br.com.cadastrocliente.mscadastrocliente.application.controller;

import br.com.cadastrocliente.mscadastrocliente.application.request.ClienteRequestDTO;
import br.com.cadastrocliente.mscadastrocliente.domain.entity.Cliente;
import br.com.cadastrocliente.mscadastrocliente.domain.service.ClienteService;
import br.com.cadastrocliente.mscadastrocliente.domain.valueObject.Endereco;
import br.com.cadastrocliente.mscadastrocliente.infra.repository.ClienteRespository;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClienteControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRespository clienteRespository;

    private List<Cliente> clienteList;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        List<Cliente> clientes = getClientes();

        clienteList = clienteRespository.saveAll(clientes);
    }

    private List<Cliente> getClientes() {
        // Cliente 1
        Cliente cliente1 = new Cliente(
                1L,
                "Guilherme Matos de Carvalho",
                "8Xa5I@example.com",
                "Sdsadwd21321@#$",
                "11923465432",
                "12345678910",
                "14785269",
                new Endereco(
                        "14785269",
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
                2L,
                "Fulano de Tal",
                "fulano@example.com",
                "senha123",
                "98765431",
                "11987654321",
                "98765431",
                new Endereco(
                        "98765431",
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
                3L,
                "Ciclano da Silva",
                "ciclano@example.com",
                "senha456",
                "36925814",
                "11987654321",
                "36925814",
                new Endereco(
                        "36925814",
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
                4L,
                "Beltrano Oliveira",
                "beltrano@example.com",
                "senha789",
                "64123987",
                "11987654321",
                "64123987",
                new Endereco(
                        "64123987",
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
                5L,
                "Maria Souza",
                "maria@example.com",
                "senhaabc",
                "85214769",
                "11987654321",
                "85214769",
                new Endereco(
                        "85214769",
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
    class ObterClientes {

        @Test
        void obterTodos() {

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/cliente")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/cliente-list.schema.json"));
        }

        @Test
        void deveObterVazio() {
            dropDatabase();
            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/cliente")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(0));
        }

        @Test
        void obterPorId() {
            Long id = clienteList.get(0).getId();

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/cliente/buscar/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/cliente.schema.json"));
        }

        @Test
        void deveGerarExcessaoQuandoIdNaoEncontrado() {
            var id = 6L;

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/cliente/buscar/{id}", id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Nao Encontrado Exception"))
                    .body("status", equalTo(404))
                    .body("message", equalTo(
                            String.format("Cliente com o id '%d' não encontrado", id)));
        }
    }

    @Nested
    class CadastrarCliente {

        @Test
        void deveCadastrar() {
            var request = new ClienteRequestDTO(
                    "Fulano de tal",
                    "fulano@example.com",
                    "Sdsadwd21321@#$",
                    "11987654321",
                    "66654812177",
                    "36925814",
                    new Endereco(
                            "14785269",
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

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/cliente/cadastrar")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/cliente.schema.json"));
        }

        @Test
        void deveGerarExcessaoQuandoCpfJaCadastrado() throws Exception {

            var request = new ClienteRequestDTO(
                    "Fulano de tal",
                    "fulano@example.com",
                    "Sdsadwd21321@#$",
                    "11987654321",
                    "12345678910",
                    "36925814",
                    new Endereco(
                            "14785269",
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

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/cliente/cadastrar")
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Cpf Exception"))
                    .body("status", equalTo(409))
                    .body("message", equalTo("Esse cpf já está sendo utilizado"));
        }
    }

    @Nested
    class AtualizarCliente {

        @Test
        void deveAtualizar() throws Exception {
            Long id = clienteList.get(0).getId();
            var request = new ClienteRequestDTO(
                    "Fulano de tal",
                    "fulano@example.com",
                    "Sdsadwd21321@#$",
                    "11987654321",
                    "66655548127",
                    "36925814",
                    new Endereco(
                            "14785269",
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

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .put("/cliente/atualizar/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/cliente.schema.json"));
        }

        @Test
        void deveGerarExcessaoQuandoIdNaoEncontrado() throws Exception {
            var id = 6L;
            var request = new ClienteRequestDTO(
                    "Fulano de tal",
                    "fulano@example.com",
                    "Sdsadwd21321@#$",
                    "11987654321",
                    "666555481217",
                    "36925814",
                    new Endereco(
                            "14785269",
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

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .put("/cliente/atualizar/{id}", id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Nao Encontrado Exception"))
                    .body("status", equalTo(404))
                    .body("message", equalTo(String
                            .format("Cliente com o id '%d' não encontrado", id)));
        }

        @Test
        void deveGerarExcessaoQuandoCpfJaCadastrado() throws Exception {
            Long id = clienteList.get(2).getId();
            var request = new ClienteRequestDTO(
                    "Fulano de tal",
                    "fulano@example.com",
                    "Sdsadwd21321@#$",
                    "11987654321",
                    "12345678910",
                    "36925814",
                    new Endereco(
                            "14785269",
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

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .put("/cliente/atualizar/{id}", id)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Cpf Exception"))
                    .body("status", equalTo(409))
                    .body("message", equalTo("Esse cpf já está sendo utilizado"));
        }
    }

    @Nested
    class DeletarCliente {

        @Test
        void deveDeletarCliente() throws Exception {
            Long id = clienteList.get(4).getId();

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/cliente/deletar/{id}", id)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void deveGerarExcessaoQuandoIdNaoEncontrado() throws Exception {
            var id = 6L;

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/cliente/deletar/{id}", id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Nao Encontrado Exception"))
                    .body("status", equalTo(404))
                    .body("message", equalTo(String
                            .format("Cliente com o id '%d' não encontrado", id)));
        }
    }
}
