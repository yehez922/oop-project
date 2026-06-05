package circuit.components;

/**
 * =============================================================================
 * CLASS: Capacitor
 * =============================================================================
 * A Capacitor stores electric charge (energy in an electric field).
 * Its impedance is PURELY IMAGINARY and NEGATIVE, and it DECREASES with
 * increasing frequency (capacitors block DC and pass high-frequency AC).
 *
 * FORMULA:  Z = 0 - j/(ωC)   where ω = 2π × f
 *           At DC (f=0): Z = ∞ (open circuit — blocks DC completely)
 *           At high freq: Z → 0 (short circuit — passes AC freely)
 *
 * OOP CONCEPTS:
 *   - Inheritance   : extends Component (IS-A Component)
 *   - Polymorphism  : overrides all three abstract methods
 * =============================================================================
 */
public class Capacitor extends Component {

    /**
     * Constructor for a Capacitor.
     * Calls the parent constructor with unit "F" (Farads).
     *
     * @param name         Label (e.g. "C1")
     * @param capacitancef Capacitance in Farads
     */
    public Capacitor(String name, double capacitancef) {
        super(name, capacitancef, "F");
    }

    /**
     * POLYMORPHISM: Overrides getImpedance() from Component.
     *
     * Capacitive impedance formula: Xc = -1 / (2π × f × C)
     * The real part is always 0; the imaginary part is negative.
     *
     * Special case: at DC (f = 0), capacitor is an OPEN CIRCUIT (Z = ∞).
     * We return a very large number to represent this.
     *
     * @param frequencyHz Frequency in Hertz
     * @return double[] { 0, Xc }  — [real=0, imaginary=negative]
     */
    @Override
    public double[] getImpedance(double frequencyHz) {
        if (frequencyHz == 0) {
            // DC case: Capacitor is an open circuit (infinite impedance)
            // We use Double.MAX_VALUE to represent "infinite" resistance to DC
            return new double[]{ 0.0, Double.MAX_VALUE }; // conceptually open
        }
        // Angular frequency: ω = 2π × f
        double omega = 2 * Math.PI * frequencyHz;
        // Capacitive reactance: Xc = -1 / (ω × C)
        // The NEGATIVE sign indicates energy is stored, not dissipated.
        double xc = -1.0 / (omega * getValue());
        return new double[]{ 0.0, xc };
    }

    /**
     * POLYMORPHISM: Overrides getDescription() from Component.
     */
    @Override
    public String getDescription() {
        return String.format("Capacitor | Z = -j/(ωC), C = %.6f F (blocks DC, passes high-freq AC)", getValue());
    }

    /**
     * POLYMORPHISM: Overrides calculateVoltageDrop() from Component.
     * |V| = |I| × |Xc|  where Xc = 1/(2π×f×C)
     *
     * @param currentAmps Peak current in Amperes
     * @param frequencyHz Frequency in Hertz
     * @return Magnitude of voltage drop in Volts
     */
    @Override
    public double calculateVoltageDrop(double currentAmps, double frequencyHz) {
        if (frequencyHz == 0) {
            // At DC, a capacitor blocks all current; infinite voltage drop concept
            return Double.MAX_VALUE;
        }
        double omega = 2 * Math.PI * frequencyHz;
        double xc = 1.0 / (omega * getValue()); // magnitude of Xc (always positive)
        return currentAmps * xc;
    }

    /**
     * Calculates the energy stored in this capacitor.
     * Formula: E = ½ × C × V²
     *
     * @param voltageVolts Voltage across the capacitor
     * @return Stored energy in Joules
     */
    public double calculateStoredEnergy(double voltageVolts) {
        return 0.5 * getValue() * voltageVolts * voltageVolts;
    }
}
