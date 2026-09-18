# Elasticsearch Swing GUI

Interface gráfica desktop (Java Swing) para o Elasticsearch, desenvolvida com
base:

- nos requisitos do README do repositório de exemplo
  [`fbgonzaga/elasticsearch_example`](https://github.com/fbgonzaga/elasticsearch_example);
- no material de aula (PDF "Elasticsearch - turma 2026"), que ensina os
  recursos do Elasticsearch usados aqui (queries bool, filtros por range,
  ordenação, highlight, fuzziness, suggest, agregações, paginação, etc.).

Diferente do projeto original (que é uma API REST em Spring Boot consumida
via Postman), aqui a própria aplicação Java conversa **diretamente** com a
API REST do Elasticsearch e apresenta os resultados em uma janela Swing.

**Não usa Docker nem nenhuma biblioteca externa** (nem o cliente oficial
`elasticsearch-java`, nem Jackson, nem Spring): tudo é feito com classes
padrão do JDK (`java.net.http.HttpClient` para as chamadas HTTP e uma
classe utilitária própria, `com.elasticgui.json.Json`, para ler/escrever
JSON). Isso significa que o projeto compila e roda com **apenas o JDK
instalado**, sem precisar baixar nada da internet.

Você só precisa ter um Elasticsearch acessível (local, em nuvem, com ou sem
Docker - o `docker.zip` da disciplina continua funcionando perfeitamente se
você quiser usá-lo, mas não é obrigatório).


## Pré-requisitos

- JDK 17 ou superior instalado (`java -version` / `javac -version`).
- Um Elasticsearch 8.x acessível (por padrão a aplicação já vem configurada
  para `https://localhost:9200`, usuário `elastic`, senha `user123`, exatamente
  como no material de aula).
- O índice de exemplo (`wikipedia`, ou `wikipedia_v2` se você fez o passo de
  reindex com o campo `label`) precisa existir e ter os campos usados em
  aula: `title`, `url`, `content`, `dt_creation`, `reading_time` e (opcional)
  `label`.


## Guia passo a passo completo (do zero até a primeira busca)

Este guia assume que você vai usar o mesmo ambiente da disciplina
(`docker.zip`), mas o processo é o mesmo se você já tiver um Elasticsearch
rodando de outra forma - basta pular direto para o **Passo 3**.

### Passo 1 - Subir o Elasticsearch e o Kibana (Docker)

1. Extraia o `docker.zip` (disponibilizado no Google Classroom) em uma pasta
   à sua escolha.
2. Abra um terminal dentro da pasta extraída (a que contém o `docker-compose.yml`
   e o `.env`).
3. Suba os containers:
   ```bash
   docker compose up -d
   ```
4. Aguarde alguns segundos e confira se os containers subiram corretamente:
   ```bash
   docker compose ps
   ```
   Os serviços de Elasticsearch e Kibana devem aparecer com status `running`/`healthy`.

> Se você já tinha containers antigos de uma aula anterior, limpe tudo antes
> com os comandos ensinados na primeira aula:
> ```bash
> docker stop $(docker ps -aq)
> docker rm $(docker ps -aq)
> docker system prune -a --volumes
> ```

### Passo 2 - Confirmar que o Elasticsearch está no ar e povoar o índice

1. Acesse o Kibana no navegador: [http://0.0.0.0:5601/](http://0.0.0.0:5601/)
   e faça login com usuário `elastic` e senha `user123`.
2. Abra o menu **Dev Tools** e rode um teste rápido para confirmar que o
   cluster responde:
   ```
   GET /_cat/health?v
   ```
3. Se o índice `wikipedia` ainda não existir (primeira vez usando o
   ambiente), crie-o e importe os dados de exemplo (`wiki.json`, também
   disponível no Google Classroom) seguindo o mesmo processo da aula de
   23/03: crie o índice com o mapping mostrado no PDF e depois rode, no
   terminal (na pasta onde está o `wiki.json`):
   ```bash
   curl -H "Content-Type: application/x-ndjson" -XPOST https://localhost:9200/wikipedia/_bulk \
     --data-binary "@wiki.json" --user "elastic:user123" --insecure
   ```
4. Confirme que o índice tem documentos:
   ```
   GET /wikipedia/_count
   ```
   Se já tiver feito isso em uma aula anterior e os dados persistiram (o
   volume do Docker não foi apagado), pode pular este passo.

### Passo 3 - Obter e compilar a interface gráfica (este projeto)

1. Extraia o `elastic-swing-gui.zip` em uma pasta à sua escolha (pode ser em
   qualquer lugar do computador, não precisa ser perto do `docker.zip`).
2. Abra um terminal **dentro da pasta `elastic-swing-gui`** (a que contém o
   `build.sh`/`build.bat`).
3. Compile o projeto:
   - **Windows:**
     ```bat
     build.bat
     ```
   - **Linux / macOS:**
     ```bash
     chmod +x build.sh run.sh
     ./build.sh
     ```
4. Ao final, deve aparecer a mensagem `Build concluído: target/elastic-swing-gui.jar`
   (ou `target\elastic-swing-gui.jar` no Windows). Isso significa que o
   `javac` compilou todas as classes e gerou o `.jar` executável.

### Passo 4 - Executar a interface

- **Windows:** dê duplo clique em `run.bat` ou rode `run.bat` no terminal.
- **Linux / macOS:** rode `./run.sh` no terminal.
- Ou, em qualquer sistema, execute o `.jar` diretamente:
  ```bash
  java -jar target/elastic-swing-gui.jar
  ```

A janela da aplicação deve abrir já com a aba **Busca** selecionada e, na
barra inferior, a mensagem `Conectado a https://localhost:9200 | índice:
wikipedia | usuário: elastic` - ou seja, com os valores padrão do curso já
preenchidos, sem precisar configurar nada.

### Passo 5 - Testar a conexão (opcional, mas recomendado na primeira vez)

1. No menu superior, clique em **Arquivo → Configuração de conexão...**.
2. Confira se os campos estão como o esperado (host `localhost`, porta
   `9200`, protocolo `https`, usuário `elastic`, senha `user123`, índice
   `wikipedia`) e clique em **Testar conexão**.
3. Se aparecer "Conexão bem-sucedida!" com o nome do cluster e a versão do
   Elasticsearch, está tudo certo - clique em **Salvar** (ou **Cancelar**,
   já que os valores já estavam certos) e siga para a busca.
4. Se aparecer uma mensagem de erro, veja a seção [Erros comuns](#erros-comuns)
   mais abaixo.

### Passo 6 - Fazer a primeira busca

1. Na aba **Busca**, digite um termo no campo "Buscar em `content`" (por
   exemplo, `square root`, um dos termos usados em aula) e clique em
   **Buscar** (ou tecle Enter).
2. Os resultados aparecem na tabela à direita; clique em qualquer linha para
   ver, embaixo, o título completo, a URL e um trecho do conteúdo com os
   termos buscados destacados em negrito.
3. Use os campos à esquerda para filtrar por tempo de leitura, data de
   criação ou classificação, e o combo "Ordenar por" para mudar a ordenação;
   depois clique em **Aplicar filtros**.
4. Use a barra inferior para navegar entre páginas, mudar quantos resultados
   aparecem por página, ou ver as estatísticas agregadas do conjunto
   filtrado.
5. Para acompanhar a saúde do cluster, os nós e os índices disponíveis,
   troque para a aba **Cluster** e clique em **Atualizar**.

### Passo 7 - Encerrar

- Feche a janela da aplicação normalmente.
- Se quiser parar o Elasticsearch/Kibana também, volte ao terminal onde
  rodou o `docker compose up -d` e execute:
  ```bash
  docker compose down
  ```
  (isso mantém os dados salvos no volume; para apagar tudo, use `docker
  compose down -v`, como ensinado em aula).


## Configurando a conexão

Ao abrir, a aplicação já vem com os valores padrão do curso (host
`localhost`, porta `9200`, `https`, usuário `elastic`, senha `user123`,
índice `wikipedia`). Para alterar host, porta, protocolo, credenciais ou o
nome do índice, use o menu **Arquivo → Configuração de conexão...**, que
também tem um botão **Testar conexão** (chama `GET /` e mostra o nome do
cluster e a versão do Elasticsearch). As configurações são salvas em
`~/.elastic-swing-gui/config.properties` e reaproveitadas na próxima
execução.

Por padrão a aplicação confia em certificados TLS autoassinados (mesmo
comportamento do `curl --insecure` usado em aula) - isso é apropriado apenas
para ambiente de desenvolvimento, e pode ser desligado na tela de conexão.


## Formas alternativas de compilar/rodar

### Usando Maven (opcional)
Um `pom.xml` também é fornecido para quem preferir importar o projeto numa
IDE (IntelliJ, Eclipse, VS Code) ou compilar com Maven. Como não há nenhuma
dependência externa, `mvn package` funciona mesmo sem repositórios extras
configurados além do Maven Central padrão (usado apenas para os plugins de
build, não para bibliotecas).
```bash
mvn package
java -jar target/elastic-swing-gui.jar
```

### Executando diretamente o .jar já compilado
```bash
java -jar target/elastic-swing-gui.jar
```

### Importando numa IDE
Basta abrir a pasta `elastic-swing-gui` como um projeto Maven (ou como um
projeto Java simples apontando `src/main/java` como source root) - não há
módulos ou dependências para configurar. A classe principal é
`com.elasticgui.Main`.


## O que a aba "Busca" cobre (requisitos do README + extras)

O README do projeto de referência descreve um endpoint `GET /search` com:
- `query` (obrigatório): termo buscado no campo `content`;
- `page` (opcional, padrão 1): paginação com `from = (page - 1) * 10`;
- resposta com `title`, `url` e `abs` (conteúdo limpo, sem tags HTML/caracteres
  especiais).

Tudo isso está implementado na aba **Busca**, mais os recursos extras de
filtro e paginação do Elasticsearch vistos em aula:

| Recurso                                   | Onde na interface                          | Aula de referência no PDF |
|--------------------------------------------|---------------------------------------------|----------------------------|
| Busca por termo no campo `content`         | Campo de texto + botão "Buscar"             | 10/03, 17/03 |
| Paginação (`from`/`size`, total de páginas)| Barra inferior (Anterior/Próxima/Ir para)   | 31/03 |
| Tamanho de página configurável             | Combo "Resultados por página" (10/20/50)    | 31/03 |
| Operador AND/OR                            | Combo "Operador"                             | 23-24/03 |
| Boost de frase exata (`match_phrase`)      | Checkbox "Priorizar frase exata"            | 23-24/03, 30/03 |
| Fuzziness (busca tolerante a erros)        | Combo "Fuzziness"                             | 06/04 |
| Destaque dos termos encontrados (`highlight`)| Checkbox "Destacar termos" + painel de detalhe | 06/04 |
| Filtro por faixa de `reading_time`         | Painel esquerdo "Tempo de leitura"           | 30/03 |
| Filtro por faixa de `dt_creation`          | Painel esquerdo "Data de criação"            | 30/03 |
| Filtro por `label` (rápido/médio/demorado) | Painel esquerdo "Classificação"              | 13/04 (pipeline de reindex)|
| Ordenação (relevância, tempo, data)        | Combo "Ordenar por"                          | 31/03 |
| Sugestão de correção ("você quis dizer?")  | Aparece automaticamente com 0 resultados     | 13/04 |
| Estatísticas do conjunto (`stats` aggregation) | Botão "Ver estatísticas do conjunto"     | 07/04 |
| Ver JSON bruto da resposta (debug)         | Disponível internamente em `SearchResponse` | - |

> Observação sobre `label`: esse campo só existe no índice `wikipedia_v2`
> (criado na aula de 13/04 via `_reindex` + pipeline). Se você estiver usando
> apenas o índice `wikipedia` original, deixe os filtros de classificação
> desmarcados - eles simplesmente não vão casar com nenhum documento.


## O que a aba "Cluster" cobre

Reproduz os primeiros comandos administrativos vistos na aula de 09/03:

- **Saúde do cluster** (`GET /_cat/health?v`) - bolinha colorida (verde /
  amarelo / vermelho) igual ao status do Elasticsearch;
- **Nós do cluster** (`GET /_cat/nodes?v`);
- **Índices** (`GET /_cat/indices?v`).


## Arquitetura do projeto

```
src/main/java/com/elasticgui/
├── Main.java                     ponto de entrada
├── json/Json.java                parser + serializador JSON próprio (sem libs externas)
├── config/AppConfig.java         configuração de conexão (persistida em disco)
├── client/
│   ├── ElasticsearchClient.java  chamadas HTTP/HTTPS à API REST do ES
│   └── EsException.java          erros retornados pelo Elasticsearch
├── model/                        objetos simples (filtros, resultados, estatísticas)
├── service/
│   ├── SearchService.java        monta as queries bool/range/highlight/sort e interpreta a resposta
│   └── ClusterService.java       consultas _cat/health, _cat/nodes, _cat/indices
└── ui/                           telas Swing (MainFrame, SearchPanel, FiltersPanel,
                                   ResultsPanel, ClusterPanel, ConnectionDialog, ...)
```

Todas as chamadas de rede rodam em uma thread de segundo plano
(`SwingWorker`, ver `UiUtils.runAsync`), então a interface não trava
enquanto aguarda a resposta do Elasticsearch.


## Erros comuns

- **"Falha de conexão com https://localhost:9200..."** - verifique se o
  Elasticsearch está no ar (`docker compose up -d`, se estiver usando o
  ambiente da disciplina) e se host/porta/usuário/senha na tela de
  configuração estão corretos.
- **"index_not_found_exception"** - confira o nome do índice na tela de
  configuração (`wikipedia` ou `wikipedia_v2`).
- **0 resultados sem sugestão** - o termo buscado pode não ter nenhuma
  palavra próxima o suficiente no índice invertido para o mecanismo de
  `suggest` propor uma correção; tente um termo mais simples.
