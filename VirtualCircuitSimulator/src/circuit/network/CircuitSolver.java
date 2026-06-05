package circuit.network;

import circuit.components.Component;
import java.util.List;

/**
 * =============================================================================
 * CLASS: CircuitSolver
 * =============================================================================
 * The CircuitSolver performs advanced analysis on a Circuit object.
 * It uses METHOD OVERLOADING to accept different input types for solving.
 *
 * METHOD OVERLOADING (Polymorphism) means having MULTIPLE methods with the
 * SAME NAME but DIFFERENT parameter lists. Java picks the right one based
 * on what arguments you pass in.
 *
 * This class demonstrates:
 *   - Method overloading (compile-time polymorphism)
 *   - Working with abstract Component references (runtime polymorphism)
 *   - Encapsulation of solver configuration
 * =============================================================================
 */
public class CircuitSolver {

    // -------------------------------------------------------------------------
    // ENCAPSULATION: Configuration fields are private
    // -------------------------------------------------------------------------
    private Circuit circuit;        // The circuit this solver works on
    private int    freqSteps;       // Number of frequency steps for sweep
    private double precision;       // Precision threshold for comparisons

    /** Default number of steps for frequency sweep analysis */
    private static final int    DEFAULT_STEPS     = 10;
    /** Default numeric precision for floating point comparisons */
    private static final double DEFAULT_PRECISION = 1e-6;

    /**
     * Constructor — attaches a solver to a specific circuit.
     *
     * @param circuit The circuit to analyze
     */
    public CircuitSolver(Circuit circuit) {
        this.circuit   = circuit;
        this.freqSteps = DEFAULT_STEPS;
        this.precision = DEFAULT_PRECISION;
    }

    // =========================================================================
    // OVERLOADED SOLVE METHODS (METHOD OVERLOADING = compile-time polymorphism)
    // Same method name "solve", different parameter lists!
    // =========================================================================

    /**
     * OVERLOAD 1: Solve at a single specific frequency.
     * Simple case — just give me the frequency in Hertz.
     *
     * @param frequencyHz The frequency to analyze at
     */
    public void solve(double frequencyHz) {
        System.out.println("\n  [Solver] Analyzing at single frequency: " + frequencyHz + " Hz");
        System.out.println(circuit.getMeasurementReport(frequencyHz));
        printComponentVoltages(frequencyHz);
    }

    /**
     * OVERLOAD 2: Solve over a frequency range (sweep analysis).
     * Performs analysis at multiple frequencies from start to end.
     *
     * @param startFreqHz Starting frequency in Hz
     * @param endFreqHz   Ending frequency in Hz
     */
    public void solve(double startFreqHz, double endFreqHz) {
        solve(startFreqHz, endFreqHz, DEFAULT_STEPS);
    }

    /**
     * OVERLOAD 3: Solve over a frequency range with custom step count.
     * Most detailed version — you control the resolution of the sweep.
     *
     * @param startFreqHz Starting frequency in Hz
     * @param endFreqHz   Ending frequency in Hz
     * @param steps       Number of frequency points to analyze
     */
    public void solve(double startFreqHz, double endFreqHz, int steps) {
        System.out.println("\n  [Solver] Frequency Sweep: " + startFreqHz
            + " Hz → " + endFreqHz + " Hz (" + steps + " steps)");
        System.out.println(
            "  ┌─────────────────────────────────────────────────────────────────┐");
        System.out.printf("  │ %-12s │ %-12s │ %-12s │ %-12s │ %-6s │%n",
            "Freq (Hz)", "|Z| (Ω)", "Phase (°)", "I (A)", "Behav.");
        System.out.println(
            "  ├─────────────────────────────────────────────────────────────────┤");

        double stepSize = (endFreqHz - startFreqHz) / Math.max(steps - 1, 1);

        for (int i = 0; i < steps; i++) {
            double freq    = startFreqHz + i * stepSize;
            double mag     = circuit.getMagnitude(freq);
            double phase   = circuit.getPhaseAngleDegrees(freq);
            double current = circuit.calculateCurrent(freq);
            String type    = phase > 0.5 ? "IndL" : (phase < -0.5 ? "Cap " : "Res ");

            System.out.printf("  │ %-12.2f │ %-12.4f │ %-12.4f │ %-12.6f │ %-6s │%n",
                freq, mag, phase, current, type);
        }
        System.out.println(
            "  └─────────────────────────────────────────────────────────────────┘");
    }

    /**
     * OVERLOAD 4: Solve using a pre-built frequency array.
     * Advanced use — pass in your own exact frequency values.
     *
     * @param frequencies Array of specific frequencies to evaluate
     */
    public void solve(double[] frequencies) {
        System.out.println("\n  [Solver] Analyzing at " + frequencies.length + " custom frequencies:");
        for (double freq : frequencies) {
            double mag   = circuit.getMagnitude(freq);
            double phase = circuit.getPhaseAngleDegrees(freq);
            System.out.printf("    f = %8.2f Hz → |Z| = %10.4f Ω, φ = %8.4f°%n",
                freq, mag, phase);
        }
    }

    // =========================================================================
    // ANALYSIS METHODS
    // =========================================================================

    /**
     * Finds the RESONANT FREQUENCY of an RLC circuit.
     * At resonance: XL = XC → the imaginary part of Z becomes 0.
     * Formula: f_res = 1 / (2π√(LC))
     *
     * @return Resonant frequency in Hz, or -1 if no L and C found
     */
    public double findResonantFrequency() {
        double totalL = 0.0;
        double totalC = 0.0;

        // Search through components to find total inductance and capacitance
        for (Component comp : circuit.getComponents()) {
            String className = comp.getClass().getSimpleName();
            if (className.equals("Inductor")) {
                totalL += comp.getValue();
            } else if (className.equals("Capacitor")) {
                totalC += comp.getValue();
            }
        }

        if (totalL <= 0 || totalC <= 0) {
            System.out.println("  [Solver] Cannot compute resonance — need both L and C.");
            return -1;
        }

        // f = 1 / (2π√(LC))
        double resonantFreq = 1.0 / (2 * Math.PI * Math.sqrt(totalL * totalC));
        System.out.printf(
            "%n  [Solver] Resonant Frequency Calculation:%n" +
            "    Total L = %.6f H%n" +
            "    Total C = %.6f F%n" +
            "    f_res   = 1 / (2π × √(%.6f × %.6f))%n" +
            "    f_res   = %.4f Hz%n",
            totalL, totalC, totalL, totalC, resonantFreq
        );

        return resonantFreq;
    }

    /**
     * Prints the voltage drop across each individual component.
     * This shows how the source voltage is "distributed" across components.
     * (Only meaningful for series circuits)
     *
     * @param frequencyHz Operating frequency in Hertz
     */
    public void printComponentVoltages(double frequencyHz) {
        double current = circuit.calculateCurrent(frequencyHz);
        List<Component> comps = circuit.getComponents();

        if (comps.isEmpty()) {
            System.out.println("  [Solver] No components to analyze.");
            return;
        }

        System.out.println("\n  [Solver] Voltage Distribution across Components:");
        System.out.println("  ┌─────────────────────────────────────────────────┐");
        System.out.printf("  │  Current through circuit: %-20.6f A │%n", current);
        System.out.println("  ├───────────┬──────────────────┬──────────────────┤");
        System.out.printf("  │ %-9s │ %-16s │ %-16s │%n",
            "Component", "|Z| (Ω)", "V-drop (V)");
        System.out.println("  ├───────────┼──────────────────┼──────────────────┤");

        double totalVolts = 0;
        for (Component comp : comps) {
            // POLYMORPHISM: correct calculateVoltageDrop() called per type
            double vDrop = comp.calculateVoltageDrop(current, frequencyHz);
            double[] z   = comp.getImpedance(frequencyHz);
            double zMag  = Math.sqrt(z[0]*z[0] + z[1]*z[1]);

            // Cap display of "infinite" values for DC capacitor
            String zStr = (zMag > 1e15) ? "     ∞ (open)" : String.format("%16.4f", zMag);
            String vStr = (vDrop > 1e15) ? "     ∞ (open)" : String.format("%16.4f", vDrop);

            System.out.printf("  │ %-9s │ %16s │ %16s │%n",
                comp.getName(), zStr, vStr);

            if (vDrop < 1e15) totalVolts += vDrop;
        }

        System.out.println("  ├───────────┴──────────────────┼──────────────────┤");
        System.out.printf("  │  Total (should ≈ source V)   │ %16.4f │%n", totalVolts);
        System.out.printf("  │  Source Voltage              │ %16.4f │%n",
            circuit.getSourceVoltage());
        System.out.println("  └──────────────────────────────┴──────────────────┘");
    }

    /**
     * Prints the full component list of the circuit with their impedances.
     *
     * @param frequencyHz Frequency at which to evaluate impedances
     */
    public void printCircuitDiagram(double frequencyHz) {
        List<Component> comps = circuit.getComponents();
        String connType = circuit.getConnectionType().toUpperCase();

        System.out.println("\n  ╔══════════════════════════════════════════════════╗");
        System.out.printf ("  ║  CIRCUIT: %-38s║%n", circuit.getCircuitName());
        System.out.printf ("  ║  Type: %-41s║%n", connType + " CIRCUIT");
        System.out.printf ("  ║  Source: Vs = %-34.2f V  ║%n", circuit.getSourceVoltage());
        System.out.printf ("  ║  Frequency: f = %-31.2f Hz ║%n", frequencyHz);
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf ("  ║  %-47s ║%n", "COMPONENTS:");

        for (int i = 0; i < comps.size(); i++) {
            Component comp = comps.get(i);
            double[] z = comp.getImpedance(frequencyHz);
            double mag = Math.sqrt(z[0]*z[0] + z[1]*z[1]);

            System.out.printf("  ║  [%d] %-44s ║%n", i+1, comp.toString());
            if (mag > 1e15) {
                System.out.printf("  ║      %-44s ║%n", "Z = ∞ (open circuit at this freq)");
            } else {
                String sign = z[1] >= 0 ? "+" : "-";
                System.out.printf("  ║      Z = %.3f %s j%.3f Ω  |Z| = %.3f Ω%n",
                    z[0], sign, Math.abs(z[1]), mag);
            }

            if (i < comps.size() - 1) {
                System.out.println("  ║  " + (isSeries(connType) ? "───┤" : "  ├") + "  (─)");
            }
        }

        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }

    /** Helper: checks if connType is "SERIES" */
    private boolean isSeries(String connType) {
        return connType.equals("SERIES");
    }

    // Getters/setters
    public Circuit getCircuit()             { return circuit; }
    public int getFreqSteps()               { return freqSteps; }
    public void setFreqSteps(int steps)     { this.freqSteps = steps; }
    public double getPrecision()            { return precision; }
    public void setPrecision(double p)      { this.precision = p; }
}
