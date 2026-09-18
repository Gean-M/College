#!/bin/bash
# Compila o projeto usando apenas o JDK (javac + jar) - não requer Maven
# nem acesso à internet, já que o projeto não tem dependências externas.
set -e

cd "$(dirname "$0")"

echo "Limpando build anterior..."
rm -rf target
mkdir -p target/classes

echo "Compilando fontes Java (release 17)..."
# Usamos find -print0 / xargs -0 (em vez de um arquivo @sources.txt) para não
# depender da decodificação de um argfile - isso evita problemas quando o
# caminho do projeto contém acentos ou outros caracteres especiais.
find src/main/java -name "*.java" -print0 | xargs -0 javac --release 17 -d target/classes -encoding UTF-8

echo "Gerando elastic-swing-gui.jar..."
cat > target/MANIFEST.MF << 'EOF'
Main-Class: com.elasticgui.Main
EOF
jar --create --file target/elastic-swing-gui.jar --manifest target/MANIFEST.MF -C target/classes .

echo ""
echo "Build concluído: target/elastic-swing-gui.jar"
echo "Para executar: ./run.sh   (ou: java -jar target/elastic-swing-gui.jar)"
