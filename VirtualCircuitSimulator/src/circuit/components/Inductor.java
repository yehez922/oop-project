package circuit.components;

/**
 * =============================================================================
 * CLASS: Inductor
 * =============================================================================
 * An Inductor stores energy in a magnetic field when current flows through it.
 * Its impedance is PURELY IMAGINARY and POSITIVE, and it INCREASES with
 * increasing frequency (inductors pass DC freely and block high-frequency AC).
 *
 * FORMULA:  Z = 0 + j×(ωL)   where ω = 2π × f
 *           At DC (f=0): Z = 0 (short circuit — wire, no opposition)
 *           At high freq: Z grows large (blocks high-frequency signals)
 *
 * OOP CONCEPTS:
 *   - Inheritance  : extends Component (IS-A Component)
 *   - Polymorphism : overrides all three abstract methods
 * =============================================================================
 */
public class Inductor extends Component {

    /**
     * Constructor for an Inductor.
     * Calls the parent constructor with unit "H" (Henrys).
     *
     * @param name          Label (e.g. "L1")
     * @param inductanceH   Inductance in Henrys
     */
    public Inductor(String name, double inductanceH) {
        super(name, inductanceH, "H");
    }

    /**
     * POLYMORPHISM: Overrides getImpedance() from Component.
     *
     * Inductive impedance formula: XL = 2π × f × L
     * The real part is always 0; the imaginary part is POSITIVE.
     * At DC, XL = 0 (behaves like a plain wire).
     *
     * @param frequencyHz Frequency in Hertz
     * @return double[] { 0, XL }  — [real=0, imaginary=positive]
     */
    @Override
    public double[] getImpedance(double frequencyHz) {
        // Angular frequency: ω = 2π × f
        double omega = 2 * Math.PI * frequencyHz;
        // Inductive reactance: XL = ω × L
        // Positive imaginary — opposes CHANGES in current
        double xl = omega * getValue();
        return new double[]{ 0.0, xl };
    }

    /**
     * POLYMORPHISM: Overrides getDescription() from Component.
     */
    @Override
    public String getDescription() {
        return String.format("Inductor  | Z = +j(ωL), L = %.6f H (passes DC, blocks high-freq AC)", getValue());
    }

    /**
     * POLYMORPHISM: Overrides calculateVoltageDrop() from Component.
     * |V| = |I| × XL  where XL = 2π×f×L
     *
     * @param currentAmps Peak current in Amperes
     * @param frequencyHz Frequency in Hertz
     * @return Magnitude of voltage drop in Volts
     */
    @Override
    public double calculateVoltageDrop(double currentAmps, double frequencyHz) {
        double omega = 2 * Math.PI * frequencyHz;
        double xl = omega * getValue(); // XL = ω × L
        return currentAmps * xl;
    }

    /**
     * Calculates the energy stored in this inductor's magnetic field.
     * Formula: E = ½ × L × I²
     *
     * @param currentAmps Current flowing through in Amperes
     * @return Stored energy in Joules
     */
    public double calculateStoredEnergy(double currentAmps) {
        return 0.5 * getValue() * currentAmps * currentAmps;
    }

    /**
     * Returns the resonant frequency with a given capacitor.
     * At resonance, XL = XC, and the circuit has minimum impedance.
     * Formula: f_res = 1 / (2π × √(LC))
     *
     * @param capacitanceFarads Capacitance in Farads
     * @return Resonant frequency in Hertz
     */
    public double getResonantFrequency(double capacitanceFarads) {
        return 1.0 / (2 * Math.PI * Math.sqrt(getValue() * capacitanceFarads));
    }
}
