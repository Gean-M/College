/*+--------------------------------------------------------------+
  | UNIFAL – Universidade Federal de Alfenas.                    |
  | BACHARELADO EM CIENCIA DA COMPUTACAO.                        |
  | Trabalho..: Métodos de Escalonamento                         |
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
#include <time.h>
#include <unistd.h>

typedef struct processo_ {
    int numeroDoProcesso;
    int tamanhoProcesso;
    struct processo_ * prox;
} Processo;

// typedef struct node_ {
//     Processo processo;
//     struct node_ * prox;
// } Node;

typedef struct fila_ {
    Processo * proximo;
    Processo * anterior;
} Fila;

void iniciaFila (Fila * fila) {
    fila->proximo = NULL;
    fila->anterior = NULL;
}

void adicionaNaFila (Fila * fila, Processo * novoProcesso) {
    Processo * novoNo = (Processo*)malloc(sizeof(Processo));
    novoNo->numeroDoProcesso = novoProcesso->numeroDoProcesso;
    novoNo->tamanhoProcesso = novoProcesso->tamanhoProcesso;
    novoNo->prox = NULL;

    if (fila->anterior == NULL) {
        fila->proximo = novoNo;
        fila->anterior = novoNo;
    } else {
        fila->anterior->prox = novoNo;
        fila->anterior = novoNo;
    }
}

int filaVazia(Fila * fila) {
    return (fila->proximo == NULL);
}

Processo * removeDaFila (Fila * fila) {
    if (filaVazia(fila)) {
        Processo processoVazio = {-1, 0, NULL};
        return * processoVazio;
    }

    Processo * temp = fila->anterior;
    fila->anterior = temp->prox;
    temp->prox = NULL;
    
    if (fila->proximo == NULL) {
        fila->anterior = NULL;
    }
    return temp;
    free(temp);
    
}

Processo removeDaFilaOrdenada (Fila * fila) {
    if (filaVazia(fila)) {
        Processo processoVazio = {-1, 0, NULL};
        return processoVazio;
    }

    Processo * temp = fila->anterior;

    if (fila->anterior == fila->proximo) {
        Processo temp = fila->anterior;
        fila->anterior = NULL;
        fila->proximo = NULL;
        return temp;
    } else {
        Processo temp = fila->anterior->prox;
        fila->anterior = fila->anterior->prox;
        return temp;
    }

    free(temp);
}

void adicionarOrdenado (Fila * fila, Processo processo) {
    Processo * novoNo = (Processo*)malloc(sizeof(Processo));
    novoNo->numeroDoProcesso = processo.numeroDoProcesso;
    novoNo->tamanhoProcesso = processo.tamanhoProcesso
    novoNo->prox = NULL;

    if (filaVazia(fila)) {
        fila->proximo = novoNo;
        fila->anterior = novoNo;
    } else {
        if (novoNo->tamanhoProcesso < fila->anterior->tamanhoProcesso) {
            novoNo->prox = fila->anterior;
            fila->anterior = novoNo;
        } else {
            Processo * anterior = NULL;
            Processo * atual = fila->anterior;
            while (atual != NULL && novoNo->tamanhoProcesso > atual->tamanhoProcesso) {
                anterior = atual;
                atual = atual->prox;
            }
            
            if (anterior != NULL) {
                anterior->prox = novoNo;
            } else {
                fila->anterior = novoNo;
            }

            novoNo->prox = atual;
            if (atual == NULL) {
                fila->proximo = novoNo;
            }
        }
    }
}

// Função para teste
void imprimirFila(Fila* fila) {
    if (filaVazia(fila)) {
        printf("A fila está vazia.\n");
        return;
    }

    Processo * atual = fila->anterior;
    while (atual != NULL) {
        printf("(%d): %d -> ", atual->numeroDoProcesso, atual->tamanhoProcesso);
        atual = atual->prox;
    }
    printf("NULL\n");
}

void main () {

    srand(time(NULL));

    Fila fila;
    iniciaFila (&fila);
    int temporizador = 0;
    int numeroDoProcesso = 2;

    int escolha;
    printf("+---------------------------------------+\n");
    printf("| 1. FCFS (First Come, First Served)    |\n");
    printf("| 2. SJF (Shortest Job First)           |\n");
    printf("| 3. Round-Robin                        |\n");
    printf("+---------------------------------------+\n");
    printf("| Digite sua escolha: ");
    scanf("%d", &escolha);

    if (fila.proximo == NULL && rand() % 100 + 1 < 90) {
        Processo novoProcesso = {1, rand() % 20 + 1, NULL};
        adicionaNaFila(&fila, novoProcesso);
    }

    while (1) {
        switch (escolha) {
            case 1: //FCFS
                while (fila.proximo != NULL) {
                    Processo processoAtual = removeDaFila(&fila);
                    int tempoRestante = processoAtual.tamanhoProcesso;
                    
                    while (tempoRestante > 0) {
                        if (rand() % 100 + 1 < 30) {
                            Processo novoProcesso = {numeroDoProcesso, rand() % 20 + 1};
                            adicionaNaFila(&fila, novoProcesso);
                            numeroDoProcesso++;
                        }
                        tempoRestante--;
                        temporizador++;
                        printf("Tempo gasto ate agora: %d | Processo \"%d\" de tamanho: %d em execucao\n", temporizador, processoAtual.numeroDoProcesso, processoAtual.tamanhoProcesso);
                        sleep(1); //Atraso p/ visualização
                    }
                }
            break;

            case 2: //SJF
                while (fila.proximo != NULL) {
                    Processo processoAtual = removeDaFilaOrdenada(&fila);
                    int tempoRestante = processoAtual.tamanhoProcesso;

                    while (tempoRestante > 0) {
                        if (rand() % 100 + 1 < 30) {
                            Processo novoProcesso = {numeroDoProcesso, rand() % 20 + 1};
                            adicionarOrdenado(&fila, novoProcesso);
                            numeroDoProcesso++;
                        }
                        tempoRestante--;
                        temporizador++;
                        printf("Tempo gasto ate agora: %d | Processo \"%d\" de tamanho: %d em execucao\n", temporizador, processoAtual.numeroDoProcesso, processoAtual.tamanhoProcesso);
                        sleep(1);
                    }

                }
            break;

            case 3:
                while (fila.proximo != NULL) {
                    Processo processoAtual = removeDaFila(&fila);
                    int quantum, tempoRestante = processoAtual.tamanhoProcesso;

                    while (tempoRestante > 0) {
                        quantum = 6;
                        if (rand() % 100 + 1 < 30) {
                            Processo novoProcesso = {numeroDoProcesso, rand() % 20 + 1};
                            adicionaNaFila(&fila, novoProcesso);
                            numeroDoProcesso++;
                        }

                    }
                }
            break;

            default:
                printf ("Valor invalido!\n");
            break;
        }

        return;
    }
}