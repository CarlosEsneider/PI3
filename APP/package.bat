@echo off
REM ============================================================
REM SIGEIV-Volcano: Empaquetado de Aplicacion Independiente
REM ============================================================

echo ========================================
echo   1. Compilando clases...
echo ========================================
if not exist "bin" mkdir bin
javac --release 17 -encoding UTF-8 -d bin -cp "lib\sqlite-jdbc-3.41.2.1.jar" src\com\sigeiv\modelo\*.java src\com\sigeiv\util\*.java src\com\sigeiv\dao\*.java src\com\sigeiv\controlador\*.java src\com\sigeiv\vista\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] La compilacion fallo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   2. Preparando archivos para empaquetado...
echo ========================================
if not exist "build" mkdir build
if not exist "build\lib" mkdir build\lib
copy "lib\sqlite-jdbc-3.41.2.1.jar" "build\lib\" /Y >nul

echo Class-Path: lib/sqlite-jdbc-3.41.2.1.jar> build\MANIFEST.MF
echo Main-Class: com.sigeiv.vista.LoginFrame>> build\MANIFEST.MF
echo.>> build\MANIFEST.MF

jar cvfm build\SIGEIV-Volcano.jar build\MANIFEST.MF -C bin .

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo la creacion del JAR.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   3. Generando ejecutable independiente con jpackage...
echo ========================================
if exist "dist_standalone" rmdir /s /q "dist_standalone"

jpackage --type app-image --name "SIGEIV-Volcano" --input build --main-jar SIGEIV-Volcano.jar --dest dist_standalone

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo el empaquetado con jpackage.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   4. Comprimiendo en un unico archivo .exe (SFX)...
echo ========================================
if exist "SIGEIV-Volcano-Unico.exe" del "SIGEIV-Volcano-Unico.exe"

REM Copiar la base de datos a la carpeta del instalador para que se incluya en el EXE
copy "sigeiv_volcano.db" "dist_standalone\SIGEIV-Volcano\" /Y

REM Crear archivo de configuracion para WinRAR
>> sfx_config.txt echo Path=%%TEMP%%\SIGEIV-Volcano
>> sfx_config.txt echo Setup=SIGEIV-Volcano.exe
>> sfx_config.txt echo Silent=1
>> sfx_config.txt echo Overwrite=1

cd dist_standalone\SIGEIV-Volcano
"C:\Program Files\WinRAR\WinRAR.exe" a -sfx -z"..\..\sfx_config.txt" -r "..\..\SIGEIV-Volcano-Unico.exe" *
cd ..\..

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo la creacion del ejecutable unico con WinRAR.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   5. Limpiando archivos temporales...
echo ========================================
rmdir /s /q bin
rmdir /s /q build
rmdir /s /q dist_standalone
del sfx_config.txt

echo.
echo ========================================
echo   EMPAQUETADO EXITOSO
echo ========================================
echo Tu aplicacion esta lista en un solo archivo: SIGEIV-Volcano-Unico.exe
echo Puedes copiar este archivo a cualquier PC y funcionara sin instalar Java.
echo.
pause

