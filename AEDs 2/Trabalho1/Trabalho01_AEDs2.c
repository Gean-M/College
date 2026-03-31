/*+--------------------------------------------------------------+
  | UNIFAL – Universidade Federal de Alfenas.                    |
  | BACHARELADO EM CIENCIA DA COMPUTACAO.                        |
  | Trabalho..: Validacao de arquivos XML                        |
  | Disciplina: Algoritmos e Estrutura de Dados II – Pratica     |
  | Professor.: Fellipe Rey                                      |
  | Aluno(s)..: Pedro Henrique de Almeida - 2022.1.08.045        |
  |             Jorran Luka Andrade dos Santos - 2022.2.08.001   |
  |             João Pedro Bueno Lellis - 2022.1.08.046          |
  |             Gean Marques - 2019.1.08.006                     |
  | Data......: 15/11/2023                                       |
  +--------------------------------------------------------------+*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define TAM 10

//Definição da estrutura da pilha
typedef struct no {
  char tag[TAM];
  struct no *ant;
} no;

// Definição do topo da pilha
typedef struct topo {
  struct no *topo;
} topo;

// Função para criar um novo nó
no *novo_no(char tag[]) {
  no *novo = (no *)malloc(sizeof(no));
  strncpy (novo->tag, tag, TAM-1);
  novo->ant = NULL;
  return novo;
}

// Função para empilhar uma nova tag
void empilhar(topo *pilha, char tag[]) {
  no *novo = novo_no(tag);
  novo->ant = pilha->topo;
  pilha->topo = novo;
}

// Função para desempilhar uma tag
char *desempilhar(topo *pilha) {
  no *temp = pilha->topo;
  char *tag = strdup (temp->tag);
  pilha->topo = temp->ant;
  free(temp);
  return tag;
}

int main() {
  // Nome do arquivo XML a ser validado
  const char *ArquivoXML = "ArquivoXML.txt";

  // Abertura do arquivo para leitura
  FILE *arquivo = fopen(ArquivoXML, "r");
  if (arquivo == NULL) { // Confirmação da validade do arquivo
    fprintf(stderr, "Erro ao abrir o arquivo %s\n", ArquivoXML);
    return 1;
  }

  // Inicialização da pilha
  topo pilha;
  pilha.topo = NULL;

  char x; // Caractere atual
  int contaLinha = 1; // Contador de linhas (para verificação da linha em que o erro surgiu)
  int contTag; // Contador de caracteres dentro do vetor tag
  while ((x = fgetc(arquivo)) != EOF) { // While para percorrer o arquivo inteiro
    if (x == '<') { // Verificação de início de tag
      x = fgetc(arquivo);
      if (x == '/') { // Verificação de tag de fechamento ou abertura: se encontrou '/', é de fechamento
        char tagFechamento[TAM];
        contTag = 0;
        while ((x = fgetc(arquivo)) != EOF && x != '>' && contTag < TAM-1) { // While para preencher o vetor de char (tagFechamento) com os char da tag (para futura comparação)
          tagFechamento[contTag] = x;
          contTag++;
        }
        tagFechamento[contTag] = '\0';
        char *tagAbertura = desempilhar(&pilha);
        tagAbertura[contTag] = '\0';

        // Verificação se as tags correspondem;
        if (strncmp(tagAbertura, tagFechamento, contTag) != 0) {
          fprintf(stderr, "ERRO: Tags não correspondem; erro na tag da linha: %d\n", contaLinha);
          fclose(arquivo);
          return 1;
        }

        free(tagAbertura);

      } else { // Se não encontrou '/', é uma tag de abertura
        char tagAbertura[TAM];
        tagAbertura[0] = x;
        contTag = 1;
        
        // Verifica se são caracteres válidos para uma tag
        while ((x = fgetc(arquivo)) != EOF && x != '>' && contTag < TAM - 1) {
          tagAbertura[contTag] = x;
          contTag++;
          if (x == '\n' || x ==  '<' || x == ' ' || x == '\t') {
            fprintf(stderr, "ERRO: Tag não foi fechada corretamente; erro na tag da linha: %d\n", contaLinha);
            fclose(arquivo);
            return 1;
          }
        }
        tagAbertura[contTag] = '\0';  // Garante que a string é terminada corretamente

        // Verificase a tag foi adicionada duas vezes
        if (pilha.topo != NULL && strcmp (pilha.topo->tag, tagAbertura) == 0) {
          fprintf(stderr, "ERRO: Tag foi adicionada duas vezes; erro na tag da linha: %d\n", contaLinha);
          fclose(arquivo);
          return 1;
        }

        empilhar(&pilha, tagAbertura);
      }
    }
    if (x == '\n') {
      contaLinha++; // Incrementa a contagem de linhas
    }
  }

  fclose(arquivo);

  // Verificação final do estado da pilha
  if (pilha.topo == NULL) {
    printf("Arquivo XML correto\n");
  } else {
    fprintf(stderr, "ERRO: Tags não fechadas corretamente. Última tag aberta: %s\n", pilha.topo->tag);
  }

  return 0;
}