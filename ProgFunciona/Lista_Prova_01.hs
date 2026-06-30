-- EXERCICIO 1 - NÚMERO PRIMO --
ehPrimo :: Int -> Bool
ehPrimo n
    | (verificarPrimo n == 2) = True
    | otherwise = False

verificarPrimo :: Int -> Int
verificarPrimo x = aux x x
    where 
        aux x y
            | (y == 0) = 0
            | ((x`mod`y) == 0) = 1 + aux x (y-1)
            | otherwise = aux x (y-1)

-- EXERCICIO 2 - ORDENA EM TUPLA --
ordenaEmTupla :: Int -> Int -> Int -> Int -> (Int, Int, Int, Int)
ordenaEmTupla a b c d = (menor, menor2, maior2, maior)
    where 
        menor = minimum [a,b,c,d]
        maior = maximum [a,b,c,d]
        maior2 = min (max a b) (max c d)
        menor2 = max (min a b) (min c d)

-- EXERCICIO 3 - DIAS NO ANO --
quantosDias :: Int -> Int
quantosDias x
    | (x`mod`4) == 0 = 366
    | otherwise = 365

-- EXERCICIO 4 - DIAS NO MES DE CERTO ANO --
diasMes :: Int -> Int -> Int
diasMes x y
    | (y == 1||y == 3||y == 5||y == 7||y == 8||y == 10||y == 12) = 31
    | (y == 4||y == 6||y == 9||y == 11) = 30
    | ((y == 2) && (quantosDias x == 365)) = 28
    | otherwise = 29

-- EXERCICIO 5 - DIAS TOTAIS ATE AQUELE DIA --
dia :: Int -> Int -> Int -> Int
dia x y z
    | ((z > (diasMes x y)) || (y > 12)) = -1
    | otherwise = (z + (dias x y))

dias :: Int -> Int -> Int
dias x y
    | (y == 1) = 0
    | otherwise = diasMes x (y-1) + dias x (y-1)

-- EXERCICIO 6 - MIN E MAX --
maioremenor :: [Int] -> (Int, Int)
maioremenor [] = (maxBound :: Int, minBound :: Int)
maioremenor (x:xs) = (menor (x:xs), maior (x:xs))
    where
        menor :: [Int] -> Int
        menor [] = x
        menor (a:b) = menorV a (menor b)
            where
                menorV :: Int -> Int -> Int
                menorV a b
                    | a < b = a
                    | otherwise = b
        
        maior :: [Int] -> Int
        maior [] = x
        maior (a:b) = maiorV a (maior b)
            where
                maiorV :: Int -> Int -> Int
                maiorV a b
                    | a > b = a
                    | otherwise = b

-- EXERCICIO 7 - ORDENACAO --
ordena :: [Int] -> [Int]
ordena [] = []
ordena (s:xs) = ordena [x|x <- xs, x < s] 
                ++ [s] ++ 
                ordena [x|x <- xs, x >= s]

-- EXERCICIO 8 - REPETE ELEMENTOS --
repeteElemento :: [Int] -> [Int]
repeteElemento [] = []
repeteElemento (x:xs) = replicate x x ++ repeteElemento xs

-- EXERCICIO 4 - PROVA --
semrepeticao :: [Int] -> [Int] -> [Int]
semrepeticao [] [] = []
semrepeticao (x:xs) (y:ys) = [x] ++ semrepeticao[xs][ys] ++ [if (verifica (x:xs) y) == False then [y] else ]

verifica :: [Int] -> Int -> Bool
verifica [] y = False
verifica (x:xs) y
    | (x == y) = True
    | otherwise = verifica (xs) y