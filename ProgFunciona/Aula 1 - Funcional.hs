potencia_2 :: Float -> Float
potencia_2 x = x * x

disc :: Float -> Float -> Float -> Float
disc a b c = (potencia_2 b) - 4 * a * c

dist_euc :: Float -> Float -> Float -> Float -> Float
dist_euc x1 x2 y1 y2 
	| (x1==x2) = sqrt(potencia_2(y2-y1))
	| (y1==y2) = sqrt(potencia_2(x2-x1))
	| otherwise = sqrt(potencia_2(x2-x1) + potencia_2(y2-y1))

qntd_raizes :: Float -> Float -> Float -> IO ()
qntd_raizes a b c
	| (disc a b c < 0) = putStrLn "Nao ha raiz"
	| (disc a b c > 0) = putStrLn "Ha duas raizes"
	| otherwise = putStrLn "Ha uma raiz"

main = do 
putStrLn "Para calcular a distancia euclidiana, digite 'dist_euc' seguido das coordenadas x1, x2, y1 e y2, em ordem."
putStrLn "Para descobrir a quantidade de raizes, digite 'qntd_raizes' seguido das constantes a, b e c da equacao, em ordem."