//package br.com.mslogisticaentrega.domain.service;
//
//import br.com.mslogisticaentrega.application.controller.response.EntregadorResponse;
//import br.com.mslogisticaentrega.domain.entity.EntregadorEntity;
//import br.com.mslogisticaentrega.infra.exceptions.NaoEncontradoException;
//import br.com.mslogisticaentrega.infra.repository.EntregadorRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@AutoConfigureTestDatabase
//@Transactional
//public class EntregadorServiceIT {
//    @Autowired
//    private EntregadorRepository entregadorRepository;
//    @Autowired
//    private EntregadorService entregadorService;
//
//    private EntregadorEntity entregadorEntity;
//
//    @BeforeEach
//    void setup(){
////        entregadorEntity = criarEntregador();
//    }
//
//    @AfterEach
//    void tearDown(){
//        entregadorRepository.deleteAll();
//    }
//
//    @Test
//    void deveCriarTabela(){
//        var totalRegistros = entregadorRepository.count();
//        assertThat(totalRegistros).isNotNegative();
//    }
//
//    @Nested
//    class BuscarTodos{
//        @Test
//        void deveGerarExcecao_QuandoBuscarTodosEntregadores(){
//            entregadorRepository.findAll();
//            assertThatThrownBy(() -> entregadorService.buscarTodos())
//                    .isInstanceOf(NaoEncontradoException.class)
//                    .hasMessage("Nenhum entregador cadastrado no sistema!");
//        }
//
//        @Test
//        void deveBuscarTodosEntregadores(){
//            entregadorEntity = criarEntregador();
//            var entregador = List.of(entregadorService.toResponse(entregadorEntity));
//
//            var resultado = entregadorService.buscarTodos();
//
//            assertEquals(entregador, resultado);
//        }
//    }
//
//
//
//    private EntregadorEntity criarEntregador(){
//        return entregadorRepository.save(new EntregadorEntity(
//                1,
//                "Teste",
//                "11111111111",
//                "teste@teste.com"
//        ));
//    }
//}
