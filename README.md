# MasterSys

API REST para gerenciamento de uma academia. O sistema organiza alunos, matrículas, modalidades, planos e faturas, além de disponibilizar relatórios operacionais e financeiros.

## Funcionalidades

- Cadastro, consulta, atualização, listagem paginada e exclusão de alunos.
- Gerenciamento de modalidades e planos, incluindo ativação e inativação.
- Criação e acompanhamento do ciclo de vida de matrículas e vínculos com modalidades.
- Geração, consulta, pagamento e cancelamento de faturas.
- Atualização automática do status de faturas vencidas e geração periódica de faturas.
- Relatórios de faturamento mensal, alunos por cidade, faturas em aberto e faturas por status.
- Validação dos dados de entrada e respostas de erro padronizadas.

## Tecnologias

- Java 26
- Spring Boot 4.1
- Spring Web MVC, Spring Data JPA e Bean Validation
- PostgreSQL
- Flyway
- Maven
- JUnit 5, Mockito e MockMvc

## Arquitetura

O código-fonte fica em `src/main/java/dev/jose/mastersys` e está organizado por domínio:

| Pacote | Responsabilidade |
| --- | --- |
| `aluno` | Alunos, filtros, validação e consultas |
| `matricula` | Matrículas e vínculos entre matrículas, modalidades e planos |
| `modalidade` | Cadastro e ativação de modalidades |
| `plano` | Cadastro e ativação de planos |
| `fatura` | Faturas, regras de status e tarefas agendadas |
| `relatorio` | Consultas e endpoints de relatórios |
| `exception` | Exceções e tratamento global de erros |

Nos fluxos HTTP, o controller recebe a requisição e delega a operação ao service. Os services aplicam as regras de negócio e usam repositories para persistir ou consultar dados. DTOs e projections definem os formatos de entrada e saída da API.

## Pré-requisitos

- JDK 26 instalado e configurado no `JAVA_HOME`.
- PostgreSQL acessível localmente.
- Um banco de dados de desenvolvimento chamado `academia` (ou configuração equivalente via variáveis de ambiente).

## Configuração do banco

Por padrão, a aplicação conecta a `jdbc:postgresql://localhost:5432/academia` usando o usuário `postgres`. Configure as credenciais no ambiente em vez de salvar senhas no repositório.

No PowerShell, por exemplo:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/academia"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "<sua-senha>"
```

Crie o banco antes de iniciar a aplicação:

```sql
CREATE DATABASE academia;
```

As migrations do Flyway são executadas automaticamente ao iniciar a aplicação. Elas criam as tabelas e habilitam a extensão PostgreSQL `unaccent`, usada nas consultas de nomes sem distinção de acentos.

O Hibernate está configurado com `ddl-auto=validate`: ele valida o schema criado pelas migrations, mas não cria nem altera tabelas por conta própria.

> **Importante:** os testes de integração dos repositories inicializam o contexto Spring e usam a conexão PostgreSQL configurada pela aplicação. Execute-os contra um banco de teste ou descartável, nunca contra dados importantes.

## Executar a aplicação

Na raiz do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

A API fica disponível em `http://localhost:8081`.

## Documentação interativa da API

O projeto inclui Springdoc OpenAPI. Com a aplicação em execução, acesse:

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- Documento OpenAPI: `http://localhost:8081/v3/api-docs`

## Endpoints

Os endpoints de criação retornam `201 Created`. Operações de ativação, inativação, cancelamento e encerramento que não retornam conteúdo usam `204 No Content`. Listagens paginadas aceitam parâmetros como `page`, `size` e `sort`.

| Recurso | Método e rota | Descrição |
| --- | --- | --- |
| Alunos | `POST /alunos` | Cadastrar aluno |
|  | `GET /alunos` | Listar e filtrar alunos |
|  | `GET /alunos/{id}` | Buscar aluno |
|  | `PUT /alunos/{id}` | Atualizar aluno |
|  | `PATCH /alunos/{id}` | Atualizar parcialmente |
|  | `DELETE /alunos/{id}` | Excluir aluno; retorna conflito se houver registros relacionados |
| Modalidades | `POST /modalidades` | Cadastrar modalidade |
|  | `GET /modalidades` | Listar modalidades |
|  | `GET /modalidades/disponiveis` | Listar modalidades ativas |
|  | `GET /modalidades/{id}` | Buscar modalidade |
|  | `PUT /modalidades/{id}` | Atualizar modalidade |
|  | `PATCH /modalidades/{id}/ativar` | Ativar modalidade |
|  | `PATCH /modalidades/{id}/inativar` | Inativar modalidade |
|  | `DELETE /modalidades/{id}` | Excluir modalidade |
| Planos | `POST /planos` | Cadastrar plano |
|  | `GET /planos/{id}` | Buscar plano |
|  | `GET /planos/modalidade/{id}` | Listar planos de uma modalidade |
|  | `PUT /planos/{id}` | Atualizar plano |
|  | `PATCH /planos/{id}/ativar` | Ativar plano |
|  | `PATCH /planos/{id}/inativar` | Inativar plano |
|  | `DELETE /planos/{id}` | Excluir plano |
| Matrículas | `POST /matriculas` | Criar matrícula |
|  | `GET /matriculas` | Listar e filtrar matrículas |
|  | `GET /matriculas/{id}` | Buscar matrícula |
|  | `PATCH /matriculas/{id}/vencimento` | Alterar dia de vencimento |
|  | `PATCH /matriculas/{id}/cancelamento` | Cancelar matrícula |
|  | `PATCH /matriculas/{id}/encerramento` | Encerrar matrícula |
|  | `PATCH /matriculas/{id}/ativacao` | Ativar matrícula |
| Vínculos de modalidade | `POST /matriculas/modalidades` | Vincular uma modalidade/plano à matrícula |
|  | `GET /matriculas/{matriculaId}/modalidades` | Listar vínculos da matrícula |
|  | `GET /matriculas/modalidades/{id}` | Buscar vínculo |
|  | `PATCH /matriculas/modalidades/{id}/encerramento` | Encerrar vínculo |
| Faturas | `POST /faturas` | Criar fatura |
|  | `GET /faturas` | Listar e filtrar faturas |
|  | `GET /faturas/{id}` | Buscar fatura |
|  | `PATCH /faturas/{id}/pagar` | Pagar fatura |
|  | `PATCH /faturas/{id}/cancelar` | Cancelar fatura |
| Relatórios | `GET /relatorios/faturamento-mensal` | Somatório de faturas pagas agrupado por mês |
|  | `GET /relatorios/alunos-por-cidade?cidade=Cruzeiro` | Quantidade de alunos na cidade informada |
|  | `GET /relatorios/faturas-em-aberto` | Faturas abertas ou vencidas, ordenadas pelo vencimento |
|  | `GET /relatorios/faturas-por-status?status=PAGA` | Faturas filtradas por status |

Os status aceitos para faturas são `ABERTA`, `PAGA`, `CANCELADA` e `VENCIDA`.

### Exemplos de requisição

Cadastrar aluno:

```http
POST /alunos
Content-Type: application/json

{
  "nome": "Ana Silva",
  "dataNascimento": "1995-05-10",
  "sexo": "F",
  "celular": "11999999999",
  "email": "ana@example.com",
  "cidade": "Cruzeiro",
  "estado": "SP"
}
```

Criar matrícula:

```http
POST /matriculas
Content-Type: application/json

{
  "alunoId": 1,
  "diaVencimento": 10
}
```

Os campos restantes dos DTOs podem ser consultados na documentação OpenAPI interativa.

## Agendamentos

Com o agendamento do Spring habilitado:

- Faturas abertas vencidas são atualizadas para `VENCIDA` a cada hora.
- A geração das faturas do período é executada diariamente à 01:00.

Os horários seguem o timezone da JVM/servidor. Para ambientes com fuso horário diferente, configure o timezone da aplicação e revise os horários desejados.

## Testes

Executar toda a suíte:

```powershell
.\mvnw.cmd test
```

Executar uma classe específica:

```powershell
.\mvnw.cmd "-Dtest=AlunoServiceTest" test
```

O projeto possui:

- **Testes unitários de services e controllers**, com Mockito para isolar dependências.
- **Testes HTTP com MockMvc**, que verificam rotas, JSON, validações, status HTTP e tratamento de erros. Os services são mockados nesses testes.
- **Testes de integração de repositories**, que executam queries reais usando PostgreSQL.
- **Teste de contexto**, que verifica se a aplicação Spring inicia.

Os testes de repository e `contextLoads` precisam da conexão de banco configurada. Para execução segura, use um banco de teste dedicado e configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` antes de rodar os testes.

## Respostas de erro

Erros são retornados em um objeto com timestamp, status HTTP, título e lista de mensagens. Erros de validação e parâmetros inválidos retornam `400 Bad Request`; recursos não encontrados retornam `404 Not Found`; conflitos de negócio e exclusões impedidas por relacionamentos retornam `409 Conflict`.

## Estrutura de pastas

```text
src/
├── main/
│   ├── java/dev/jose/mastersys/
│   │   ├── aluno/
│   │   ├── exception/
│   │   ├── fatura/
│   │   ├── matricula/
│   │   ├── modalidade/
│   │   ├── plano/
│   │   └── relatorio/
│   └── resources/db/migration/
└── test/java/dev/jose/mastersys/
    ├── controller/
    ├── factory/
    ├── repository/
    ├── relatorio/
    └── service/
```

## Próximos passos possíveis

- Criar um perfil de teste com banco isolado (por exemplo, PostgreSQL em container) para não depender do banco local.
- Adicionar testes ponta a ponta que usem controllers, services e banco na mesma execução.
- Configurar autenticação/autorização caso a API seja exposta fora de ambiente local ou acadêmico.
- Desabilitar `spring.jpa.show-sql` e revisar `spring.jpa.open-in-view` para configurações de produção.

