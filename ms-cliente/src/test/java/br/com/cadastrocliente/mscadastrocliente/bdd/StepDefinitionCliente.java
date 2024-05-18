package br.com.cadastrocliente.mscadastrocliente.bdd;

import br.com.cadastrocliente.mscadastrocliente.application.request.ClienteRequestDTO;
import br.com.cadastrocliente.mscadastrocliente.application.response.ClienteResponseDTO;
import br.com.cadastrocliente.mscadastrocliente.domain.valueObject.Endereco;
import com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.it.Quando;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepDefinitionCliente {
    private Response response;
    private ClienteResponseDTO clienteResponseDTO;
    private List<ClienteResponseDTO> clienteResponseDTOList;
    private final String ENDPOINT_API_CLIENTE = "http://localhost:8081/cliente";

    @Quando("cadastar um novo cliente")
    public ClienteResponseDTO cadastrar_um_novo_cliente() {
        var clienteRequest = getClienteRequest();
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(clienteRequest)
                .when()
                .post(ENDPOINT_API_CLIENTE + "/cadastrar");

        return response.then().extract().as(ClienteResponseDTO.class);
    }

    @Entao("o cliente e cadastrado com sucesso")
    public void o_cliente_e_cadastrado_com_sucesso() {
        response.then().statusCode(HttpStatus.CREATED.value());
    }

    @Entao("deve ser apresentado")
    public void deve_ser_apresentado() {
        response.then()
                .body("id", notNullValue());
    }

    @Dado("que um cliente foi cadastrado")
    public void que_um_cliente_foi_cadastrado() {
        clienteResponseDTO = cadastrar_um_novo_cliente();
    }

    @Quando("eu realizar uma busca de todos os clientes")
    public void eu_realizar_uma_busca_de_todos_os_clientes() {
        response = given()
                .when()
                .get(ENDPOINT_API_CLIENTE);

        clienteResponseDTOList = new ArrayList<>(response.then().extract()
                .as(new TypeReference<List<ClienteResponseDTO>>() {

                }.getType()));
    }

    @Entao("a lista de clientes deve ser apresentado com sucesso")
    public void a_lista_de_clientes_deve_ser_apresentado_com_sucesso() {
        Integer actualId = response.then().extract().path("[0].id"); // Extrair como Integer
        Long expectedId = clienteResponseDTOList.get(0).id(); // Supondo que getId() retorna um Long

        // Converter o valor retornado para Long antes de comparar
        Long actualIdLong = actualId.longValue();

        assertEquals(expectedId, actualIdLong);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("[0].nome", equalTo(clienteResponseDTOList.get(0).nome()))
                .body("[0].email", equalTo(clienteResponseDTOList.get(0).email()))
                .body("[0].cpf", equalTo(clienteResponseDTOList.get(0).cpf()))
                .body("[0].telefone", equalTo(clienteResponseDTOList.get(0).telefone()))
                .body("[0].endereco.cep", equalTo(clienteResponseDTOList.get(0).endereco().getCep()))
                .body("[0].endereco.logradouro", equalTo(clienteResponseDTOList.get(0).endereco().getLogradouro()))
                .body("[0].endereco.bairro", equalTo(clienteResponseDTOList.get(0).endereco().getBairro()))
                .body("[0].endereco.cidade", equalTo(clienteResponseDTOList.get(0).endereco().getCidade()))
                .body("[0].endereco.uf", equalTo(clienteResponseDTOList.get(0).endereco().getUf()))
                .body("[0].endereco.numero", equalTo(clienteResponseDTOList.get(0).endereco().getNumero()))
                .body("[0].endereco.complemento", equalTo(clienteResponseDTOList.get(0).endereco().getComplemento()));

    }

    @Quando("eu realizar uma busca desse cliente")
    public void eu_realizar_uma_busca_desse_cliente(){
        response = given()
                .when()
                .get(ENDPOINT_API_CLIENTE + "/buscar/" + clienteResponseDTO.id());

        clienteResponseDTO = response.then().extract().as(ClienteResponseDTO.class);
    }

    @Entao("o cliente deve ser apresentado com sucesso")
    public void o_cliente_deve_ser_apresentado_com_sucesso() {
        Integer actualId = response.then().extract().path("id"); // Extrair como Integer
        Long expectedId = clienteResponseDTO.id(); // Supondo que getId() retorna um Long

        // Converter o valor retornado para Long antes de comparar
        Long actualIdLong = actualId.longValue();

        assertEquals(expectedId, actualIdLong);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("nome", equalTo(clienteResponseDTO.nome()))
                .body("email", equalTo(clienteResponseDTO.email()))
                .body("cpf", equalTo(clienteResponseDTO.cpf()))
                .body("telefone", equalTo(clienteResponseDTO.telefone()))
                .body("endereco.cep", equalTo(clienteResponseDTO.endereco().getCep()))
                .body("endereco.logradouro", equalTo(clienteResponseDTO.endereco().getLogradouro()))
                .body("endereco.bairro", equalTo(clienteResponseDTO.endereco().getBairro()))
                .body("endereco.cidade", equalTo(clienteResponseDTO.endereco().getCidade()))
                .body("endereco.uf", equalTo(clienteResponseDTO.endereco().getUf()))
                .body("endereco.numero", equalTo(clienteResponseDTO.endereco().getNumero()))
                .body("endereco.complemento", equalTo(clienteResponseDTO.endereco().getComplemento()));

    }

    @Quando("eu atualizar esse cliente")
    public void eu_atualizar_esse_cliente() {
        var clienteAtualizado = getClienteRequest();
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(clienteAtualizado)
                .when()
                .put(ENDPOINT_API_CLIENTE + "/atualizar/" + clienteResponseDTO.id());

        clienteResponseDTO = response.then().extract().as(ClienteResponseDTO.class);
    }

    @Entao("o cliente deve ser atualizado com sucesso")
    public void o_cliente_deve_ser_atualizado_com_sucesso() {
        response.then().statusCode(HttpStatus.OK.value());
    }

    @Entao("deve ser apresentado atualizado")
    public void deve_ser_apresentado_atualizado() {
        Integer actualId = response.then().extract().path("id"); // Extrair como Integer
        Long expectedId = clienteResponseDTO.id(); // Supondo que getId() retorna um Long

        // Converter o valor retornado para Long antes de comparar
        Long actualIdLong = actualId.longValue();

        assertEquals(expectedId, actualIdLong);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("nome", equalTo(clienteResponseDTO.nome()))
                .body("email", equalTo(clienteResponseDTO.email()))
                .body("cpf", equalTo(clienteResponseDTO.cpf()))
                .body("telefone", equalTo(clienteResponseDTO.telefone()))
                .body("endereco.cep", equalTo(clienteResponseDTO.endereco().getCep()))
                .body("endereco.logradouro", equalTo(clienteResponseDTO.endereco().getLogradouro()))
                .body("endereco.bairro", equalTo(clienteResponseDTO.endereco().getBairro()))
                .body("endereco.cidade", equalTo(clienteResponseDTO.endereco().getCidade()))
                .body("endereco.uf", equalTo(clienteResponseDTO.endereco().getUf()))
                .body("endereco.numero", equalTo(clienteResponseDTO.endereco().getNumero()))
                .body("endereco.complemento", equalTo(clienteResponseDTO.endereco().getComplemento()));
    }

    @Quando("deletar esse cliente")
    public void deletar_esse_cliente() {
        response = given()
                .when()
                .delete(ENDPOINT_API_CLIENTE + "/deletar/" + clienteResponseDTO.id());
    }

    @Entao("o cliente deve ser deletado com sucesso")
    public void o_cliente_deve_ser_deletado_com_sucesso() {
        response.then().statusCode(HttpStatus.NO_CONTENT.value());
    }



    private ClienteRequestDTO getClienteRequest() {
        return new ClienteRequestDTO(
                "Guilherme Matos de Carvalho",
                "8Xa5I@example.com",
                "Sdsadwd21321@#$",
                "11987654321",
                gerarCpfsAleatorios(),
                "147852369",
                new Endereco(
                        "14785239",
                        "123",
                        "Casa",
                        "Centro",
                        "São Paulo",
                        "SP",
                        "12345678",
                        8787.44,
                        11.226
                )
        );
    }

    private String gerarCpfsAleatorios() {
        Random rand = new Random();
        StringBuilder numeros = new StringBuilder();

        // Gera 11 números aleatórios
        for (int i = 0; i < 11; i++) {
            numeros.append(rand.nextInt(10)); // Gera números de 0 a 9
        }

        return numeros.toString();
    }
}
