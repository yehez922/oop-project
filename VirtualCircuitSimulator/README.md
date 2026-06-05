# Virtual Circuit Simulator & Solver
### OOP Project — ECE 2026 | Project Choice #1

---

## How to Compile and Run

### On Windows:
1. Install Java JDK 11 or higher from https://adoptium.net
2. Make sure `javac` is in your PATH
3. Double-click `run.bat` OR open Command Prompt and type:
   ```
   run.bat
   ```

### On Linux / Mac:
1. Install JDK: `sudo apt install default-jdk` (Ubuntu/Debian)
2. Make the script executable: `chmod +x run.sh`
3. Run: `./run.sh`

### Manual Compilation (any OS):
```bash
mkdir out
javac -d out -sourcepath src src/Main.java src/circuit/components/*.java src/circuit/network/*.java src/circuit/crypto/*.java src/circuit/utils/*.java
java -cp out Main
```

---

## Project Structure

```
VirtualCircuitSimulator/
├── src/
│   ├── Main.java                          ← Entry point, interactive menu
│   └── circuit/
│       ├── components/
│       │   ├── Component.java             ← Abstract base class (ABSTRACTION)
│       │   ├── Measurable.java            ← Interface (CAN-DO contract)
│       │   ├── Resistor.java              ← Extends Component (INHERITANCE)
│       │   ├── Capacitor.java             ← Extends Component (INHERITANCE)
│       │   └── Inductor.java              ← Extends Component (INHERITANCE)
│       ├── network/
│       │   ├── Circuit.java               ← Implements Measurable (INTERFACE)
│       │   ├── CircuitSolver.java         ← Analysis engine (OVERLOADING)
│       │   └── CircuitLibrary.java        ← Circuit registry (ENCAPSULATION)
│       ├── crypto/
│       │   └── CryptoManager.java         ← AES-128 via javax.crypto
│       └── utils/
│           ├── DisplayUtils.java          ← Console UI formatting
│           └── InputValidator.java        ← Safe user input reading
├── run.bat                                ← Windows run script
├── run.sh                                 ← Linux/Mac run script
└── README.md                              ← This file
```

---

## OOP Concepts Implemented

| Concept       | Where Demonstrated                                                  |
|---------------|---------------------------------------------------------------------|
| Abstraction   | `Component` (abstract class), `Measurable` (interface)             |
| Inheritance   | `Resistor`, `Capacitor`, `Inductor` all extend `Component`          |
| Polymorphism  | `getImpedance()` behaves differently per component type             |
|               | `CircuitSolver.solve()` is overloaded with 4 signatures             |
| Encapsulation | All fields are `private`; accessed via `public` getters/setters     |
| Cryptography  | `javax.crypto.Cipher`, `java.security.SecureRandom` (AES-128 CBC)  |

---

## Features

- Add Resistors, Capacitors, and Inductors to a circuit
- Series and Parallel circuit configurations
- AC/DC analysis at any frequency
- Frequency sweep (Bode-style table output)
- Resonant frequency calculation: f = 1/(2π√LC)
- Voltage drop across each component
- AES-128 CBC encryption of circuit data
- Save/load circuits in a library
- 6 pre-built demo circuits
