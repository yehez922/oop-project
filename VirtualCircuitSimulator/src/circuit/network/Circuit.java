package circuit.network;

import circuit.components.Component;
import circuit.components.Measurable;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * CLASS: Circuit
 * =============================================================================
 * Represents a complete electrical circuit that can hold multiple components
 * arranged in either SERIES or PARALLEL configuration.
 *
 * This class IMPLEMENTS the Measurable interface (CAN-DO: it CAN be measured).
 *
 * KEY CIRCUIT MATH:
 *   SERIES  : Z_total = Z1 + Z2 + Z3 + ...    (impedances ADD up)
 *   PARALLEL: 1/Z_total = 1/Z1 + 1/Z2 + ...   (reciprocals add, then invert)
 *
 * OOP CONCEPTS:
 *   - Encapsulation : Private list of components; accessed via public methods
 *   - Abstraction   : Implements Measurable interface contract
 *   - Polymorphism  : Uses Component references (could be Resistor/Capacitor/
 *                     Inductor) — the right getImpedance() is called at runtime
 * =============================================================================
 */
public class Circuit implements Measurable {

    // -------------------------------------------------------------------------
    // ENCAPSULATION: The internal list of components is private.
    // Nobody outside this class can directly manipulate the component list.
    // -------------------------------------------------------------------------
    private String circuitName;           // Name label for this circuit
    private List<Component> components;   // Holds all added components
    private boolean isSeries;             // true = series, false = parallel
    private double sourceVoltage;         // Voltage source applied (Volts)

    /**
     * Circuit types — enumeration for clarity
     */
    public static final boolean SERIES   = true;
    public static final boolean PARALLEL = false;

    /**
     * Constructor — creates an empty circuit.
     *
     * @param name          Label for this circuit (e.g. "RLC Series Circuit")
     * @param isSeries      true = series connection, false = parallel
     * @param sourceVoltage Voltage of the source driving this circuit (Volts)
     */
    public Circuit(String name, boolean isSeries, double sourceVoltage) {
        this.circuitName   = name;
        this.isSeries      = isSeries;
        this.sourceVoltage = sourceVoltage;
        this.components    = new ArrayList<>(); // Start with empty component list
    }

    // =========================================================================
    // PUBLIC METHODS — Adding/Removing Components
    // =========================================================================

    /**
     * Adds a component to this circuit.
     * POLYMORPHISM in action: The parameter type is Component (abstract),
     * but we can pass any subclass: Resistor, Capacitor, or Inductor.
     *
     * @param component The component to add (Resistor, Capacitor, or Inductor)
     */
    public void addComponent(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("Cannot add a null component!");
        }
        components.add(component);
        System.out.println("  ✓ Added: " + component.toString() + " to [" + circuitName + "]");
    }

    /**
     * Removes a component by its name/label.
     *
     * @param name The name of the component to remove (e.g. "R1")
     * @return true if found and removed, false if not found
     */
    public boolean removeComponent(String name) {
        return components.removeIf(c -> c.getName().equalsIgnoreCase(name));
    }

    /**
     * Returns the number of components currently in this circuit.
     */
    public int getComponentCount() {
        return components.size();
    }

    /**
     * Returns a copy of the components list (defensive copy for encapsulation).
     * We return a copy so external code can't modify our internal list directly.
     */
    public List<Component> getComponents() {
        return new ArrayList<>(components); // Return a COPY, not the original
    }

    // =========================================================================
    // CORE CIRCUIT CALCULATIONS
    // =========================================================================

    /**
     * Calculates the TOTAL complex impedance of the circuit.
     * Uses POLYMORPHISM: calls component.getImpedance() on each component,
     * and Java automatically calls the correct subclass version at runtime.
     *
     * SERIES FORMULA:    Z_total = ΣZi   (add all impedances)
     * PARALLEL FORMULA:  Z_total = 1 / Σ(1/Zi)
     *
     * @param frequencyHz Operating frequency in Hertz
     * @return double[] { totalReal, totalImaginary }
     */
    public double[] getTotalImpedance(double frequencyHz) {
        if (components.isEmpty()) {
            return new double[]{ 0.0, 0.0 };
        }

        if (isSeries) {
            return calculateSeriesImpedance(frequencyHz);
        } else {
            return calculateParallelImpedance(frequencyHz);
        }
    }

    /**
     * SERIES: Simply add all real parts and all imaginary parts separately.
     * Z_series = (R1+R2+...) + j(X1+X2+...)
     */
    private double[] calculateSeriesImpedance(double frequencyHz) {
        double totalReal      = 0.0;
        double totalImaginary = 0.0;

        for (Component comp : components) {
            // POLYMORPHISM: comp could be Resistor, Capacitor, or Inductor.
            // Java calls the correct getImpedance() based on actual type.
            double[] z = comp.getImpedance(frequencyHz);
            totalReal      += z[0]; // Add real parts
            totalImaginary += z[1]; // Add imaginary parts
        }

        return new double[]{ totalReal, totalImaginary };
    }

    /**
     * PARALLEL: Use the admittance (Y = 1/Z) summation method.
     * Y_total = ΣYi, then Z_total = 1/Y_total
     * This uses complex number division to handle the reciprocal correctly.
     */
    private double[] calculateParallelImpedance(double frequencyHz) {
        double totalAdmittanceReal = 0.0; // Real part of total admittance Y
        double totalAdmittanceImag = 0.0; // Imaginary part of total admittance Y

        for (Component comp : components) {
            double[] z = comp.getImpedance(frequencyHz);
            double real = z[0];
            double imag = z[1];

            // Magnitude squared: |Z|² = R² + X²
            double magSquared = real * real + imag * imag;

            if (magSquared < 1e-15) {
                // Component has near-zero impedance (short circuit) → huge admittance
                // The parallel combination would be zero impedance
                return new double[]{ 0.0, 0.0 };
            }

            // Y = 1/Z = (R - jX) / (R² + X²)  [complex conjugate division]
            totalAdmittanceReal += real / magSquared;
            totalAdmittanceImag += (-imag) / magSquared;
        }

        // Z_total = 1 / Y_total
        double yMagSq = totalAdmittanceReal * totalAdmittanceReal
                      + totalAdmittanceImag * totalAdmittanceImag;

        if (yMagSq < 1e-15) {
            return new double[]{ Double.MAX_VALUE, 0.0 }; // Open circuit
        }

        double totalReal = totalAdmittanceReal / yMagSq;
        double totalImag = (-totalAdmittanceImag) / yMagSq;

        return new double[]{ totalReal, totalImag };
    }

    /**
     * Calculates the total current flowing in the circuit.
     * Using Ohm's Law: I = V / Z
     *
     * For complex impedance: |I| = |V| / |Z|
     *
     * @param frequencyHz Frequency in Hertz
     * @return Current magnitude in Amperes
     */
    public double calculateCurrent(double frequencyHz) {
        double magnitude = getMagnitude(frequencyHz);
        if (magnitude < 1e-15) {
            return Double.MAX_VALUE; // Short circuit → theoretically infinite current
        }
        return sourceVoltage / magnitude; // I = V / |Z|
    }

    // =========================================================================
    // MEASURABLE INTERFACE IMPLEMENTATION (CAN-DO contract)
    // =========================================================================

    /**
     * INTERFACE IMPLEMENTATION: getMagnitude()
     * |Z| = √(R² + X²)
     */
    @Override
    public double getMagnitude(double frequencyHz) {
        double[] z = getTotalImpedance(frequencyHz);
        return Math.sqrt(z[0] * z[0] + z[1] * z[1]);
    }

    /**
     * INTERFACE IMPLEMENTATION: getPhaseAngleDegrees()
     * φ = arctan(X / R) × (180/π)
     * Positive = inductive (current LAGS), Negative = capacitive (current LEADS)
     */
    @Override
    public double getPhaseAngleDegrees(double frequencyHz) {
        double[] z = getTotalImpedance(frequencyHz);
        return Math.toDegrees(Math.atan2(z[1], z[0])); // atan2(imaginary, real)
    }

    /**
     * INTERFACE IMPLEMENTATION: getMeasurementReport()
     * Formats a complete measurement display for the user.
     */
    @Override
    public String getMeasurementReport(double frequencyHz) {
        double[] z       = getTotalImpedance(frequencyHz);
        double magnitude = getMagnitude(frequencyHz);
        double phase     = getPhaseAngleDegrees(frequencyHz);
        double current   = calculateCurrent(frequencyHz);

        // Determine the sign character for the imaginary part display
        String imagSign  = (z[1] >= 0) ? "+" : "-";
        double absImag   = Math.abs(z[1]);

        // Determine circuit behavior based on phase angle
        String behavior;
        if (Math.abs(phase) < 0.01) {
            behavior = "RESONANCE — purely resistive (XL = XC)";
        } else if (phase > 0) {
            behavior = "INDUCTIVE — current LAGS voltage by " + String.format("%.2f°", phase);
        } else {
            behavior = "CAPACITIVE — current LEADS voltage by " + String.format("%.2f°", Math.abs(phase));
        }

        return String.format(
            "\n  ┌─────────────────────────────────────────────┐\n" +
            "  │  MEASUREMENT REPORT: %-24s│\n" +
            "  ├─────────────────────────────────────────────┤\n" +
            "  │  Frequency    : %10.2f Hz              │\n" +
            "  │  Source Volt. : %10.2f V               │\n" +
            "  │  Z (complex)  : %8.4f %s %8.4f j Ω│\n" +
            "  │  |Z| (mag.)   : %10.4f Ω              │\n" +
            "  │  Phase angle  : %10.4f °              │\n" +
            "  │  Current |I|  : %10.6f A              │\n" +
            "  ├─────────────────────────────────────────────┤\n" +
            "  │  Behavior: %-33s│\n" +
            "  └─────────────────────────────────────────────┘",
            circuitName,
            frequencyHz, sourceVoltage,
            z[0], imagSign, absImag,
            magnitude,
            phase,
            current,
            behavior
        );
    }

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================

    public String getCircuitName()            { return circuitName; }
    public boolean isSeries()                 { return isSeries; }
    public double getSourceVoltage()          { return sourceVoltage; }
    public String getConnectionType()         { return isSeries ? "Series" : "Parallel"; }

    public void setSourceVoltage(double v)    { this.sourceVoltage = v; }
    public void setCircuitName(String name)   { this.circuitName = name; }

    @Override
    public String toString() {
        return String.format("Circuit[%s | %s | %d components | Vs=%.2fV]",
            circuitName, getConnectionType(), components.size(), sourceVoltage);
    }
}
