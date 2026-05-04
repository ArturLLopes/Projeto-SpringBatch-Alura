# 🎫 CodeTickets — Processamento em Lote com Spring Batch

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white"/>
</p>

---

## 📋 Sobre o Projeto

O **CodeTickets** é uma aplicação de **processamento em lote (batch processing)** desenvolvida durante o curso de Spring Batch da [Alura](https://www.alura.com.br/). O objetivo do projeto é demonstrar, na prática, como processar grandes volumes de dados de forma eficiente, confiável e rastreável utilizando o ecossistema Spring.

O sistema realiza a leitura, processamento e escrita de dados de tickets em lote, integrando-se com um banco de dados PostgreSQL para persistência e controle de estado dos jobs.

---

## 🚀 Tecnologias Utilizadas

### ☕ Java
**Por quê?** Linguagem principal do ecossistema Spring. Fortemente tipada, madura e amplamente adotada no mercado para aplicações back-end robustas e de alta performance. Garante segurança de tipos em tempo de compilação e excelente suporte a orientação a objetos.

---

### 🌱 Spring Boot
**Por quê?** Simplifica a configuração e o bootstrap da aplicação, eliminando a necessidade de configurações XML extensas. O Spring Boot oferece um servidor embutido, autoconfiguração e um ecossistema rico de starters, acelerando o desenvolvimento sem abrir mão de produção.

---

### ⚙️ Spring Batch
**Por quê?** É o framework padrão do ecossistema Spring para **processamento em lote**. Oferece suporte nativo a:
- **Jobs** e **Steps** para organizar o fluxo de processamento;
- **ItemReader**, **ItemProcessor** e **ItemWriter** para a arquitetura de chunk-oriented processing;
- **Retry** e **Skip** automáticos em caso de falhas;
- **Restart** de jobs a partir do ponto de falha, sem reprocessar dados já confirmados;
- Rastreamento completo do estado de execução via metadados no banco de dados.

É a escolha ideal para cenários onde se precisa processar arquivos CSV, migrar dados ou executar transformações em grandes volumes de registros.

---

### 🐘 PostgreSQL
**Por quê?** Banco de dados relacional open-source robusto e confiável. O Spring Batch utiliza tabelas de metadados para persistir o estado dos Jobs (como `BATCH_JOB_INSTANCE`, `BATCH_JOB_EXECUTION`, `BATCH_STEP_EXECUTION`), e o PostgreSQL oferece excelente suporte a transações ACID, garantindo a integridade e o controle preciso de cada execução batch. Além disso, é amplamente utilizado em ambientes de produção.

---

### 🗄️ Spring Data JPA
**Por quê?** Abstrai a camada de persistência com o banco de dados. Utiliza o Hibernate como implementação padrão do JPA, permitindo mapear entidades Java diretamente para tabelas do banco de dados sem escrever SQL boilerplate. Facilita operações de leitura e escrita dos tickets processados, com suporte a repositórios, queries derivadas e paginação.

---

### 🌐 Spring Initializr
**Por quê?** Ferramenta oficial da Spring para geração de projetos com estrutura padronizada. Permite selecionar as dependências necessárias (Spring Batch, PostgreSQL Driver, Spring Data JPA) e gera um projeto pronto para uso com Maven/Gradle já configurado, acelerando o setup inicial e seguindo as boas práticas do ecossistema.

---

### 🔧 Git & GitHub
**Por quê?** Utilizados para **controle de versão** e **hospedagem do código-fonte**. O Git permite rastrear todo o histórico de alterações do projeto, criar branches para desenvolver funcionalidades de forma isolada e reverter mudanças quando necessário. O GitHub complementa oferecendo uma plataforma colaborativa, acesso remoto ao repositório e visibilidade do projeto.

---

## 🏗️ Arquitetura do Projeto

```
Projeto-SpringBatch-Alura/
└── codetickets/
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/codetickets/
    │   │   │       ├── batch/         # Jobs, Steps, Reader, Processor, Writer
    │   │   │       ├── domain/        # Entidades JPA (Ticket, etc.)
    │   │   │       ├── repository/    # Repositórios Spring Data
    │   │   │       └── config/        # Configurações do Spring Batch
    │   │   └── resources/
    │   │       └── application.properties
    │   └── test/
    └── pom.xml
```

---

## 🔄 Fluxo de Processamento Batch

```
[ ItemReader ]        [ ItemProcessor ]        [ ItemWriter ]
   Lê os dados    →   Processa/Transforma   →   Persiste no BD
  (CSV / BD)           os registros             (PostgreSQL)
        ↑
     [ Job ] → [ Step ] → chunk-oriented processing
```

O Spring Batch divide o processamento em **chunks** (lotes menores), garantindo que cada chunk seja confirmado em uma transação independente. Em caso de falha, apenas o chunk atual é reprocessado, não o job inteiro.

---

## ⚙️ Pré-requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

- [Java 17+](https://www.oracle.com/java/technologies/downloads/)
- [Maven 3.8+](https://maven.apache.org/download.cgi)
- [PostgreSQL 14+](https://www.postgresql.org/download/)
- [Git](https://git-scm.com/)

---

## 🗃️ Configuração do Banco de Dados

1. Crie o banco de dados no PostgreSQL:

```sql
CREATE DATABASE codetickets;
```

2. Configure as credenciais no arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/codetickets
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.batch.jdbc.initialize-schema=always
```

> **Nota:** A propriedade `spring.batch.jdbc.initialize-schema=always` faz com que o Spring Batch crie automaticamente suas tabelas de metadados no banco de dados na primeira execução.

---

## ▶️ Como Executar

### Clonando o repositório

```bash
git clone https://github.com/ArturLLopes/Projeto-SpringBatch-Alura.git
cd Projeto-SpringBatch-Alura/codetickets
```

### Executando com Maven

```bash
./mvnw spring-boot:run
```

### Gerando o JAR e executando

```bash
./mvnw clean package
java -jar target/codetickets-*.jar
```

---

## 📊 Tabelas de Metadados do Spring Batch

O Spring Batch cria e gerencia automaticamente as seguintes tabelas no PostgreSQL para rastrear a execução dos jobs:

| Tabela                        | Descrição                                      |
|-------------------------------|------------------------------------------------|
| `BATCH_JOB_INSTANCE`          | Registro de cada instância de Job              |
| `BATCH_JOB_EXECUTION`         | Histórico de execuções de cada Job             |
| `BATCH_JOB_EXECUTION_PARAMS`  | Parâmetros passados a cada execução            |
| `BATCH_STEP_EXECUTION`        | Detalhes de execução de cada Step              |
| `BATCH_STEP_EXECUTION_CONTEXT`| Estado de contexto de cada Step                |
| `BATCH_JOB_EXECUTION_CONTEXT` | Estado de contexto global do Job               |

---

## 📚 Referências

- [Documentação oficial do Spring Batch](https://docs.spring.io/spring-batch/docs/current/reference/html/)
- [Spring Initializr](https://start.spring.io/)
- [Documentação do PostgreSQL](https://www.postgresql.org/docs/)
- [Curso Alura](https://www.alura.com.br/)

---

## 👨‍💻 Autor

Desenvolvido por **[Artur L. Lopes](https://github.com/ArturLLopes)** como parte do aprendizado na plataforma Alura.

---

<p align="center">
  Feito com ☕ e 🍃 Spring
</p>