#include <stdio.h>
#include <stdlib.h>

typedef struct knot_ {
    int valor;
    int chave;
} knot;

//Busca em listas não ordenadas
int buscaV1 (knot L[], int n, int x) { //Onde n é o tamanho da lista e x o valor a ser buscado
    int i = 0;
    while (i < n) {
        if (L[i].chave == x) { //O valor a ser buscado, neste caso, é o elemento dentro do .chave; é possível realizar a busca pelo .valor também
            return x;
            i = n;
        }
        else {
            i++;
        }
    }
    return -1;
}

//Busca em listas ordenadas
int busca_ordV1 (knot L[], int n, int x) {
    L[n].chave = x;
    int i = 0;
    while (L[i].chave < x) {
        i++;
    }
    if (i == n || L[i].chave != x) {
        return -1;
    }
    return i;
}

void main () {
    int resposta, retorno;
    knot L[10];
    L[0].chave = 10;
    L[1].chave = 20;
    L[2].chave = 30;
    L[3].chave = 40;
    L[4].chave = 50;
    L[5].chave = 60;
    L[6].chave = 70;
    L[7].chave = 80;
    L[8].chave = 90;
    L[9].chave = 100;

    printf("Escolha o numero a ser buscado: ");
    scanf("%d", &resposta);
    retorno = buscaV1(L, 10, resposta);
    if (retorno != -1) {
        printf("Valor encontrado.");
    } else {
        printf("Valor nao encontrado.");
    }
}