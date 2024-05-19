package br.com.mslogisticaentrega.infra.repository;

import br.com.mslogisticaentrega.domain.entity.EntregadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregadorRepository extends JpaRepository<EntregadorEntity, Integer> {

    boolean existsByCpf(String cpf);
}
