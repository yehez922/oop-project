package circuit.components;

/**
 * =============================================================================
 * CLASS: Resistor
 * =============================================================================
 * A Resistor opposes the flow of current and converts electrical energy
 * to heat. It is the simplest component — its impedance is purely real
 * (it does NOT depend on frequency).
 *
 * FORMULA:  Z = R + 0j   (no imaginary part)
 *           V = I × R    (basic Ohm's Law)
 *
 * OOP CONCEPTS:
 *   - Inheritance   : extends Component (IS-A Component)
 *   - Polymorphism  : overrides getImpedance(), getDescription(),
 *                     calculateVoltageDrop()
 * =============================================================================
 */
public class Resistor extends Component {

    /**
     * Constructor for a Resistor.
     * Calls the parent constructor with the correct unit "Ω" (Ohms).
     *
     * @param name          Label for this resistor (e.g. "R1")
     * @param resistanceOhm Resistance in Ohms (must be > 0 for a real resistor)
     */
    public Resistor(String name, double resistanceOhm) {
        super(name, resistanceOhm, "Ω");  // Calls Component's constructor
    }

    /**
     * POLYMORPHISM: Overrides the abstract method from Component.
     *
     * A resistor's impedance is always just R, regardless of frequency.
     * The imaginary part is always 0.
     *
     * @param frequencyHz Frequency in Hz (IGNORED for a resistor)
     * @return double[] { R, 0 }  — [real part, imaginary part]
     */
    @Override
    public double[] getImpedance(double frequencyHz) {
        // A resistor has NO reactive (imaginary) part — it's purely resistive.
        return new double[]{ getValue(), 0.0 };
    }

    /**
     * POLYMORPHISM: Overrides getDescription() from Component.
     * Describes the electrical behavior of a resistor.
     */
    @Override
    public String getDescription() {
        return String.format("Resistor | Z = %.4f Ω (purely resistive, frequency-independent)", getValue());
    }

    /**
     * POLYMORPHISM: Overrides calculateVoltageDrop() from Component.
     * Uses simple Ohm's Law: V = I × R
     *
     * @param currentAmps The current through the resistor in Amperes
     * @param frequencyHz Frequency (ignored for resistor)
     * @return Voltage drop in Volts
     */
    @Override
    public double calculateVoltageDrop(double currentAmps, double frequencyHz) {
        // V = I × R  (classic Ohm's Law)
        return currentAmps * getValue();
    }

    /**
     * Calculates the power dissipated by this resistor.
     * Formula: P = I² × R
     *
     * @param currentAmps Current in Amperes
     * @return Power in Watts
     */
    public double calculatePower(double currentAmps) {
        return currentAmps * currentAmps * getValue();
    }
}
