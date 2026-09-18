@echo off
cd /d "%~dp0"
if not exist target\elastic-swing-gui.jar (
    echo Ainda nao compilado. Rodando build.bat primeiro...
    call build.bat
)
java -jar target\elastic-swing-gui.jar
