/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: flavio
 *
 */

#include <stdio.h>
#include <stdlib.h>
//#include <limits.h>

#include "tabela_hash.h"

/*
 * 
 */
int main() {
	hash * ha = cria_hash(1000);

	estudante * e = cria_estudante("Flavio Barbieri\0", 125);
	insere_hash_end_aberto(ha, e);

	e = cria_estudante("Joao das Couves\0", 1125);
	insere_hash_end_aberto(ha, e);

	char * a = busca_hash_end_aberto(ha, 125);
	if (a != NULL)
		printf("%s\n", a);
	else
		printf("Aluno nao encontrado.\n");

	a = busca_hash_end_aberto(ha, 1125);
	if (a != NULL)
		printf("%s\n", a);
	else
		printf("Aluno nao encontrado.");

	libera_hash(ha);
	return (EXIT_SUCCESS);
}

