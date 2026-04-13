# Especificação técnica — cora-api

Documento de referência para implementação e manutenção do backend. Escopo atual: **API REST** para contas; cliente web ou mobile pode ser integrado separadamente.

---

## 1. Objetivo e escopo

| Item | Descrição |
|------|-----------|
| **Objetivo** | API REST para **criar** e **listar** contas bancárias, com persistência e CORS para consumo por front-end em desenvolvimento. |
| **Linguagem** | Java **17+** (este documento assume **Java 17** + Maven). |
| **Framework** | Spring Boot **3.x**. |
| **Banco** | **H2 em memória** (padrão) ou outro relacional; contrato H2 abaixo. |
| **Fora de escopo** | PUT/PATCH/DELETE, autenticação, validação e tratamento de erros extensos (validação mínima opcional). |

**Testes automatizados** são parte desejável do repositório (integração da API).

---

## 2. Requisitos funcionais (contrato da API)

### 2.1 Criar conta

| Campo | Valor |
|-------|--------|
| **Método / URL** | `POST /accounts` |
| **Content-Type** | `application/json` |
| **Body (JSON)** | `{ "name": string, "cpf": string }` |
| **Exemplo** | `{ "name": "Nome do Usuário", "cpf": "12345678901" }` |
| **Resposta de sucesso** | **HTTP 201 Created** |
| **Corpo de resposta** | JSON do recurso criado: `{ "id": 1, "name": "...", "cpf": "..." }` |
| **Header opcional** | `Location: /accounts/{id}` |

**Nota:** Unicidade e formato de CPF não são obrigatórios neste escopo; persistir como texto.

### 2.2 Listar contas

| Campo | Valor |
|-------|--------|
| **Método / URL** | `GET /accounts` |
| **Resposta de sucesso** | **HTTP 200 OK** |
| **Corpo** | Array JSON, ex.: `[{ "id": 1, "name": "...", "cpf": "..." }, ...]` ou `[]`. |

---

## 3. Requisitos não funcionais

| Requisito | Especificação |
|-----------|----------------|
| **CORS** | Origens de dev comuns (`http://localhost:5173`, `http://localhost:3000`, etc.) via `WebMvcConfigurer` ou `@CrossOrigin`. |
| **Porta** | **8080** (padrão; documentar no README). |
| **H2 Console** | Opcional: `/h2-console` para inspeção local. |
| **Persistência** | Dados em memória durante a JVM; reinício zera a base. |
| **Container** | Imagem Docker opcional para executar a API sem instalar JDK/Maven no host (ver seção 14). |

### 3.1 H2 (memória)

| Parâmetro | Valor |
|-----------|--------|
| JDBC URL | `jdbc:h2:mem:testdb` |
| User | `sa` |
| Password | *(vazio)* |
| Console (se habilitado) | `http://localhost:8080/h2-console` |

---

## 4. Modelo de dados

### 4.1 Entidade `Account` (JPA)

| Coluna | Tipo | Observação |
|--------|------|------------|
| `id` | `BIGINT` / `Long` | PK gerada |
| `name` | `VARCHAR` | titular |
| `cpf` | `VARCHAR` | texto livre |

**Tabela:** `accounts`.

---

## 5. Arquitetura e pacotes

```
com.cora.api
├── CoraApiApplication.java
├── account
│   ├── Account.java
│   ├── AccountRepository.java
│   ├── AccountService.java
│   ├── AccountController.java
│   └── dto
│       ├── CreateAccountRequest.java
│       └── AccountResponse.java
└── config
    └── WebConfig.java
```

- **Controller:** DTO → service → HTTP 201 / 200.
- **Service:** mapeamento request ↔ entidade ↔ response.
- **DTOs:** separados da entidade JPA.

---

## 6. Dependências Maven (mínimo)

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation` *(opcional)*
- `h2` (runtime)
- `spring-boot-starter-test` (test)
- `springdoc-openapi-starter-webmvc-ui` **2.x** — Swagger UI + OpenAPI 3

Parent: `spring-boot-starter-parent` 3.x; `java.version` **17** (ou 21).

---

## 7. Configuração (`application.yml`)

- Datasource conforme seção 3.1
- `spring.jpa.hibernate.ddl-auto`: `update` (main) ou `create-drop` (profile `test`)
- `spring.h2.console.enabled: true` *(opcional)*
- `server.port: 8080`

**Profile `test`:** H2 em memória com URL estável, ex.: `jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`.

**Container:** nenhuma variável extra obrigatória; a aplicação escuta em `0.0.0.0:8080` dentro do container (padrão Spring Boot), mapeada para o host via `-p 8080:8080` ou `docker compose`.

---

## 8. Plano de implementação (ordem sugerida)

1. Projeto base: `pom.xml`, `CoraApiApplication`, `application.yml`, `.gitignore`.
2. Persistência: `Account`, `AccountRepository`.
3. API: DTOs, `AccountService`, `AccountController` (`POST`, `GET`).
4. CORS: `WebConfig` ou anotação no controller.
5. Testes: seção 9.
6. **Docker:** `Dockerfile` (multi-stage), `.dockerignore`, `docker-compose.yml` (opcional), validar `docker build` e `docker compose up` (seção 14).
7. README: execução local, **execução via Docker**, porta, endpoints, curl, H2, CORS, pré-requisitos.
8. Validação manual: app (local e/ou container), Postman/curl, H2 console opcional.

---

## 9. Estratégia de testes

### 9.1 Integração (API)

- `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvc` (ou `TestRestTemplate`).
- Profile `test` + H2 em memória.

| # | Cenário | Asserções |
|---|---------|-----------|
| T1 | `POST /accounts` válido | **201**; `id`, `name`, `cpf` |
| T2 | `GET /accounts` sem dados | **200**; `[]` |
| T3 | Após um `POST`, `GET` | **200**; um elemento |
| T4 | Dois `POST` + `GET` | **200**; tamanho **2** |

### 9.2 Opcional

- `@DataJpaTest` no repositório.

### 9.3 Comando

```bash
./mvnw test
```

ou `mvn test`.

---

## 10. README — conteúdo sugerido

1. Como executar (`mvn spring-boot:run` ou wrapper).
2. **Docker:** pré-requisito Docker Engine, comandos `docker build`, `docker run` e/ou `docker compose up --build`.
3. Porta (local e container: **8080** no host quando mapeada).
4. Endpoints e exemplos `curl`.
5. H2: console e credenciais de acesso.
6. CORS em dev.
7. Pré-requisitos: JDK e Maven (só para build local sem Docker).
8. **Documentação:** Swagger UI (`/swagger-ui.html`) e JSON OpenAPI (`/v3/api-docs`).

---

## 11. Checklist de verificação

- [ ] Aplicação sobe sem erro (local).
- [ ] **Imagem Docker constrói** (`docker build -t cora-api:local .`).
- [ ] **Container expõe a API** (`docker run -p 8080:8080 …` ou `docker compose up`).
- [ ] `POST /accounts` → **201**
- [ ] `GET /accounts` → **200**
- [ ] CORS configurado para dev local.
- [ ] H2 operacional.
- [ ] Testes passando (`mvn test`).
- [ ] README atualizado (incluindo Docker).
- [ ] **Swagger UI acessível** em `http://localhost:8080/swagger-ui.html`.
- [ ] **JSON OpenAPI** disponível em `http://localhost:8080/v3/api-docs`.

---

## 12. Fora do escopo atual

- CRUD completo (update/delete).
- Autenticação OAuth/JWT.
- Catálogo de erros HTTP customizado.
- Front-end no mesmo repositório (opcional).
- Orquestração multi-serviço além da API única (compose com um serviço basta).

---

## 13. Referência — `curl`

```bash
curl -s -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{"name":"Nome do Usuário","cpf":"12345678901"}'

curl -s http://localhost:8080/accounts
```

---

## 14. Containerização (Docker)

### 14.1 Objetivo

- Empacotar a API em uma **imagem** reproduzível.
- Permitir `docker run` ou **Docker Compose** sem instalar JDK/Maven no ambiente de execução.
- O H2 em memória continua **dentro do processo JVM** do container; ao parar o container, os dados somem (mesmo comportamento do modo local).

### 14.2 Arquivos

| Arquivo | Função |
|---------|--------|
| **`Dockerfile`** | Build multi-stage: estágio 1 compila com Maven (`mvn package`); estágio 2 imagem enxuta JRE 17 e executa o JAR fat. |
| **`.dockerignore`** | Reduz contexto de build (`target/`, `.git`, IDE, documentação). |
| **`docker-compose.yml`** | Serviço `api`, build local, mapeamento `8080:8080`, variáveis JVM opcionais. |

### 14.3 Build da imagem

```bash
docker build -t cora-api:local .
```

### 14.4 Execução

```bash
docker run --rm -p 8080:8080 cora-api:local
```

Ou, na raiz do repositório:

```bash
docker compose up --build
```

A API fica em **`http://localhost:8080`** (host). Console H2: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:testdb`, user `sa`, senha vazia).

### 14.5 Detalhes técnicos

- **Estágio de build:** imagem oficial Maven + Eclipse Temurin 17 (Alpine), `mvn -DskipTests package` para acelerar a camada de build em CI.
- **Estágio de runtime:** `eclipse-temurin:17-jre-alpine`, um único JAR copiado de `target/*.jar`.
- **Porta:** `EXPOSE 8080`; publicar com `-p 8080:8080`.
- **Testes:** rodam no host com Maven; não é obrigatório rodar `mvn test` dentro do Dockerfile (mantém build de imagem mais rápido). Opcional: stage de test ou pipeline CI separado.

---

Implementar conforme seções **4–7**, **14–15** e validar com **9** e **11**.

---

## 15. OpenAPI / Swagger UI

### 15.1 Objetivo

Gerar documentação interativa automática da API a partir de anotações no código-fonte, sem manutenção manual de arquivos YAML/JSON.

### 15.2 Dependência

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.8</version>
</dependency>
```

### 15.3 URLs

| Recurso | URL |
|---------|-----|
| **Swagger UI** (interface interativa) | `http://localhost:8080/swagger-ui.html` |
| **JSON OpenAPI 3** (spec bruta) | `http://localhost:8080/v3/api-docs` |

### 15.4 Configuração (`application.yml`)

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
```

### 15.5 Anotações por camada

| Camada | Anotação | Uso |
|--------|----------|-----|
| Config | `@Bean OpenAPI` | Título, descrição e versão da API |
| Controller (classe) | `@Tag` | Agrupa endpoints por domínio |
| Controller (método) | `@Operation` | Resumo e descrição do endpoint |
| Controller (método) | `@ApiResponse` | Códigos HTTP e corpo de resposta documentados |
| DTO (classe) | `@Schema` | Descrição do objeto |
| DTO (campo) | `@Schema` | Descrição e exemplo de cada campo |

### 15.6 Arquivos envolvidos

| Arquivo | Ação |
|---------|------|
| `config/OpenApiConfig.java` | Bean `OpenAPI` com metadados (`Info`) |
| `account/AccountController.java` | `@Tag`, `@Operation`, `@ApiResponse` nos endpoints |
| `account/dto/CreateAccountRequest.java` | `@Schema` na classe e campos |
| `account/dto/AccountResponse.java` | `@Schema` na classe e campos |
