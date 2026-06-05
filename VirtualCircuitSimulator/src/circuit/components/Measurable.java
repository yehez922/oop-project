package circuit.components;

/**
 * =============================================================================
 * INTERFACE: Measurable
 * =============================================================================
 * An INTERFACE models a "CAN-DO" (capability) relationship.
 * (As opposed to "IS-A" which is modeled with inheritance/extends)
 *
 * WHY THIS INTERFACE?
 *   Not all objects in a circuit are "Components", but some can be measured.
 *   This interface defines the contract: "If you implement Measurable,
 *   you MUST be able to provide a magnitude and a phase angle."
 *
 * OOP CONCEPT: ABSTRACTION via interface (system contract)
 *
 * Classes implementing Measurable:
 *   - Circuit (a full circuit can report its total impedance measurements)
 * =============================================================================
 */
public interface Measurable {

    /**
     * Returns the magnitude (absolute value) of the impedance.
     * For complex impedance Z = R + jX, magnitude = √(R² + X²)
     *
     * @param frequencyHz Operating frequency in Hertz
     * @return Impedance magnitude in Ohms
     */
    double getMagnitude(double frequencyHz);

    /**
     * Returns the phase angle between voltage and current.
     * For complex impedance Z = R + jX, phase = arctan(X / R)
     * - Positive angle → current lags voltage (inductive dominant)
     * - Negative angle → current leads voltage (capacitive dominant)
     * - Zero angle     → purely resistive (resonance)
     *
     * @param frequencyHz Operating frequency in Hertz
     * @return Phase angle in degrees
     */
    double getPhaseAngleDegrees(double frequencyHz);

    /**
     * Provides a formatted measurement report string.
     *
     * @param frequencyHz Operating frequency in Hertz
     * @return Human-readable measurement summary
     */
    String getMeasurementReport(double frequencyHz);
}
