package circuit.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * =============================================================================
 * CLASS: CircuitLibrary
 * =============================================================================
 * A registry (library) of saved/named circuits.
 * Users can save their built circuits here and retrieve them by name.
 *
 * Uses Java's built-in HashMap as a key-value store:
 *   Key   = circuit name (String)
 *   Value = Circuit object
 *
 * OOP CONCEPTS:
 *   - Encapsulation : Private HashMap, accessed through public methods
 *   - Type safety   : Generics <String, Circuit> for the HashMap
 * =============================================================================
 */
public class CircuitLibrary {

    // -------------------------------------------------------------------------
    // ENCAPSULATION: The internal map is private
    // -------------------------------------------------------------------------
    private Map<String, Circuit> library;
    private String libraryName;

    /**
     * Constructor
     * @param libraryName A label for this library (e.g. "My Circuit Library")
     */
    public CircuitLibrary(String libraryName) {
        this.libraryName = libraryName;
        this.library = new HashMap<>(); // Empty library to start
    }

    /**
     * Saves a circuit to the library.
     * If a circuit with the same name already exists, it is REPLACED.
     *
     * @param circuit The circuit to save
     */
    public void saveCircuit(Circuit circuit) {
        library.put(circuit.getCircuitName(), circuit);
        System.out.println("  [Library] Saved circuit: \"" + circuit.getCircuitName() + "\"");
    }

    /**
     * Retrieves a circuit by its name.
     *
     * @param name The circuit name to look up
     * @return The Circuit, or null if not found
     */
    public Circuit getCircuit(String name) {
        return library.get(name);
    }

    /**
     * Removes a circuit from the library by name.
     *
     * @param name The circuit name to remove
     * @return true if it was found and removed
     */
    public boolean removeCircuit(String name) {
        return library.remove(name) != null;
    }

    /**
     * Lists all circuit names currently in the library.
     *
     * @return A set of all circuit names
     */
    public Set<String> listCircuitNames() {
        return library.keySet();
    }

    /**
     * Returns the number of circuits in the library.
     */
    public int size() {
        return library.size();
    }

    /**
     * Prints a formatted summary of all circuits in the library.
     */
    public void printLibrarySummary() {
        System.out.println("\n  ╔═══════════════════════════════════════════╗");
        System.out.printf("  ║  LIBRARY: %-32s ║%n", libraryName);
        System.out.printf("  ║  Saved circuits: %-25d ║%n", library.size());
        System.out.println("  ╠═══════════════════════════════════════════╣");

        if (library.isEmpty()) {
            System.out.println("  ║  (No circuits saved yet)                  ║");
        } else {
            int i = 1;
            for (Map.Entry<String, Circuit> entry : library.entrySet()) {
                Circuit c = entry.getValue();
                System.out.printf("  ║  [%d] %-37s ║%n", i++, c.toString());
            }
        }
        System.out.println("  ╚═══════════════════════════════════════════╝");
    }

    public String getLibraryName() { return libraryName; }
}
