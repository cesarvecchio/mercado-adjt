package br.com.msprodutos.repository;

import br.com.msprodutos.domain.enitity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
}
