#include <stdio.h>
#include <stdlib.h>
#define FALSE 0
#define TRUE 1

typedef struct no {
    int info;
    struct no *proximo;
    struct no *anterior;
} Node;

typedef struct {
    struct no *comeco;
    struct no *fim;
} Lista;

//Iniciar lista:
void iniciaLista (Lista *lista) {
    lista->comeco = NULL;
    lista->fim = NULL;
}

void insereOrd (Lista *lista, int valor) {
    Node *aux = lista->comeco;
    Node *novoNo = (Node*) malloc(sizeof(Node));
    novoNo->info = valor;
    if (aux == NULL) {
        lista->comeco = novoNo;
        lista->fim = novoNo;
        novoNo->anterior = NULL;
        novoNo->proximo = NULL;
    } else if (aux->info > valor) {
        lista->comeco = novoNo;
        novoNo->proximo = aux;
        novoNo->anterior = NULL;
    } else {
        while (aux->proximo != NULL && valor > aux->proximo->info) {
            aux = aux->proximo;
        }
        novoNo->anterior = aux;
        novoNo->proximo = aux->proximo;
        if (aux->proximo == NULL) {
            lista->fim = novoNo;
            aux->proximo = novoNo;
        } else {
            aux->proximo->anterior = novoNo;
            aux->proximo = novoNo;
        }
    }
}

int remover (Lista *lista, int valor) {
    if (lista->comeco == NULL) {
        printf("\nLista vazia, impossivel remover!\n");
        return FALSE;
    }
    Node *aux = lista->comeco;
    if (aux->info == valor) {
        lista->comeco = aux->proximo;
        aux->proximo->anterior = NULL;
        return TRUE;
    }
    while (aux != NULL) {
        if (aux->info == valor) {
            if (aux->proximo == NULL) {
                lista->fim = aux->anterior;
                aux->anterior->proximo = aux->proximo;
            } else {
            aux->anterior->proximo = aux->proximo;
            aux->proximo->anterior = aux->anterior;
            }
            return TRUE;
        }
        aux = aux->proximo;
    }
    return FALSE;
}

void imprimeLista(Lista lista) {
    Node *aux = lista.comeco;
    if (aux == NULL) {
        printf("\nNao ha elementos na lista.\n");
    } else {
        printf("\nOs elementos da lista sao: \n");
        while (aux != NULL) {
            printf(" %d\t", aux->info);
            aux = aux->proximo;
        }
    }
}

void imprimeListaInversa(Lista lista) {
    Node *aux = lista.fim;
    if (aux == NULL) {
        printf("\nNao ha elementos na lista.\n");
    } else {
        printf("\nOs elementos da lista sao (inversamente): \n");
        while (aux != NULL) {
            printf(" %d\t", aux->info);
            aux = aux->anterior;
        }
    }
}

int main () {
    Lista lista;
    iniciaLista(&lista);
    imprimeLista(lista);
    remover(&lista, -1);
    insereOrd(&lista, 5);
    insereOrd(&lista, 6);
    insereOrd(&lista, 3);
    insereOrd(&lista, 4);
    insereOrd(&lista, 8);
    insereOrd(&lista, 2);
    insereOrd(&lista, 1);
    insereOrd(&lista, 7);
    insereOrd(&lista, -1);
    imprimeLista(lista);
    remover(&lista, -1);
    remover(&lista, 7);
    remover(&lista, 5);
    remover(&lista, 8);
    imprimeLista(lista);
    int elemento = remover(&lista, 99);
    printf("\nValor foi removido? %d", elemento);
    printf("\n");
    imprimeListaInversa(lista);
}