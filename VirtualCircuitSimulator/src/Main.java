import circuit.components.*;
import circuit.network.*;
import circuit.crypto.CryptoManager;
import circuit.utils.*;

/**
 * =============================================================================
 * CLASS: Main (Entry Point)
 * =============================================================================
 * This is where the program STARTS. Java always looks for main() here first.
 *
 * This class ties everything together:
 *   - Presents an interactive console menu
 *   - Lets users build circuits with Resistors, Capacitors, and Inductors
 *   - Performs series and parallel circuit analysis
 *   - Runs frequency sweeps
 *   - Demonstrates AES encryption via javax.crypto
 *   - Shows resonant frequency calculation
 *
 * HOW THE OOP HIERARCHY WORKS (summary):
 *
 *   [INTERFACE]   Measurable        (CAN-DO: can be measured)
 *                     ↑
 *   [ABSTRACT]    Component         (IS-A root, cannot be instantiated)
 *                  /    |    \
 *   [CONCRETE] Resistor Capacitor Inductor   (real components)
 *
 *   Circuit  implements  Measurable
 *   Circuit  contains    Component[] (any mix of R, C, L)
 *
 *   CircuitSolver  works on  Circuit
 *   CryptoManager  protects  circuit data (javax.crypto AES)
 * =============================================================================
 */
public class Main {

    /**
     * PROGRAM ENTRY POINT.
     * Java starts execution here when you run the program.
     *
     * @param args Command-line arguments (not used in this application)
     */
    public static void main(String[] args) {

        // Display the welcome banner
        DisplayUtils.printBanner();
        DisplayUtils.printOOPLegend();

        // Create the circuit library — holds all user-saved circuits
        CircuitLibrary library = new CircuitLibrary("My Circuit Library");

        // Create the crypto manager for AES encryption demonstrations
        CryptoManager crypto = null;
        try {
            crypto = new CryptoManager();
        } catch (Exception e) {
            DisplayUtils.printError("Could not initialize CryptoManager: " + e.getMessage());
        }

        // Pre-load some demo circuits into the library for quick testing
        preloadDemoCircuits(library);

        boolean running = true;

        // =====================================================================
        // MAIN MENU LOOP
        // The program keeps showing this menu until the user types 0 (Exit)
        // =====================================================================
        while (running) {
            DisplayUtils.printMainMenu();
            int choice = InputValidator.readInt();

            switch (choice) {

                case 1: // Build a new circuit interactively
                    buildCircuitInteractive(library);
                    break;

                case 2: // Analyze a circuit from the library
                    analyzeCircuitInteractive(library);
                    break;

                case 3: // View library of all saved circuits
                    library.printLibrarySummary();
                    break;

                case 4: // Frequency sweep on a chosen circuit
                    frequencySweepInteractive(library);
                    break;

                case 5: // Find resonant frequency
                    findResonanceInteractive(library);
                    break;

                case 6: // AES encryption demonstration
                    if (crypto != null) {
                        encryptionDemo(crypto, library);
                    } else {
                        DisplayUtils.printError("CryptoManager not available.");
                    }
                    break;

                case 7: // Run all pre-built demo circuits
                    runAllDemos();
                    break;

                case 0: // Exit
                    running = false;
                    System.out.println();
                    System.out.println("  ╔═══════════════════════════════════════╗");
                    System.out.println("  ║  Thank you for using VCS Simulator!   ║");
                    System.out.println("  ║  OOP Project — Good luck!             ║");
                    System.out.println("  ╚═══════════════════════════════════════╝");
                    System.out.println();
                    break;

                default:
                    DisplayUtils.printError("Invalid choice. Please enter 0-7.");
            }
        }

        InputValidator.close();
    }

    // =========================================================================
    // INTERACTIVE MENU HANDLERS
    // =========================================================================

    /**
     * Interactive circuit builder — lets the user:
     *   1. Name their circuit
     *   2. Choose series or parallel
     *   3. Set source voltage
     *   4. Add components (R, C, L) one by one
     *   5. Save to library
     *
     * @param library The circuit library to save into
     */
    private static void buildCircuitInteractive(CircuitLibrary library) {
        DisplayUtils.printSectionHeader("BUILD NEW CIRCUIT");

        // Step 1: Get circuit name
        System.out.print("  Enter circuit name (e.g. 'My RLC Circuit'): ");
        String name = InputValidator.readString();

        // Step 2: Series or Parallel?
        System.out.println("  Connection type:");
        System.out.println("  [1] Series   (components in a single line)");
        System.out.println("  [2] Parallel (components side by side)");
        System.out.print("  Choose: ");
        boolean isSeries;
        int connChoice = InputValidator.readInt();
        isSeries = (connChoice != 2); // Default to series if invalid

        // Step 3: Source voltage
        DisplayUtils.printPromptLine("Source Voltage (Vs)", "V");
        double voltage = InputValidator.readPositiveDouble("Source Voltage");

        // Create the circuit object
        Circuit circuit = new Circuit(name, isSeries, voltage);
        System.out.println("  Circuit created: " + circuit);

        // Step 4: Add components in a loop
        boolean addingComponents = true;
        int componentCount = 1; // For auto-labeling: R1, R2, C1, etc.

        while (addingComponents) {
            DisplayUtils.printComponentMenu();
            int compChoice = InputValidator.readInt();

            if (compChoice == 0) {
                addingComponents = false;
                break;
            }

            switch (compChoice) {
                case 1: // Add Resistor
                    System.out.print("  Resistor label (e.g. R1): ");
                    String rName = InputValidator.readString();
                    DisplayUtils.printPromptLine("Resistance", "Ω");
                    double resistance = InputValidator.readPositiveDouble("Resistance");

                    // POLYMORPHISM in action: We pass a Resistor to addComponent(Component)
                    circuit.addComponent(new Resistor(rName, resistance));
                    break;

                case 2: // Add Capacitor
                    System.out.print("  Capacitor label (e.g. C1): ");
                    String cName = InputValidator.readString();
                    DisplayUtils.printPromptLine("Capacitance", "F (e.g. 0.0001 for 100µF)");
                    double capacitance = InputValidator.readPositiveDouble("Capacitance");

                    circuit.addComponent(new Capacitor(cName, capacitance));
                    break;

                case 3: // Add Inductor
                    System.out.print("  Inductor label (e.g. L1): ");
                    String lName = InputValidator.readString();
                    DisplayUtils.printPromptLine("Inductance", "H (e.g. 0.001 for 1mH)");
                    double inductance = InputValidator.readPositiveDouble("Inductance");

                    circuit.addComponent(new Inductor(lName, inductance));
                    break;

                default:
                    DisplayUtils.printError("Invalid component type.");
            }
        }

        if (circuit.getComponentCount() == 0) {
            DisplayUtils.printError("Circuit has no components. Not saved.");
            return;
        }

        // Step 5: Save to library
        library.saveCircuit(circuit);
        DisplayUtils.printSuccess("Circuit saved to library!");

        // Step 6: Immediately show analysis?
        if (InputValidator.readYesNo("Analyze this circuit now?")) {
            System.out.print("  Enter frequency to analyze at (Hz): ");
            double freq = InputValidator.readNonNegativeDouble("Frequency");

            CircuitSolver solver = new CircuitSolver(circuit);
            solver.printCircuitDiagram(freq);
            solver.solve(freq); // OVERLOAD 1: single frequency
        }
    }

    /**
     * Interactive circuit analyzer — user selects from library, enters frequency.
     *
     * @param library The circuit library to pick from
     */
    private static void analyzeCircuitInteractive(CircuitLibrary library) {
        DisplayUtils.printSectionHeader("ANALYZE CIRCUIT");

        if (library.size() == 0) {
            DisplayUtils.printInfo("Library is empty. Build a circuit first (option 1).");
            return;
        }

        library.printLibrarySummary();
        System.out.print("  Enter circuit name to analyze: ");
        String name = InputValidator.readString();

        Circuit circuit = library.getCircuit(name);
        if (circuit == null) {
            DisplayUtils.printError("Circuit '" + name + "' not found in library.");
            return;
        }

        System.out.print("  Enter frequency (Hz) [0 for DC]: ");
        double freq = InputValidator.readNonNegativeDouble("Frequency");

        CircuitSolver solver = new CircuitSolver(circuit);
        solver.printCircuitDiagram(freq);

        // Calls OVERLOAD 1 of solve() — single frequency
        solver.solve(freq);
    }

    /**
     * Interactive frequency sweep — runs analysis across a range of frequencies.
     *
     * @param library The circuit library
     */
    private static void frequencySweepInteractive(CircuitLibrary library) {
        DisplayUtils.printSectionHeader("FREQUENCY SWEEP ANALYSIS");

        if (library.size() == 0) {
            DisplayUtils.printInfo("Library is empty. Build a circuit first (option 1).");
            return;
        }

        library.printLibrarySummary();
        System.out.print("  Enter circuit name to sweep: ");
        String name = InputValidator.readString();

        Circuit circuit = library.getCircuit(name);
        if (circuit == null) {
            DisplayUtils.printError("Circuit not found.");
            return;
        }

        System.out.print("  Start frequency (Hz): ");
        double startF = InputValidator.readPositiveDouble("Start frequency");
        System.out.print("  End frequency (Hz): ");
        double endF = InputValidator.readPositiveDouble("End frequency");
        System.out.print("  Number of steps (e.g. 10): ");
        int steps = InputValidator.readInt();
        if (steps < 2) steps = 5;

        CircuitSolver solver = new CircuitSolver(circuit);
        // Calls OVERLOAD 3 of solve() — range + steps
        solver.solve(startF, endF, steps);
    }

    /**
     * Interactive resonant frequency finder.
     *
     * @param library The circuit library
     */
    private static void findResonanceInteractive(CircuitLibrary library) {
        DisplayUtils.printSectionHeader("FIND RESONANT FREQUENCY");

        if (library.size() == 0) {
            DisplayUtils.printInfo("Library is empty. Build a circuit first (option 1).");
            return;
        }

        library.printLibrarySummary();
        System.out.print("  Enter circuit name: ");
        String name = InputValidator.readString();

        Circuit circuit = library.getCircuit(name);
        if (circuit == null) {
            DisplayUtils.printError("Circuit not found.");
            return;
        }

        CircuitSolver solver = new CircuitSolver(circuit);
        double resFreq = solver.findResonantFrequency();

        if (resFreq > 0) {
            // Analyze at the resonant frequency
            System.out.println("\n  Analysis AT resonant frequency:");
            solver.solve(resFreq);
        }
    }

    /**
     * Demonstrates AES encryption of circuit data.
     * Required by the project brief: javax.crypto + java.security.
     *
     * @param crypto  The crypto manager
     * @param library The library to get circuit data from
     */
    private static void encryptionDemo(CryptoManager crypto, CircuitLibrary library) {
        DisplayUtils.printSectionHeader("AES ENCRYPTION DEMO");

        // Build a sample circuit data string to encrypt
        String dataToEncrypt;

        if (library.size() > 0) {
            // Use the first saved circuit's info as the data to encrypt
            String firstName = library.listCircuitNames().iterator().next();
            Circuit c = library.getCircuit(firstName);
            dataToEncrypt = "Circuit:" + c.getCircuitName()
                + " | Type:" + c.getConnectionType()
                + " | Vs=" + c.getSourceVoltage() + "V"
                + " | Components=" + c.getComponentCount();
        } else {
            dataToEncrypt = "VCS-Circuit | R=1000Ω | C=0.0001F | L=0.01H | Vs=120V";
        }

        // Run the full encrypt → display → decrypt cycle
        crypto.demonstrateEncryption(dataToEncrypt);

        System.out.println();
        DisplayUtils.printInfo("The circuit parameters above were encrypted using AES-128 CBC.");
        DisplayUtils.printInfo("javax.crypto.Cipher and java.security.SecureRandom were used.");
        DisplayUtils.printInfo("Only the holder of the SecretKey can decrypt this data.");
    }

    // =========================================================================
    // DEMO CIRCUIT RUNNERS
    // =========================================================================

    /**
     * Pre-loads common example circuits into the library for quick testing.
     * Students can choose option 2 to immediately analyze these.
     */
    private static void preloadDemoCircuits(CircuitLibrary library) {
        System.out.println();
        DisplayUtils.printInfo("Loading pre-built demo circuits into library...");

        // ── Demo 1: Simple Series RC Circuit ──────────────────────────────
        Circuit rc = new Circuit("Series RC", Circuit.SERIES, 120.0);
        rc.addComponent(new Resistor("R1", 1000.0));      // 1kΩ resistor
        rc.addComponent(new Capacitor("C1", 0.0001));     // 100µF capacitor
        library.saveCircuit(rc);

        // ── Demo 2: Classic Series RLC Circuit ────────────────────────────
        Circuit rlc = new Circuit("Series RLC", Circuit.SERIES, 230.0);
        rlc.addComponent(new Resistor("R1", 100.0));      // 100Ω resistor
        rlc.addComponent(new Inductor("L1", 0.1));        // 100mH inductor
        rlc.addComponent(new Capacitor("C1", 0.00001));   // 10µF capacitor
        library.saveCircuit(rlc);

        // ── Demo 3: Parallel RL Circuit ───────────────────────────────────
        Circuit parallelRL = new Circuit("Parallel RL", Circuit.PARALLEL, 240.0);
        parallelRL.addComponent(new Resistor("R1", 500.0));  // 500Ω
        parallelRL.addComponent(new Inductor("L1", 0.05));   // 50mH
        library.saveCircuit(parallelRL);

        // ── Demo 4: Pure Resistor Network (DC circuit) ────────────────────
        Circuit dcResistors = new Circuit("DC Series R", Circuit.SERIES, 9.0);
        dcResistors.addComponent(new Resistor("R1", 100.0));
        dcResistors.addComponent(new Resistor("R2", 220.0));
        dcResistors.addComponent(new Resistor("R3", 330.0));
        library.saveCircuit(dcResistors);

        DisplayUtils.printSuccess("4 demo circuits loaded. Use option 2 to analyze them.");
    }

    /**
     * Runs all pre-built demonstrations automatically.
     * Great for a first look at the program's capabilities.
     */
    private static void runAllDemos() {
        DisplayUtils.printSectionHeader("RUNNING ALL DEMO CIRCUITS");

        // ── DEMO A: Series RLC at 60 Hz (US mains frequency) ─────────────
        System.out.println("\n  ━━━ DEMO 1: Series RLC Circuit at 60 Hz ━━━");
        Circuit rlc60 = new Circuit("RLC at 60Hz", Circuit.SERIES, 120.0);
        rlc60.addComponent(new Resistor("R1", 100.0));
        rlc60.addComponent(new Inductor("L1", 0.1));
        rlc60.addComponent(new Capacitor("C1", 0.00001));

        CircuitSolver solver60 = new CircuitSolver(rlc60);
        solver60.printCircuitDiagram(60.0);
        solver60.solve(60.0);     // OVERLOAD 1 used here

        // ── DEMO B: Find resonance in RLC circuit ─────────────────────────
        System.out.println("\n  ━━━ DEMO 2: RLC Resonance Analysis ━━━");
        Circuit rlcRes = new Circuit("RLC Resonance", Circuit.SERIES, 10.0);
        rlcRes.addComponent(new Resistor("R1", 50.0));
        rlcRes.addComponent(new Inductor("L1", 0.01));
        rlcRes.addComponent(new Capacitor("C1", 0.000001));

        CircuitSolver solverRes = new CircuitSolver(rlcRes);
        double fRes = solverRes.findResonantFrequency();
        if (fRes > 0) {
            solverRes.solve(fRes);   // Analyze exactly AT resonance
        }

        // ── DEMO C: Frequency Sweep ───────────────────────────────────────
        System.out.println("\n  ━━━ DEMO 3: Frequency Sweep 10 Hz → 10 kHz ━━━");
        Circuit sweepCircuit = new Circuit("RLC Sweep", Circuit.SERIES, 100.0);
        sweepCircuit.addComponent(new Resistor("R1", 200.0));
        sweepCircuit.addComponent(new Inductor("L1", 0.05));
        sweepCircuit.addComponent(new Capacitor("C1", 0.00005));

        CircuitSolver sweepSolver = new CircuitSolver(sweepCircuit);
        // OVERLOAD 3: range + step count
        sweepSolver.solve(10.0, 10000.0, 12);

        // ── DEMO D: Custom frequency array ───────────────────────────────
        System.out.println("\n  ━━━ DEMO 4: Custom Frequencies Array ━━━");
        double[] customFreqs = { 50.0, 60.0, 100.0, 159.15, 200.0, 1000.0 };
        sweepSolver.solve(customFreqs); // OVERLOAD 4 used here

        // ── DEMO E: Parallel Resistor Network ─────────────────────────────
        System.out.println("\n  ━━━ DEMO 5: Parallel Resistor Network (DC) ━━━");
        Circuit parallelR = new Circuit("Parallel Resistors", Circuit.PARALLEL, 12.0);
        parallelR.addComponent(new Resistor("R1", 100.0));
        parallelR.addComponent(new Resistor("R2", 200.0));
        parallelR.addComponent(new Resistor("R3", 300.0));

        CircuitSolver parallelSolver = new CircuitSolver(parallelR);
        parallelSolver.printCircuitDiagram(0.0);
        parallelSolver.solve(0.0); // DC analysis

        // ── DEMO F: Voltage drops in series circuit ────────────────────────
        System.out.println("\n  ━━━ DEMO 6: Voltage Distribution in Series RLC ━━━");
        Circuit vDropCircuit = new Circuit("Voltage Divider", Circuit.SERIES, 50.0);
        vDropCircuit.addComponent(new Resistor("R1", 300.0));
        vDropCircuit.addComponent(new Resistor("R2", 200.0));
        vDropCircuit.addComponent(new Inductor("L1", 0.02));

        CircuitSolver vSolver = new CircuitSolver(vDropCircuit);
        vSolver.printComponentVoltages(100.0); // 100 Hz

        System.out.println("\n  All demos completed!");
        DisplayUtils.printDivider();
    }
}
