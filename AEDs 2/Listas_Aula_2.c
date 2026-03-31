#include<stdio.h>
#include<stdlib.h>

typedef struct knot_ {
    int valor;
    int chave;
} knot;

//Função para realizar buscas dentro de uma lista (não ordenada)
int buscar (knot L[], int n, int x) {
    L[n].chave = x;
    int i = 0;
    while (L[i].chave != x) {
        i++;
    }

    if (i != n) {
        return i; //Retorna a posição na qual está inserido o número buscado;
    }
    return -1;
}

//Função para realizar uma inserção dentro de uma lista
int inserir (knot L[], int n, int m, knot d) {
    if (n < m) {
        if (buscar(L, n, d.chave) == -1) {
            L[n].chave = d.chave;
            L[n].valor = d.valor;
            return n+1;
        }
        return 0;
    }
    return -1;
}

knot* remover (int x, knot L[], int *n) {
    knot * retorno = NULL;
    if (n != 0) {
        int indice = buscar(L, *n, x);
        if (indice >= 0) {
            retorno = malloc(sizeof(knot));
            (*retorno).chave = L[indice].chave;
            (*retorno).valor = L[indice].valor;
            for (int i = indice; i < (*n)-1; i++) {
                L[i].chave = L[i+1].chave;
                L[i].valor = L[i+1].valor;
            }
            *n = (*n)-1;
        }
    }
    return retorno;
}

void imprimir (knot L[], int n) {
    if (!n) {
        printf("<Lista vazia!>");
    }
    for (int i = 0; i < n; i++) {
        printf("%d: %d\n", L[i].chave, L[i].valor);
        printf("=========\n");
    }
}

void ler_menu(int * resposta){
    printf("\n-----------------------\n");
    printf("escolha uma das opcoes:\n");
    printf("0 - sair\n");
    printf("1 - inserir\n");
    printf("2 - remover\n");
    printf("3 - imprimir\n");
    scanf("%d", resposta);
    printf("-----------------------\n\n");
}

void ler_no(knot * novo_no){
    printf("informe a chave: ");
    scanf("%d", &(*novo_no).chave);
    printf("informe o valor: ");
    scanf("%d", &(*novo_no).valor);
    printf("\n");
}

void main () {
    int m; //Tamanho máximo
    int n = 0; //Tamanho atual
    int resposta = 1;

    printf("Informe o tamanho maximo da lista: ");
    scanf("%d", &m);
    knot L[m+1];
    while (resposta != 0) {
            ler_menu(&resposta);
            if(resposta == 0) {
                //sair
                return ;
            } if(resposta == 1) {
                //inserir
                knot novo_no;
                ler_no(&novo_no);
                int valor_retornado = inserir (L, n, m, novo_no);
                if(valor_retornado == -1){
                    printf("lista cheia\n");
                }else if(valor_retornado == 0){
                    printf("elemento ja existente\n");
                }else{
                    n = valor_retornado;
                    printf("elemento inserido\ntamanho da lista: %d\n", n);
                }
            } else if (resposta == 2) {
                //remover
                int chave;
                printf("informe a chave a remover: ");
                scanf("%d", &chave);

                knot * no_removido = remover(chave, L, &n);
                if(no_removido != NULL){
                    printf("conteudo do no removido:\n");
                    printf("%d: %d\n",(*no_removido).chave, (*no_removido).valor);
                    printf("tamanho da lista: %d\n", n);
                    free(no_removido);
                }else {
                    printf("elemento inexistente\n");
                }
            } else if(resposta == 3) {
                //imprimir
                imprimir(L, n);
            } else {
                printf("Opcao invalida\n");
            }
        }
}