package br.com.msprodutos.domain.service;


import br.com.msprodutos.application.controller.exceptions.EstoqueException;
import br.com.msprodutos.application.controller.exceptions.NaoEncontradoException;
import br.com.msprodutos.application.request.ProdutoPedidoRequestDTO;
import br.com.msprodutos.application.request.ProdutoRequestDTO;
import br.com.msprodutos.application.response.ConsultaBaixaEstoqueProdutoResponseDTO;
import br.com.msprodutos.application.response.ProdutoResponseDTO;
import br.com.msprodutos.domain.enitity.Produto;
import br.com.msprodutos.repository.ProdutoRepository;
import br.com.msprodutos.utils.Utils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@EnableScheduling
@Service
public class ProdutoService {

    //private final Path fileStorageLocation;

    @Value("${file.storage.location}")
    private String fileStorageLocation;

    private final JobLauncher jobLauncher;
    private final Job job;
    private final ProdutoRepository produtoRepository;
    private final Utils utils;

    public ProdutoService(@Qualifier("jobLauncherAsync") JobLauncher jobLauncher,
                          Job job, ProdutoRepository produtoRepository,
                          Utils utils) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.produtoRepository = produtoRepository;
        this.utils = utils;
    }

    public void uploadProdutoFile(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Criar um arquivo temporário no diretório temporário do sistema
        Path tempFile = Files.createTempFile("upload-", ".csv");

        // Transferir o conteúdo do arquivo MultipartFile para o arquivo temporário
        file.transferTo(tempFile.toFile());

        // Criar os parâmetros do job
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("produtos", fileName)
                .addString("produtosFile", "file:" + tempFile.toString())
                .toJobParameters();

        // Executar o job
        jobLauncher.run(job, jobParameters);
    }

    //@Scheduled(fixedRate = 10000)
    public void processarProdutosAgendados() {
        Path dirPath = Paths.get(fileStorageLocation);

        if (!Files.exists(dirPath)) {
            System.err.println("O diretório " + fileStorageLocation + " não existe.");
            return;
        }

        try {
            Files.list(dirPath).forEach(filePath -> {
                JobParameters jobParameters = new JobParametersBuilder()
                        .addString("produtosFile", "file:" + filePath.toAbsolutePath().toString())
                        .addDate("date", new Date())
                        .toJobParameters();

                try {
                    jobLauncher.run(job, jobParameters);
                } catch (Exception e) {
                    System.err.println("Erro ao executar o job: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            System.err.println("Erro ao listar arquivos no diretório: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public ProdutoResponseDTO cadastrarProdutoIndividualmente(ProdutoRequestDTO produtoRequestDTO) {
        Produto produto = toEntity(produtoRequestDTO);
        return toResponseDTO(produtoRepository.save(produto));
    }

    public List<ProdutoResponseDTO> cadastrarProdutoEmLote(List<ProdutoRequestDTO> produtosDTO) {
        List<Produto> produtos = new ArrayList<>();
        produtosDTO.forEach(produtoDTO -> {
            Produto produto = toEntity(produtoDTO);
            produtos.add(produto);
        });

        return produtoRepository.saveAll(produtos).stream().map(this::toResponseDTO).toList();
    }

    public List<ConsultaBaixaEstoqueProdutoResponseDTO> consultaEDaBaixaNoEstoque(List<ProdutoPedidoRequestDTO> produtos) {
        List<Produto> produtosASeremAtualizados = new ArrayList<>();
        for (ProdutoPedidoRequestDTO produto : produtos) {

            Optional<Produto> produtoOptional = produtoRepository.findById(produto.idProduto());
            if (produtoOptional.isEmpty()) {
                throw new NaoEncontradoException(String.format("Produto com o id '%s' não encontrado",
                        produto.idProduto())
                );
            }
            Produto produtoEncontrado = produtoOptional.get();
            Integer quantidadeEmEstoque = produtoEncontrado.getQuantidadeEstoque();
            if (quantidadeEmEstoque < produto.quantidade()) {
                throw new EstoqueException(String.format("Produto com o id '%s' está fora de estoque",
                        produto.idProduto())
                );
            }
            produtoEncontrado.setQuantidadeEstoque(quantidadeEmEstoque - produto.quantidade());
            produtosASeremAtualizados.add(produtoEncontrado);
        }
        List<Produto> produtosAtualizados = produtoRepository.saveAll(produtosASeremAtualizados);
        return montaRetorno(produtosAtualizados, produtos);
    }

    public Page<ProdutoResponseDTO> listaProdutos(Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findAll(pageable);
        return produtos.map(this::toResponseDTO);
    }

    public ProdutoResponseDTO atualizarProduto(Integer idProduto, ProdutoRequestDTO produtoRequestDTO) {
        Produto produto = produtoRepository.findById(idProduto).orElseThrow(() -> new NaoEncontradoException(
                String.format("Produto com o id '%s' não encontrado", idProduto)));

        utils.copyNonNullProperties(produtoRequestDTO, produto);

        return toResponseDTO(produtoRepository.save(produto));
    }

    public Produto toEntity(ProdutoRequestDTO produtoRequestDTO) {
        return new Produto(produtoRequestDTO.descricao(),
                produtoRequestDTO.quantidadeEstoque(),
                produtoRequestDTO.valor()
        );
    }

    public ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getDescricao(),
                produto.getQuantidadeEstoque(),
                produto.getValor()
        );
    }

    public List<ConsultaBaixaEstoqueProdutoResponseDTO> montaRetorno(List<Produto> produtos,
                                                                     List<ProdutoPedidoRequestDTO> produtosDoPedido) {
        List<ConsultaBaixaEstoqueProdutoResponseDTO> response = new ArrayList<>();
        for (Produto produto : produtos) {
            Optional<ProdutoPedidoRequestDTO> produtoDoPedido = produtosDoPedido
                    .stream()
                    .filter(prod -> Objects.equals(prod.idProduto(), produto.getId()))
                    .findFirst();
            if (produtoDoPedido.isPresent()) {
                ConsultaBaixaEstoqueProdutoResponseDTO consultaBaixaEstoqueProdutoResponseDTO = new ConsultaBaixaEstoqueProdutoResponseDTO(produto.getId(),
                        produto.getDescricao(),
                        produtoDoPedido.get().quantidade(),
                        produto.getValor());
                response.add(consultaBaixaEstoqueProdutoResponseDTO);
            }
        }
        return response;
    }
}
