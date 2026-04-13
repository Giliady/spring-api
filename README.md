# cora-api

API REST para **criar** e **listar** contas bancárias. Stack: **Java 17+**, **Spring Boot 3.x**, **H2** em memória.

## Pré-requisitos

### Execução local (Maven)

| Ferramenta | Versão |
|------------|--------|
| **JDK** | 17 ou superior |
| **Maven** | 3.9+ *(opcional — este repositório inclui Maven Wrapper)* |

### Execução com Docker

| Ferramenta | Versão |
|------------|--------|
| **Docker** | Docker Engine / Docker Desktop com Compose v2 |

Para desenvolver ou integrar um cliente **Node.js** (por exemplo um SPA em React), use a versão LTS atual do Node compatível com o seu frontend.

## Como rodar

### Local (Maven)

Na raiz do projeto:

```bash
mvn spring-boot:run
```

Com Maven Wrapper (Linux/macOS):

```bash
./mvnw spring-boot:run
```

No Windows (cmd/PowerShell):

```bash
mvnw.cmd spring-boot:run
```

### Docker

Na raiz do repositório, gerar a imagem e subir o serviço:

```bash
docker compose up --build
```

Ou somente com Docker:

```bash
docker build -t cora-api:local .
docker run --rm -p 8080:8080 cora-api:local
```

A API usa a mesma porta **8080** no host quando mapeada com `-p 8080:8080` ou via `docker-compose.yml`.

## Porta

Servidor HTTP: **`http://localhost:8080`**.

## Endpoints

| Método | Caminho | Descrição |
|--------|---------|-----------|
| `POST` | `/accounts` | Cria uma conta |
| `GET` | `/accounts` | Lista todas as contas |

### Corpo da requisição (POST)

```json
{
  "name": "Nome do Usuário",
  "cpf": "12345678901"
}
```

### Respostas

- **POST** bem-sucedido: **201 Created** + JSON com `id`, `name`, `cpf` e header `Location` com a URI do recurso.
- **GET** bem-sucedido: **200 OK** + array JSON (`[]` se não houver contas).

### Exemplos com curl

```bash
curl -s -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Nome do Usuário\",\"cpf\":\"12345678901\"}"

curl -s http://localhost:8080/accounts
```

No PowerShell, ajuste aspas e escape do JSON conforme necessário.

## CORS

CORS liberado para desenvolvimento em **`http://localhost:3000`**, **`http://localhost:5173`** e equivalentes em `127.0.0.1`, para consumo por aplicações front-end locais.

## Banco de dados (H2)

- Persistência **em memória** (dados perdidos ao encerrar o processo).
- **Console H2:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

| Campo | Valor |
|-------|--------|
| **JDBC URL** | `jdbc:h2:mem:testdb` |
| **User** | `sa` |
| **Password** | *(vazio)* |

## Documentação

A documentação interativa da API é gerada automaticamente pelo **Springdoc / Swagger UI**.

| Recurso | URL |
|---------|-----|
| **Swagger UI** (interface interativa) | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| **JSON OpenAPI 3** (spec bruta) | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |

A interface Swagger UI permite explorar os endpoints, visualizar os esquemas de request/response e executar chamadas diretamente pelo browser.

## Testes

```bash
./mvnw test
```

No Windows (cmd/PowerShell): `mvnw.cmd test`.

## Especificação técnica

Detalhes de arquitetura, contratos e testes: [`TECH_SPEC.md`](TECH_SPEC.md).
