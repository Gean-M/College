# Elasticsearch - Turma 2026

Este repositório reúne todo o material da disciplina de Elasticsearch
(turma 2026) e o projeto final desenvolvido a partir dele: uma interface
gráfica desktop (Java Swing) para o Elasticsearch.

Ele foi mantido em um **repositório separado** (em vez de misturado com
outros projetos pessoais) para facilitar a organização e a gerência dos
arquivos da disciplina - ambiente Docker, dataset de exemplo, anotações de
aula e código-fonte ficam todos juntos, versionados e fáceis de encontrar.

> 📌 **Se você só quer compilar e rodar o programa**, vá direto para
> [`projeto_elastic`](./Projeto_Elastic) - lá está o
> passo a passo completo, desde subir o Elasticsearch até fazer a primeira
> busca na interface.


## Estrutura do repositório

```
.
├── docker/
│   ├── docker-compose.yml       Sobe Elasticsearch + Kibana (mesma versão usada em aula: 8.17.2)
│   └── .env                     Credenciais e portas (elastic/user123, 9200/5601)
├── wiki.json                    Dataset de exemplo (artigos da Wikipedia) usado para popular o índice "wikipedia"
├── Elasticsearch_-_turma_2026.pdf   Anotações de todas as aulas (comandos, queries e explicações)
└── projeto_elastic/             Projeto final: interface gráfica em Java (Swing) para o Elasticsearch
    ├── README.md                 👉 Instruções completas de instalação e execução
    ├── build.sh / build.bat      Scripts para compilar (sem Maven, sem dependências externas)
    ├── run.sh / run.bat          Scripts para executar a aplicação já compilada
    ├── pom.xml                   Build alternativo via Maven (opcional)
    └── src/                      Código-fonte Java
```


## O que tem em cada parte

### `docker/`
Ambiente usado durante toda a disciplina para rodar o Elasticsearch e o
Kibana localmente via Docker Compose (extraído do `docker.zip` distribuído
em aula). Sobe com:
```bash
cd docker
docker compose up -d
```
Depois disso, o Kibana fica disponível em `http://0.0.0.0:5601/` e o
Elasticsearch em `https://localhost:9200/` (usuário `elastic`, senha
`user123`).

### `wiki.json`
Conjunto de documentos de exemplo (artigos estilo Wikipedia, com os campos
`title`, `url`, `content`, `dt_creation` e `reading_time`) usado para
popular o índice `wikipedia`, criado e explorado ao longo das aulas
(queries, filtros, agregações, highlight, etc.).

### `Elasticsearch_-_turma_2026.pdf`
Registro de todas as aulas da disciplina: comandos do Dev Tools do Kibana,
explicações sobre mappings, analyzers, queries booleanas, agregações,
paginação, highlight, fuzziness, suggest, reindex e mais. É a referência
usada como base para o projeto final.

### `projeto_elastic/`
O projeto final da disciplina: uma interface gráfica desktop (Java Swing)
que conversa diretamente com a API REST do Elasticsearch, cobrindo tanto os
requisitos básicos de busca/paginação quanto os recursos avançados vistos
em aula (filtros, ordenação, fuzziness, highlight, sugestão de correção,
estatísticas, monitoramento de cluster). Não depende de bibliotecas
externas nem de Docker para compilar/rodar - só do JDK.

Todas as instruções detalhadas (pré-requisitos, como subir o
Elasticsearch, como compilar, como executar, como usar cada função da
interface e solução de problemas comuns) estão no
[README do próprio projeto](./projeto_elastic).


## Início rápido

```bash
# 1. Subir o Elasticsearch e o Kibana
cd docker
docker compose up -d

# 2. Compilar e rodar a interface gráfica
cd ../projeto_elastic
./build.sh   # ou build.bat no Windows
./run.sh     # ou run.bat no Windows
```

Para o passo a passo completo (incluindo como popular o índice com o
`wiki.json` e como configurar a conexão pela interface), consulte
[`projeto_elastic/README.md`](./projeto_elastic/README.md).


## Contexto acadêmico

Repositório organizado para a disciplina de Elasticsearch (turma 2026),
reunindo o material de aula e o projeto final desenvolvido com base nele.
