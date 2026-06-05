#!/bin/bash
# ============================================================
#  Virtual Circuit Simulator — Linux/Mac Compile & Run Script
#  Prerequisites: JDK 11+ installed (sudo apt install default-jdk)
# ============================================================

echo "[VCS] Compiling Virtual Circuit Simulator..."

# Create output directory for .class files
mkdir -p out

# Compile all Java source files
javac -d out -sourcepath src \
    src/Main.java \
    src/circuit/components/*.java \
    src/circuit/network/*.java \
    src/circuit/crypto/*.java \
    src/circuit/utils/*.java

if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed."
    echo "        Make sure JDK is installed: sudo apt install default-jdk"
    exit 1
fi

echo "[VCS] Compilation successful!"
echo "[VCS] Running application..."
echo ""

# Run the Main class
java -cp out Main
