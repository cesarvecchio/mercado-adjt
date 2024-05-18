package br.com.msprodutos.batch;

import br.com.msprodutos.domain.enitity.Produto;
import br.com.msprodutos.repository.ProdutoRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
public class BatchConfiguration {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Bean
    public Job processarProduto(JobRepository jobRepository, Step step) {
        return new JobBuilder("processarProduto", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager platformTransactionManager,
                     ItemReader<Produto> itemReader,
                     ItemWriter<Produto> itemWriter,
                     ItemProcessor<Produto, Produto> itemProcessor) {
        return new StepBuilder("step", jobRepository)
                .<Produto, Produto>chunk(10, platformTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Produto> itemReader(@Value("#{jobParameters['produtosFile']}") Resource resource) {
        BeanWrapperFieldSetMapper<Produto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Produto.class);

        return new FlatFileItemReaderBuilder<Produto>()
                .name("produtoItemReader")
                .resource(resource)
                .delimited()
                .names("id", "descricao", "quantidadeEstoque", "valor")
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    @Bean
    public ItemWriter<Produto> itemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Produto>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .sql("insert into produto (descricao, quantidade_estoque, valor) values (:descricao, :quantidadeEstoque, :valor)")
                .build();
    }

    @Bean
    public ItemProcessor<Produto, Produto> itemProcessor() {
        return new ProdutoProcessor(produtoRepository);
    }

    @Bean
    public JobLauncher jobLauncherAsync(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}
