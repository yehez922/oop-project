package circuit.components;

/**
 * =============================================================================
 * ABSTRACT CLASS: Component
 * =============================================================================
 * This is the ROOT of our entire class hierarchy (Abstraction + Inheritance).
 *
 * WHY ABSTRACT?
 *   A "Component" by itself is too vague to place in a real circuit.
 *   You can't just say "add a Component" — you need a Resistor, Capacitor,
 *   or Inductor. Abstract classes FORCE subclasses to provide real definitions
 *   for the abstract methods.
 *
 * OOP CONCEPTS DEMONSTRATED HERE:
 *   - Abstraction   : Abstract class with abstract methods (no body).
 *   - Encapsulation : Private fields; access only through getters/setters.
 *   - Inheritance   : Resistor, Capacitor, Inductor all extend this class.
 *   - Polymorphism  : getImpedance() behaves differently per subclass.
 * =============================================================================
 */
public abstract class Component {

    // -------------------------------------------------------------------------
    // ENCAPSULATION: All fields are private.
    // They can ONLY be read or changed through the public getter/setter methods.
    // This protects data from accidental modification from outside the class.
    // -------------------------------------------------------------------------
    private String name;          // Human-readable label, e.g. "R1", "C1"
    private double value;         // The numeric value: Ohms, Farads, or Henrys
    private String unit;          // Unit label: "Ω", "F", or "H"

    /**
     * Constructor — called by every subclass via super(name, value, unit).
     * Validates that the component value is not negative.
     *
     * @param name  Label for this component (e.g. "R1")
     * @param value Numeric value (must be >= 0)
     * @param unit  String unit label (e.g. "Ω")
     */
    public Component(String name, double value, String unit) {
        if (value < 0) {
            throw new IllegalArgumentException(
                "Component value cannot be negative! Received: " + value
            );
        }
        this.name  = name;
        this.value = value;
        this.unit  = unit;
    }

    // =========================================================================
    // ABSTRACT METHODS — Every subclass MUST override these.
    // They have no body here; the subclass provides the real implementation.
    // =========================================================================

    /**
     * Returns the complex impedance of this component at a given frequency.
     * - Resistor   : Z = R         (purely real, no imaginary part)
     * - Capacitor  : Z = 1/(jωC)   (purely imaginary, negative)
     * - Inductor   : Z = jωL       (purely imaginary, positive)
     *
     * @param frequencyHz The AC frequency in Hertz (0 = DC)
     * @return A double array [real, imaginary] representing complex impedance
     */
    public abstract double[] getImpedance(double frequencyHz);

    /**
     * Returns a human-readable description of what this component does.
     * Each subclass gives its own answer (Polymorphism in action).
     *
     * @return A string describing the component's electrical behavior
     */
    public abstract String getDescription();

    /**
     * Calculates the voltage drop across this component.
     * Formula: V = I × Z  (Ohm's Law in complex form)
     *
     * @param currentAmps    The current flowing through (in Amperes)
     * @param frequencyHz    The frequency of the signal (in Hertz)
     * @return Voltage drop magnitude in Volts
     */
    public abstract double calculateVoltageDrop(double currentAmps, double frequencyHz);

    // =========================================================================
    // CONCRETE METHODS — Shared by ALL subclasses (no override needed).
    // =========================================================================

    /**
     * Returns a formatted summary string for this component.
     * Example output: "R1 [Resistor] = 100.00 Ω"
     */
    public String getSummary() {
        return String.format("%-5s = %10.4f %s   | %s",
            name, value, unit, getDescription());
    }

    // =========================================================================
    // GETTERS & SETTERS — Public access to private fields (Encapsulation)
    // =========================================================================

    /** @return The name/label of this component (e.g. "R1") */
    public String getName() { return name; }

    /** @return The numeric value (Ohms, Farads, or Henrys) */
    public double getValue() { return value; }

    /** @return The unit string (e.g. "Ω", "F", "H") */
    public String getUnit() { return unit; }

    /**
     * Updates the component value. Validates it is non-negative.
     * @param value New value to set
     */
    public void setValue(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be >= 0, got: " + value);
        }
        this.value = value;
    }

    /** Updates the name/label */
    public void setName(String name) { this.name = name; }

    /**
     * Standard toString for printing the component in a simple format.
     */
    @Override
    public String toString() {
        return String.format("%s (%.4f %s)", name, value, unit);
    }
}
