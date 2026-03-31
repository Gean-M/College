#!/bin/bash

remove_saida () {
	i=1
	FILE=./saida_$i.txt
	while [ -f "$FILE" ]
	do
		rm $FILE
		((i=i+1))
		FILE=./saida_$i.txt
	done
}

executa () {
	i=1
	FILE=./entrada_$i.txt
	while [ -f "$FILE" ]
	do
		./$1 $i
		((i=i+1))
		FILE=./entrada_$i.txt
	done
}

gabarito () {
	BLACK='\033[0;30m'
	RED='\033[0;31m'
	GREEN='\033[0;32m'

	i=1
	total=0
	certas=0
	SAIDA=./saida_$i.txt
	GABARITO=./gabarito_$i.txt
	while [ -f "$SAIDA" ]
	do
		if cmp -s "$SAIDA" "$GABARITO"; then
			echo -e "${BLACK}O teste $i executou ${GREEN}corretamente${BLACK}."
			((certas=certas+1))
		else
			echo -e "${BLACK}O teste $i ${RED}falhou${BLACK}."

		fi
		((total=total+1))
		((i=i+1))

		SAIDA=./saida_$i.txt
		GABARITO=./gabarito_$i.txt
	done
	resultado=0
	if [ "$total" -ne "0" ]; then
		resultado=$(bc <<< "scale=2 ; ($certas*100.0 / $total)")
	fi
	echo "========================="
	echo -e "percentual total de acertos: "$resultado"%."
	echo $resultado > resultado

}

remove_saida
executa $1
gabarito


