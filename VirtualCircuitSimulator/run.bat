@echo off
REM ============================================================
REM  Virtual Circuit Simulator — Windows Compile & Run Script
REM  Prerequisites: JDK 11+ installed, javac in PATH
REM ============================================================

echo [VCS] Compiling Virtual Circuit Simulator...

REM Create output directory for .class files
if not exist "out" mkdir out

REM Compile all .java files under src\
javac -d out -sourcepath src src\Main.java src\circuit\components\*.java src\circuit\network\*.java src\circuit\crypto\*.java src\circuit\utils\*.java

IF %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Make sure JDK is installed and javac is in PATH.
    pause
    exit /b 1
)

echo [VCS] Compilation successful!
echo [VCS] Running application...
echo.

REM Run the Main class (class files are in the 'out' folder)
java -cp out Main

pause
