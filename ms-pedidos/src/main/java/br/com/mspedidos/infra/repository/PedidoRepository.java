package br.com.mspedidos.infra.repository;

import br.com.mspedidos.domain.entity.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends MongoRepository<Pedido, String> {
}
