#include <stdio.h>
#include <stdlib.h>

typedef struct reg {
   int conteudo;
   struct reg *prox;
} celula;

int main (void) {
   printf ("sizeof (celula) = %d\n", 
            sizeof (celula));
   return EXIT_SUCCESS;
}

void imprim (celula *le) {
   if (le != NULL) {
      printf ("%d\n", le->conteudo);
      imprime (le->prox);
   }
}