#include <stdio.h>
#include <stdlib.h>

typdef struct no_ {
    int valor;
    int chave;
    struct no_ * prox;
} no;

void buscar (no* ptlista, int x, no** ant, no** pont) {
    *ant = ptlista;
    *pont = NULL;
    no * ptr = ptlista->prox;

    while (ptr != NULL) {
        if (ptr->chave < x) {
            *ant = ptr;
            ptr = ptr->prox;
        } else {
            if (ptr->chave == x) {
            *pont = ptr;
            }
            ptr = NULL;
        }
    }
}

no* inserir (no * ptlista, no * novo_no) {
    int inserir_enc = -1;
    int x = novo_no->chave;
    buscar (no * novo_no, x, no** ant, no** pont);
    if (*pont == NULL) {
        novo_no->prox = ant->prox;
        ant->prox = *novo_no;
        return NULL;
    }
    return inserir_enc;
}

void imprimir (no* L) {
   if (L != NULL) {
      printf ("%d: %d\n", le->chave, le->valor);
      imprime (le->prox);
   }
}

/* OUTRA OPÇÃO:
void imprimir (no * L){
	no * p;
	for (p = L->prox; p != NULL; p = p->prox) {
		printf("%d: %d\n", p->chave, p->valor);
	}
	printf("\n");
} */