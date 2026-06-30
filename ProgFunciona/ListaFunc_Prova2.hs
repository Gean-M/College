{-
    LISTA DE EXERCÍCIOS - PROGRAMAÇÃO FUNCIONAL
    NOME: Gean Marques | R.A.: 2019.1.08.006
    PROFESSOR: Fellipe Rey
-}

import Data.Char

-- QUESTÃO 1: VERIFICA SE O NÚMERO É PERFEITO
ehPerfeito :: Int -> Bool
ehPerfeito a = somaDivisores a == a
  where
    somaDivisores :: Int -> Int
    somaDivisores a = soma [x | x <- [1 .. a - 1], a `mod` x == 0]
      where
        soma :: [Int] -> Int
        soma [] = 0
        soma (x : xs) = x + soma xs

-- QUESTÃO 2:

-- QUESTÃO 3: INVERTE STRING
inverteString :: String -> String
inverteString [] = []
inverteString (x : xs) = inverteString xs ++ [x]

-- QUESTÃO 4: CALCULAR O QUADRADO DOS NÚMEROS DE UMA LISTA
quadrado :: [Int] -> [Int]
quadrado = map (^ 2)

-- QUESTÃO 5: PRODUTO CARTESIANO ENTRE DUAS LISTAS
cartesiano :: [a] -> [b] -> [(a, b)]
cartesiano [] [] = []
cartesiano xs ys = [(x, y) | x <- xs, y <- ys]

-- QUESTÃO 6: SELECIONA OS NÚMEROS POSITIVOS DA LISTA
positivos :: [Int] -> [Int]
positivos = filter (> 0)

-- QUESTÃO 7: SOMA DO DOBRO DAS FUNÇÕES
somaDobro :: [Int] -> Int
somaDobro xs = foldr1 (+) [2 * x | x <- xs]

-- QUESTÃO 7 (ALTERNATIVA)
somaDobro2 :: [Int] -> Int
somaDobro2 = foldr1 (+) . map (* 2)

-- QUESTÃO 8: CONCATENAÇÃO DE STRING
concatena :: [String] -> String
concatena = foldr1 (++)