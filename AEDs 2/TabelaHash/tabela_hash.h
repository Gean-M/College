/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   tabela_hash.h
 * Author: flavio
 *
 */

#ifndef TABELA_HASH_H
#define TABELA_HASH_H

#ifdef __cplusplus
extern "C" {
#endif

#ifdef __cplusplus
}
#endif

typedef struct estudante_{
	int matricula;//chave
	char nome[30];//valor
}estudante;

typedef struct hash_{
	int qtd, TABLE_SIZE;//qtd = quantidade de elementos inseridos,
	//TABLE_SIZE = tamanho da tabela.
	estudante ** estudantes;//vetor de estudantes.
}hash;

estudante * cria_estudante(char * nome, int matricula);
hash * cria_hash(int TABLE_SIZE);
void libera_hash(hash * ha);

int insere_hash_s_colisao(hash * ha, estudante * e);
char* busca_hash_s_colisao(hash * ha, int matricula);

int sondagem_linear(int pos, int i, int TABLE_SIZE);
int sondagem_quadratica(int pos, int i, int TABLE_SIZE);
int duplo_hash(int H1, int chave, int i, int TABLE_SIZE);

int insere_hash_end_aberto(hash * ha, estudante * e);
char* busca_hash_end_aberto(hash * ha, int matricula);

/**
 * TODO!
 * implementar as funções do encadeamento separado:
 *  - insere_hash_enc_separado
 *  - busca_hash_enc_separado
 *  - remove_hash_enc_separado
 */


#endif /* TABELA_HASH_H */

