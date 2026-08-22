# Golden Raspberry Awards API

API RESTful para consultar a lista de indicados e vencedores da categoria Pior Filme do Golden Raspberry Awards, com foco no cálculo do produtor com maior e menor intervalo entre dois prêmios consecutivos.

## Stack

- Java 21
- Spring Boot 4.1.1 (Web MVC + Data JPA)
- H2 (banco em memória, sem instalação externa)
- Maven (via wrapper mvnw)

## Como rodar a aplicação

Nenhuma instalação externa é necessária. O banco H2 sobe em memória e os dados do CSV são carregados automaticamente assim que a aplicação inicia.

```bash
./mvnw spring-boot:run
```

A aplicação sobe por padrão em `http://localhost:8080`.

O carregamento dos dados acontece a partir de `src/main/resources/movielist.csv` na inicialização (via `MovieDataLoader`). No console você deve ver a mensagem:

```
Database successfully initialized for Golden Raspberry Awards API
```

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/v1/movie` | Lista todos os filmes carregados |
| GET | `/api/v1/movie/producer-intervals` | Retorna o produtor com menor e maior intervalo entre prêmios consecutivos |

Exemplo de resposta de `/api/v1/movie/producer-intervals`:

```json
{
  "min": [
    {
      "producer": "Joel Silver",
      "interval": 1,
      "previousWin": 1990,
      "followingWin": 1991
    }
  ],
  "max": [
    {
      "producer": "Matthew Vaughn",
      "interval": 13,
      "previousWin": 2002,
      "followingWin": 2015
    }
  ]
}
```

Também é possível consultar os dados diretamente pelo console do H2 em `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:movies`, usuário `sa`, sem senha).

## Como rodar os testes

O projeto contém dois tipos de teste:

- Um teste de contexto (`MoviesApplicationTests`), executado pelo Surefire em `mvn test`.
- Um teste de integração (`MovieControllerIT`), executado pelo Failsafe, que sobe o contexto Spring completo e valida a resposta real do endpoint `/producer-intervals` contra o dataset padrão.

Para rodar tudo, incluindo o teste de integração:

```bash
./mvnw verify
```

## Trocando o dataset

Para testar com outro conjunto de dados, basta substituir o conteúdo de `src/main/resources/movielist.csv`, mantendo o mesmo formato (`year;title;studios;producers;winner`, separado por ponto e vírgula), e reiniciar a aplicação.