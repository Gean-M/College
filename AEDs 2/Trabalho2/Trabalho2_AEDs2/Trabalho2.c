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
#define quantum 6

// Structs (processo, acesso ao processo e fila)
typedef struct processo_ {
    int numeroDoProcesso;
    int tamanhoProcesso;
} Processo;

typedef struct node_ {
    Processo processo;
    struct node_ * prox;
} Node;

typedef struct fila_ {
    Node * proximo;
    Node * anterior;
} Fila;

// Funções de manipulação da fila desordenada (inicia, verificação de nulidade, adicionar e remover)
void iniciaFila (Fila * fila) {
    fila->proximo = NULL;
    fila->anterior = NULL;
}

int filaVazia(Fila * fila) {
    return (fila->proximo == NULL);
}

void adicionaNaFila (Fila * fila, Processo novoProcesso) {
    Node * novoNo = (Node*)malloc(sizeof(Node));
    novoNo->processo = novoProcesso;
    novoNo->prox = NULL;

    if (filaVazia(fila)) {
        fila->proximo = novoNo;
        fila->anterior = novoNo;
    } else {
        fila->anterior->prox = novoNo;
        fila->anterior = novoNo;
    }
}

Processo removeDaFila (Fila * fila) {
    if (filaVazia(fila)) {
        Processo processoVazio = {-1, 0};
        return processoVazio;
    }

    Node * temp = fila->proximo;
    Processo processo = temp->processo;

    fila->proximo = fila->proximo->prox;
    free(temp);

    if (fila->proximo == NULL) {
        fila->anterior = NULL;
    }
    
    return processo;
}

// Funções de remoção e adição na fila, de forma ordenada.
Processo removeDaFilaOrdenada (Fila * fila) {
    if (filaVazia(fila)) {
        Processo processoVazio = {-1, 0};
        return processoVazio;
    }

    Node * temp = fila->anterior;

    if (fila->anterior == fila->proximo) {
        Processo aux = fila->anterior->processo;
        fila->anterior = NULL;
        fila->proximo = NULL;
        return aux;
    } else {
        Processo aux = fila->anterior->processo;
        fila->anterior = fila->anterior->prox;
        return aux;
    }

    free(temp);
}

void adicionarOrdenado (Fila * fila, Processo processo) {
    Node * novoNo = (Node*)malloc(sizeof(Node));
    novoNo->processo = processo;
    novoNo->prox = NULL;

    if (filaVazia(fila)) {
        fila->proximo = novoNo;
        fila->anterior = novoNo;
    } else {
        if (novoNo->processo.tamanhoProcesso < fila->anterior->processo.tamanhoProcesso) {
            novoNo->prox = fila->anterior;
            fila->anterior = novoNo;
        } else {
            Node * anterior = NULL;
            Node * atual = fila->anterior;
            while (atual != NULL && novoNo->processo.tamanhoProcesso > atual->processo.tamanhoProcesso) {
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

// Função para imprimir a fila, necessária para testes e melhor visualização do código.
void imprimirFila(Fila* fila) {
    if (filaVazia(fila)) {
        printf("A fila está vazia.\n");
        return;
    }

    Node * atual = fila->proximo;
    while (atual != NULL) {
        printf("(%d): %d -> ", atual->processo.numeroDoProcesso, atual->processo.tamanhoProcesso);
        atual = atual->prox;
    }
    printf("NULL\n");
}

// Função para imprimir a fila ordenada.
void imprimirFilaOrdenada(Fila* fila) {
    if (filaVazia(fila)) {
        printf("A fila está vazia.\n");
        return;
    }

    Node * atual = fila->anterior;
    while (atual != NULL) {
        printf("(%d): %d -> ", atual->processo.numeroDoProcesso, atual->processo.tamanhoProcesso);
        atual = atual->prox;
    }
    printf("NULL\n");
}

// Função principal.
void main () {

    srand(time(NULL));

    Fila fila;
    iniciaFila (&fila);
    int temporizador = 0;
    int numeroDoProcesso = 2;

    int escolha;
    printf("+---------------------------------------+\n");
    printf("|       ESCOLHA A OPCAO DESEJADA        |\n");
    printf("+---------------------------------------+\n");
    printf("| 1. FCFS (First Come, First Served)    |\n");
    printf("| 2. SJF (Shortest Job First)           |\n");
    printf("| 3. Round-Robin                        |\n");
    printf("+---------------------------------------+\n");
    printf("| Digite sua escolha: ");
    scanf("%d", &escolha);

    if (fila.proximo == NULL && rand() % 100 + 1 < 30) { // Chance do primeiro processo ser criado.
        Processo novoProcesso = {1, rand() % 20 + 1}; // Recebe 1 (por ser o primeriro) e um tamanho entre 1 e 20.
        adicionaNaFila(&fila, novoProcesso); // É adicionado a fila.
    }

    while (1) { // Início do loop infinito.
        switch (escolha) {
            case 1: //FCFS.
                while (fila.proximo != NULL) { // Confere se a lista está vazia: caso em que nenhum número foi gerado nas 30% chance iniciais.
                    Processo processoAtual = removeDaFila(&fila); // processoAtual recebe o elemento e ele é removido da fila.
                    int tempoRestante = processoAtual.tamanhoProcesso; // tempoRestante é usado como variável auxiliar para manipular a quantidade de vezes que o código será executado, sem alterar tamanhoProcesso.
                    
                    while (tempoRestante > 0) { // Mantém o processo em execução até ele ser terminado.
                        if (rand() % 100 + 1 < 30) { // 30% de chance de gerar um novo processo.
                            Processo novoProcesso = {numeroDoProcesso, rand() % 20 + 1}; // Processo recebe um número de identificação referente ao seu momento de criação e seu tamanho (de 1 a 20).
                            adicionaNaFila(&fila, novoProcesso); // Adiciona o processo criado na fila.
                            numeroDoProcesso++; // Incrementa o numeroDoProcesso, para que o próximo processo receba a identificação correta.
                            //imprimirFila(&fila); // Impressão da fila a cada adição, use para visualizar melhor o funcionamento do código.
                        }
                        tempoRestante--;
                        temporizador++;
                        printf("Tempo gasto ate agora: %d | Processo \"%d\" de tamanho: %d em execucao\n", temporizador, processoAtual.numeroDoProcesso, processoAtual.tamanhoProcesso);
                        //sleep(1); // Cria um atraso entre cada execução do loop, use para visualizar melhor o funcionamento do código.
                    }
                }
            break;

            case 2: //SJF
                while (fila.proximo != NULL) {
                    Processo processoAtual = removeDaFilaOrdenada(&fila);
                    int tempoRestante = processoAtual.tamanhoProcesso;

                    while (tempoRestante > 0) { // A única diferença é a adição ordenada e a impressão, que foi modificada para imprimir corretamente.
                        if (rand() % 100 + 1 < 30) {
                            Processo novoProcesso = {numeroDoProcesso, rand() % 20 + 1};
                            adicionarOrdenado(&fila, novoProcesso);
                            numeroDoProcesso++;
                            //imprimirFilaOrdenada(&fila); // Impressão da fila ordenada a cada adição.
                        }
                        tempoRestante--;
                        temporizador++;
                        printf("Tempo gasto ate agora: %d | Processo \"%d\" de tamanho: %d em execucao\n", temporizador, processoAtual.numeroDoProcesso, processoAtual.tamanhoProcesso);
                        //sleep(1); // Se necessário, use para visualizar melhor o funcionamento do código.
                    }

                }
            break;

            case 3:
                while (fila.proximo != NULL) {
                    Processo processoAtual = removeDaFila(&fila);
                    int tempoRestante = processoAtual.tamanhoProcesso, contaQuantum = 0;

                    while (tempoRestante > 0 && contaQuantum < quantum) { // Diferente dos métodos anteriores, o processo só é executado pelo tempo determinado por quantum.
                        if (rand() % 100 + 1 < 30) {
                            Processo novoProcesso = {numeroDoProcesso, rand() % 20 + 1};
                            adicionaNaFila(&fila, novoProcesso);
                            numeroDoProcesso++;
                            imprimirFila(&fila);
                        }
                        tempoRestante--;
                        temporizador++;
                        contaQuantum++;
                        printf("Tempo gasto ate agora: %d | Processo \"%d\" de tamanho: %d em execucao\n", temporizador, processoAtual.numeroDoProcesso, processoAtual.tamanhoProcesso);
                        sleep(1); // Se necessário, use para visualizar melhor o funcionamento do código.
                    }
                    if(processoAtual.tamanhoProcesso > quantum) { // Garante que um processo que ainda não terminou de ser executado retorne para a lista com seu tamanho atualizado.
                        processoAtual.tamanhoProcesso = processoAtual.tamanhoProcesso - quantum;
                        adicionaNaFila(&fila, processoAtual);
                        imprimirFila(&fila); // Imprime a fila após o processo reentrar nela.
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