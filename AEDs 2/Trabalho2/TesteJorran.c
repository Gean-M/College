#include <stdio.h>
#include <stdlib.h>
#include <time.h>

typedef struct processo_ {
  int tempoGasto;
  int tamanhoProcesso;
} Processo;

typedef struct node_ {
  Processo processo;
  struct node_ *prox;
} Node;

typedef struct fila_ {
  Node *proximo;
  Node *anterior;
} Fila;

void iniciaFila(Fila *fila) {
  fila->proximo = NULL;
  fila->anterior = NULL;
}

void adicionaNaFila(Fila *fila, Processo novoProcesso) {
  Node *novoNo = (Node *)malloc(sizeof(Node));
  novoNo->processo = novoProcesso;
  novoNo->prox = NULL;

  if (fila->anterior == NULL) {
    fila->proximo = novoNo;
    fila->anterior = novoNo;
  } else {
    fila->anterior->prox = novoNo;
    fila->anterior = novoNo;
  }
}

Processo removeDaFila(Fila *fila) {
  if (fila->proximo == NULL) {
    Processo processoVazio = {-1, 0};
    return processoVazio;
  }

  Node *temp = fila->proximo;
  Processo processo = temp->processo;

  fila->proximo = fila->proximo->prox;
  free(temp);

  if (fila->proximo == NULL) {
    fila->anterior = NULL;
  }

  return processo;
}

void main() {

  srand(time(NULL));

  Fila fila;
  iniciaFila(&fila);
  int temporizador = 0;

  int escolha;
  printf("Escolha o metodo de escalonamento:\n");
  printf("1. FCFS (First Come, First Served)\n");
  printf("2. SJF (Shortest Job First)\n");
  printf("3. Round-Robin\n");
  scanf("%d", &escolha);

  int contadorProcessos = 0;

  while (1) {
    if (rand() % 100 < 30 && rand() % 2 == 0) {
      Processo novoProcesso = {temporizador, rand() % 20 + 1};
      adicionaNaFila(&fila, novoProcesso);
    }

    switch (escolha) {
    case 1: // FCFS
      while (fila.proximo != NULL) {
        Processo processoAtual = removeDaFila(&fila);
        temporizador += processoAtual.tamanhoProcesso;
        int tempoRestante = processoAtual.tamanhoProcesso;
        int numeroDoProcesso = ++contadorProcessos;

        while (tempoRestante > 0) {
          if (rand() % 100 < 30) {
            Processo novoProcesso = {0, rand() % 20 + 1};
            adicionaNaFila(&fila, novoProcesso);
          }
          tempoRestante--;

          printf("Tempo gasto até agora: %d | Processo de tamanho: %d em "
                 "execução\n",
                 temporizador, processoAtual.tamanhoProcesso - tempoRestante);
          sleep(1);
        }
      }
      break;

    case 2: // SJF
      printf("Segundo caso\n");
      break;

    case 3:
      printf("Terceiro caso\n");
      break;

    default:
      printf("Valor invalido!\n");
    }
  }
}