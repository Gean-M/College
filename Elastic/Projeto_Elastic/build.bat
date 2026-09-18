@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo Limpando build anterior...
if exist target rmdir /s /q target
mkdir target\classes

echo Compilando fontes Java (release 17)...
set "FILES="
for /r "src\main\java" %%f in (*.java) do set "FILES=!FILES! "%%f""

javac --release 17 -d target\classes -encoding UTF-8 !FILES!
if errorlevel 1 exit /b 1

echo Gerando elastic-swing-gui.jar...
echo Main-Class: com.elasticgui.Main> target\MANIFEST.MF
jar --create --file target\elastic-swing-gui.jar --manifest target\MANIFEST.MF -C target\classes .

echo.
echo Build concluido: target\elastic-swing-gui.jar
echo Para executar: run.bat
