package br.com.mspedidos.infra.repository;

import br.com.mspedidos.domain.entity.Pedido;
import br.com.mspedidos.domain.enums.StatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends MongoRepository<Pedido, String> {

    List<Pedido> findByStatus(StatusEnum status);
}
