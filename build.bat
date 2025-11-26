@echo off
echo ========================================
echo Compilando OrgaUNS con los 4 componentes
echo ========================================
echo.

cd /d "%~dp0"

echo [1/3] Sincronizando Gradle...
call gradlew.bat --refresh-dependencies
if %errorlevel% neq 0 (
    echo ERROR: Fallo la sincronizacion de Gradle
    pause
    exit /b 1
)

echo.
echo [2/3] Limpiando build anterior...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo ERROR: Fallo la limpieza
    pause
    exit /b 1
)

echo.
echo [3/3] Compilando aplicacion...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo ERROR: Fallo la compilacion
    pause
    exit /b 1
)

echo.
echo ========================================
echo COMPILACION EXITOSA!
echo ========================================
echo APK generado en: app\build\outputs\apk\debug\app-debug.apk
echo.
pause

