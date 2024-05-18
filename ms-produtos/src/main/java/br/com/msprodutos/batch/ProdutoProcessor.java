package br.com.msprodutos.batch;

import br.com.msprodutos.domain.enitity.Produto;
import br.com.msprodutos.repository.ProdutoRepository;
import org.springframework.batch.item.ItemProcessor;

import java.util.Optional;

public class ProdutoProcessor implements ItemProcessor<Produto, Produto> {

    private final ProdutoRepository produtoRepository;

    public ProdutoProcessor(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public Produto process(Produto item) throws Exception {
        Optional<Produto> produto = produtoRepository.findById(item.getId());

        produto.ifPresent(value -> item.setQuantidadeEstoque(item.getQuantidadeEstoque() + value.getQuantidadeEstoque()));

        return item;
    }
}
