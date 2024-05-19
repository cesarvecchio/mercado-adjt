package br.com.mslogisticaentrega.domain.service;

import br.com.mslogisticaentrega.infra.exceptions.JaCadastradoException;
import br.com.mslogisticaentrega.infra.exceptions.NaoEncontradoException;
import br.com.mslogisticaentrega.application.controller.request.EntregadorRequest;
import br.com.mslogisticaentrega.application.controller.response.EntregadorResponse;
import br.com.mslogisticaentrega.domain.entity.EntregadorEntity;
import br.com.mslogisticaentrega.infra.repository.EntregadorRepository;
import br.com.mslogisticaentrega.application.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntregadorService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final EntregadorRepository entregadorRepository;

    public EntregadorService(EntregadorRepository entregadorRepository) {
        this.entregadorRepository = entregadorRepository;
    }

    public List<EntregadorResponse> buscarTodos(){
        logger.info("Buscando Todos os Entregadores");
        List<EntregadorEntity> entregadorEntityList = entregadorRepository.findAll();

        if(entregadorEntityList.isEmpty()){
            logger.warn("Nenhum entregador encontrado");

            throw new NaoEncontradoException("Nenhum entregador cadastrado no sistema!");
        }

        return entregadorEntityList.stream()
                .map(EntregadorResponse::new)
                .toList();
    }

    public EntregadorResponse criar(EntregadorRequest request) {
        validarCpf(request.getCpf());

        EntregadorEntity entity = entregadorRepository.saveAndFlush(request.toEntity());

        return new EntregadorResponse(entity);
    }

    public EntregadorResponse buscarPorId(Integer id) {
        return new EntregadorResponse(validarPorId(id));
    }

    public EntregadorResponse atualizar(Integer id, EntregadorRequest request) {
        EntregadorEntity entregador = validarPorId(id);

        Utils.copyNonNullProperties(request, entregador);

        return new EntregadorResponse(entregadorRepository.save(entregador));
    }

    public void deletar(Integer id) {
        entregadorRepository.delete(validarPorId(id));
    }

    private EntregadorEntity validarPorId(Integer id) {
        return entregadorRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException(
                        String.format("Entregador com o id:[%s] nao foi encontrado!", id)
                ));
    }

    private void validarCpf(String cpf){
        if(entregadorRepository.existsByCpf(cpf)) {
            throw new JaCadastradoException(String.format("Entregador com cpf:[%s] ja esta cadastrado!", cpf));
        }
    }

}
