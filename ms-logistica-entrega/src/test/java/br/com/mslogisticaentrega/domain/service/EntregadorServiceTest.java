package br.com.mslogisticaentrega.domain.service;

import br.com.mslogisticaentrega.infra.exceptions.JaCadastradoException;
import br.com.mslogisticaentrega.infra.exceptions.NaoEncontradoException;
import br.com.mslogisticaentrega.application.controller.request.EntregadorRequest;
import br.com.mslogisticaentrega.application.controller.response.EntregadorResponse;
import br.com.mslogisticaentrega.domain.entity.EntregadorEntity;
import br.com.mslogisticaentrega.infra.repository.EntregadorRepository;
import br.com.mslogisticaentrega.application.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EntregadorServiceTest {
    @Mock
    private EntregadorRepository entregadorRepository;

    private EntregadorService entregadorService;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        entregadorService = new EntregadorService(entregadorRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Nested
    class BuscarTodos{
        @Test
        void deveBuscarTodosEntregadores(){
            List<EntregadorResponse> entregadoresLista = List.of(
                    entregadorService.toResponse(buildEntregadorEntity()));

            when(entregadorRepository.findAll()).thenReturn(List.of(buildEntregadorEntity()));

            var resultado = entregadorService.buscarTodos();

            assertEquals(entregadoresLista, resultado);

            verify(entregadorRepository).findAll();
        }

        @Test
        void deveGerarExcecao_QuandoBuscarTodosEntregadores(){
            List<EntregadorResponse> entregadoresLista = List.of(
                    entregadorService.toResponse(buildEntregadorEntity()));

            when(entregadorRepository.findAll()).thenReturn(List.of());

            assertThatThrownBy(() -> entregadorService.buscarTodos())
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage("Nenhum entregador cadastrado no sistema!");

            verify(entregadorRepository).findAll();
        }
    }

    @Nested
    class CriarEntregador{
        @Test
        void deveCriarEntregador(){
            var request = buildEntregadorRequest();
            var entity = buildEntregadorEntity();
            var resposta = entregadorService.toResponse(entity);

            when(entregadorRepository.existsByCpf(request.getCpf())).thenReturn(false);
            when(entregadorRepository.saveAndFlush(request.toEntity())).thenReturn(entity);

            var resultado = entregadorService.criar(request);

            assertEquals(resposta, resultado);

            verify(entregadorRepository, times(1)).existsByCpf(request.getCpf());
            verify(entregadorRepository, times(1)).saveAndFlush(request.toEntity());
        }

        @Test
        void deveGerarExcecao_JaCadastrado_QuandoCriarEntregador(){
            var request = buildEntregadorRequest();

            when(entregadorRepository.existsByCpf(request.getCpf())).thenReturn(true);

            assertThatThrownBy(() -> entregadorService.criar(request))
                    .isInstanceOf(JaCadastradoException.class)
                    .hasMessage(String.format("Entregador com cpf:[%s] ja esta cadastrado!", request.getCpf()));

            verify(entregadorRepository, times(1)).existsByCpf(request.getCpf());
        }
    }

    @Nested
    class BuscarEntregador{
        @Test
        void deveBuscarEntregadorPorId() {
            var entity = buildEntregadorEntity();

            when(entregadorRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

            var resultado = entregadorService.buscarPorId(entity.getId());

            assertEquals(entregadorService.toResponse(entity), resultado);

            verify(entregadorRepository, times(1)).findById(entity.getId());
        }

        @Test
        void deveGerarExcecao_NaoEncontrado_QuandoBuscarEntregador() {
            var entity = buildEntregadorEntity();
            var id = entity.getId();

            when(entregadorRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> entregadorService.buscarPorId(id))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(String.format("Entregador com o id:[%s] nao foi encontrado!", id));

            verify(entregadorRepository, times(1)).findById(id);
        }
    }

    @Nested
    class AtualizarEntregador{
        @Test
        void deveAtualizarEntregadorPorId() {
            var request = buildEntregadorRequest();
            request.setNome("Cesar Alves");

            var entityBanco = buildEntregadorEntity();
            var id = entityBanco.getId();

            var entityAtualizada = buildEntregadorEntity();
            Utils.copyNonNullProperties(request, entityAtualizada);

            when(entregadorRepository.findById(id)).thenReturn(Optional.of(entityBanco));
            when(entregadorRepository.save(entityAtualizada)).thenReturn(entityAtualizada);

            var resultado = entregadorService.atualizar(id, request);

            assertEquals(entregadorService.toResponse(entityAtualizada), resultado);

            verify(entregadorRepository, times(1)).findById(id);
            verify(entregadorRepository, times(1)).save(entityAtualizada);
        }

        @Test
        void deveGerarExcecao_NaoEncontrado_QuandoAtualizarEntregador() {
            var request = buildEntregadorRequest();
            var id = 1;

            when(entregadorRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> entregadorService.atualizar(id, request))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(String.format("Entregador com o id:[%s] nao foi encontrado!", id));

            verify(entregadorRepository, times(1)).findById(id);
        }
    }

    @Nested
    class DeletarEntregador{
        @Test
        void deveDeletarEntregador(){
            var entity = buildEntregadorEntity();
            var id = entity.getId();

            when(entregadorRepository.findById(id)).thenReturn(Optional.of(entity));
            doNothing().when(entregadorRepository).delete(entity);

            entregadorService.deletar(id);

            verify(entregadorRepository, times(1)).findById(id);
            verify(entregadorRepository, times(1)).delete(entity);
        }

        @Test
        void deveGerarExcecao_NaoEncontrado_QuandoDeletarEntregador(){
            var entity = buildEntregadorEntity();
            var id = entity.getId();
            when(entregadorRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> entregadorService.deletar(id))
                    .isInstanceOf(NaoEncontradoException.class)
                    .hasMessage(String.format("Entregador com o id:[%s] nao foi encontrado!", id));

            verify(entregadorRepository, times(1)).findById(id);
        }
    }

    private EntregadorRequest buildEntregadorRequest(){
        return new EntregadorRequest("Cesar", "33244565367", "cesar@gmail.com");
    }

    private EntregadorEntity buildEntregadorEntity(){
        return new EntregadorEntity(1, "Cesar", "33244565367", "cesar@gmail.com");
    }
}
