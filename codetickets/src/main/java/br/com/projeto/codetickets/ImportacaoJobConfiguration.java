package br.com.projeto.codetickets;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ImportacaoJobConfiguration {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public Job job(Step passoInicial, JobRepository jobRepository) {
        return new JobBuilder("geracao-tickets", jobRepository)
                .start(passoInicial)
                .next(moverArquivosStep(jobRepository))
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step passoInicial(ItemReader<Importacao> reader, ItemWriter<Importacao> writer, JobRepository jobRepository) {
        return new StepBuilder("passo-inicial", jobRepository)
                .<Importacao, Importacao>chunk(200, transactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<Importacao> reader() {
        return new FlatFileItemReaderBuilder<Importacao>()
                .name("leitura-csv")
                .resource(new ClassPathResource("files/dados.csv"))
                .comments("--")
                .delimited()
                .delimiter(";")
                .names("cpf", "cliente", "nascimento", "evento", "data", "tipoIngresso", "valor")
                .fieldSetMapper(new ImportacaoMapper())
                .build();
    }

    @Bean
    public ItemWriter<Importacao> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Importacao>()
                .dataSource(dataSource)
                .sql(
                        "INSERT INTO importacao (cpf, cliente, nascimento, evento, data, tipo_ingresso, valor, hora_importacao, taxa_adm) VALUES" +
                                " (:cpf, :cliente, :nascimento, :evento, :data, :tipoIngresso, :valor, :horaImportacao, :taxaAdm)"

                )
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
    @Bean
    public ImportacaoProcessor processor() {
        return new ImportacaoProcessor();
    }

    @Bean
    public Tasklet moverArquivosTasklet() {
        return (contribution, chunkContext) -> {
            String raiz = System.getProperty("user.dir");

            File pastaOrigem = new File(raiz + "/codetickets/src/main/resources/files");
            File pastaDestino = new File(raiz + "/codetickets/imported-files");

            if (!pastaDestino.exists()) {
                pastaDestino.mkdirs();
            }

            File[] arquivos = pastaOrigem.listFiles((dir, name) -> name.endsWith(".csv"));

            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    // Gera o sufixo com data e hora
                    String dataHora = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

                    //Separa nome e extensão e insere a data no meio
                    String nomeOriginal = arquivo.getName(); // ex: dados.csv
                    String nome = nomeOriginal.replace(".csv", ""); // ex: dados
                    String novoNome = nome + "_" + dataHora + ".csv";

                    Files.move(
                            arquivo.toPath(),
                            new File(pastaDestino, novoNome).toPath(),
                            StandardCopyOption.REPLACE_EXISTING // 👈 evita erro se já existir
                    );

                    System.out.println("Arquivo movido e renomeado para: " + novoNome);
                }
            } else {
                System.out.println("Nenhum arquivo encontrado em: " + pastaOrigem.getAbsolutePath());
            }
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step moverArquivosStep(JobRepository jobRepository) {
        return new StepBuilder("mover-arquivo", jobRepository)
                .tasklet(moverArquivosTasklet(), transactionManager)
                .build();
    }
}
