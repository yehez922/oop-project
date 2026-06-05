package circuit.utils;

/**
 * =============================================================================
 * CLASS: DisplayUtils
 * =============================================================================
 * A utility class with static helper methods for printing formatted output.
 * All methods are STATIC — you don't need to create an instance to use them.
 *
 * This class handles:
 *   - Banner/header printing
 *   - Menu display
 *   - Formatted dividers
 *   - Color-like ASCII formatting (since this is a console app)
 *
 * OOP CONCEPT: Utility class pattern — groups related static methods together.
 * =============================================================================
 */
public class DisplayUtils {

    /** No-arg private constructor prevents instantiation (it's a utility class) */
    private DisplayUtils() {}

    /**
     * Prints the main application banner/title screen.
     */
    public static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                              ║");
        System.out.println("  ║     ██╗   ██╗ ██████╗███████╗    ██████╗ ██████╗ ███████╗   ║");
        System.out.println("  ║      ╚██╗██╔╝██╔════╝██╔════╝   ██╔════╝██╔══██╗██╔════╝   ║");
        System.out.println("  ║       ╚███╔╝ ██║     ███████╗   ██║     ██████╔╝███████╗   ║");
        System.out.println("  ║       ██╔██╗ ██║          ██║   ██║     ██╔══██╗╚════██║   ║");
        System.out.println("  ║      ██╔╝ ██╗╚██████╗███████║   ╚██████╗██║  ██║███████║   ║");
        System.out.println("  ║      ╚═╝  ╚═╝ ╚═════╝╚══════╝    ╚═════╝╚═╝  ╚═╝╚══════╝   ║");
        System.out.println("  ║                                                              ║");
        System.out.println("  ║         VIRTUAL CIRCUIT SIMULATOR & SOLVER                  ║");
        System.out.println("  ║         OOP Project — Java Console Application               ║");
        System.out.println("  ║                                                              ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Prints the main menu options.
     */
    public static void printMainMenu() {
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────┐");
        System.out.println("  │            MAIN MENU                    │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │  [1]  Build a new circuit               │");
        System.out.println("  │  [2]  Analyze existing circuit          │");
        System.out.println("  │  [3]  View circuit library              │");
        System.out.println("  │  [4]  Run frequency sweep analysis      │");
        System.out.println("  │  [5]  Find resonant frequency           │");
        System.out.println("  │  [6]  Demonstrate AES encryption        │");
        System.out.println("  │  [7]  Run all pre-built demo circuits   │");
        System.out.println("  │  [0]  Exit                              │");
        System.out.println("  └─────────────────────────────────────────┘");
        System.out.print("  Enter your choice: ");
    }

    /**
     * Prints the component-type selection sub-menu.
     */
    public static void printComponentMenu() {
        System.out.println();
        System.out.println("  Add which component type?");
        System.out.println("  [1] Resistor   (R, Ohms)");
        System.out.println("  [2] Capacitor  (C, Farads)");
        System.out.println("  [3] Inductor   (L, Henrys)");
        System.out.println("  [0] Done adding / go back");
        System.out.print("  Your choice: ");
    }

    /**
     * Prints a section header with a title.
     * @param title The section title text
     */
    public static void printSectionHeader(String title) {
        int width = 50;
        int padding = (width - title.length() - 2) / 2;
        String pad = " ".repeat(Math.max(0, padding));
        System.out.println();
        System.out.println("  ╔" + "═".repeat(width) + "╗");
        System.out.printf("  ║%s %s %s║%n", pad, title,
            " ".repeat(Math.max(0, width - title.length() - 2 * padding - 2)));
        System.out.println("  ╚" + "═".repeat(width) + "╝");
    }

    /**
     * Prints a simple horizontal divider.
     */
    public static void printDivider() {
        System.out.println("  " + "─".repeat(50));
    }

    /**
     * Prints a confirmation/success message.
     * @param message The message to display
     */
    public static void printSuccess(String message) {
        System.out.println("  ✓  " + message);
    }

    /**
     * Prints an error/warning message.
     * @param message The error message
     */
    public static void printError(String message) {
        System.out.println("  ✗  ERROR: " + message);
    }

    /**
     * Prints an informational note.
     * @param message The info message
     */
    public static void printInfo(String message) {
        System.out.println("  ℹ  " + message);
    }

    /**
     * Prompts the user and reads a double value safely.
     * Shows a hint about the expected unit.
     *
     * @param prompt The prompt to show
     * @param unit   The unit hint (e.g. "Ω", "Hz")
     * @return The double value entered
     */
    public static void printPromptLine(String prompt, String unit) {
        System.out.printf("  → %s [%s]: ", prompt, unit);
    }

    /**
     * Prints the OOP concepts legend for educational purposes.
     */
    public static void printOOPLegend() {
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────────────┐");
        System.out.println("  │             OOP CONCEPTS IN THIS PROJECT              │");
        System.out.println("  ├──────────────────────────────────────────────────────┤");
        System.out.println("  │  ABSTRACTION   : Component (abstract class)           │");
        System.out.println("  │                  Measurable (interface / CAN-DO)      │");
        System.out.println("  │  INHERITANCE   : Resistor, Capacitor, Inductor        │");
        System.out.println("  │                  extend Component (IS-A)              │");
        System.out.println("  │  POLYMORPHISM  : getImpedance() behaves differently   │");
        System.out.println("  │                  per component type (runtime dispatch) │");
        System.out.println("  │                  CircuitSolver.solve() is overloaded  │");
        System.out.println("  │                  (compile-time polymorphism)           │");
        System.out.println("  │  ENCAPSULATION : All fields private, public getters/  │");
        System.out.println("  │                  setters in every class               │");
        System.out.println("  │  CRYPTO        : javax.crypto AES-128 encryption      │");
        System.out.println("  │                  java.security SecureRandom, Cipher   │");
        System.out.println("  └──────────────────────────────────────────────────────┘");
    }
}
