package br.com.cadastrocliente.mscadastrocliente.infra.repository;

import br.com.cadastrocliente.mscadastrocliente.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRespository extends JpaRepository<Cliente, Long> {

    Boolean existsByCpf(String cpf);


}
