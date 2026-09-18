#!/bin/bash
cd "$(dirname "$0")"
if [ ! -f target/elastic-swing-gui.jar ]; then
    echo "Ainda não compilado. Rodando build.sh primeiro..."
    ./build.sh
fi
java -jar target/elastic-swing-gui.jar
