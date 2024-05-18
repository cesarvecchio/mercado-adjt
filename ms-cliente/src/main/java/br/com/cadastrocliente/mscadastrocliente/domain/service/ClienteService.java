package br.com.cadastrocliente.mscadastrocliente.domain.service;

import br.com.cadastrocliente.mscadastrocliente.application.controller.exception.CpfException;
import br.com.cadastrocliente.mscadastrocliente.application.controller.exception.NaoEncontradoException;
import br.com.cadastrocliente.mscadastrocliente.application.request.ClienteRequestDTO;
import br.com.cadastrocliente.mscadastrocliente.application.response.ClienteResponseDTO;
import br.com.cadastrocliente.mscadastrocliente.domain.entity.Cliente;
import br.com.cadastrocliente.mscadastrocliente.infra.repository.ClienteRespository;
import br.com.cadastrocliente.mscadastrocliente.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRespository clienteRespository;

    private final Utils utils;

    public ClienteService(ClienteRespository clienteRespository, Utils utils) {
        this.clienteRespository = clienteRespository;
        this.utils = utils;
    }

    public List<ClienteResponseDTO> obterTodos() {
        return clienteRespository
                .findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ClienteResponseDTO cadastrarCliente(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = toEntity(clienteRequestDTO);

        if (cpfExistente(clienteRequestDTO.cpf())) {
            throw new CpfException("Esse cpf já está sendo utilizado");
        }

        clienteRespository.save(cliente);

        return toResponseDTO(cliente);
    }

    public ClienteResponseDTO obterClienteResponsePorId(Long id) {
        return toResponseDTO(obterClientePorId(id));
    }

    public Cliente obterClientePorId(Long id) {
        return clienteRespository.findById(id).orElseThrow(() -> new NaoEncontradoException(
                String.format("Cliente com o id '%d' não encontrado", id)
        ));
    }

    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO clienteRequestDTO) {

        Cliente cliente = obterClientePorId(id);

        if (clienteRequestDTO.cpf() != null && !clienteRequestDTO.cpf().equals(cliente.getCpf())) {
            if (cpfExistente(clienteRequestDTO.cpf()))
                throw new CpfException("Esse cpf já está sendo utilizado");
        }
        utils.copyNonNullProperties(clienteRequestDTO, cliente);

        clienteRespository.save(cliente);

        return toResponseDTO(cliente);

    }

    public void deletarCliente(Long id) {
        existePorId(id);

        clienteRespository.deleteById(id);
    }

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getEndereco()
        );
    }

    public Cliente toEntity(ClienteRequestDTO clienteRequestDTO) {
        return Cliente.builder()
                .nome(clienteRequestDTO.nome())
                .cpf(clienteRequestDTO.cpf())
                .rg(clienteRequestDTO.rg())
                .email(clienteRequestDTO.email())
                .senha(clienteRequestDTO.senha())
                .telefone(clienteRequestDTO.telefone())
                .endereco(clienteRequestDTO.endereco())
                .build();
    }

    public ClienteRequestDTO toRequestDTO(Cliente cliente) {
        return new ClienteRequestDTO(
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getSenha(),
                cliente.getTelefone(),
                cliente.getCpf(),
                cliente.getRg(),
                cliente.getEndereco()
        );
    }

    private boolean cpfExistente(String cpf) {
        return clienteRespository.existsByCpf(cpf);
    }

    private void existePorId(Long idCliente) {
        if (!clienteRespository.existsById(idCliente)) {
            throw new NaoEncontradoException(
                    String.format("Cliente com o id '%d' não encontrado", idCliente));
        }
    }
}
