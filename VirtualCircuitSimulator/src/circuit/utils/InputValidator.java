package circuit.utils;

import java.util.Scanner;

/**
 * =============================================================================
 * CLASS: InputValidator
 * =============================================================================
 * Handles all user input reading with proper validation and error handling.
 * All methods are static helper utilities.
 *
 * WHY SEPARATE CLASS?
 *   Good OOP practice = "Single Responsibility Principle".
 *   This class is ONLY responsible for reading and validating user input.
 *   It keeps the main program clean and focused.
 *
 * OOP CONCEPT: Encapsulation of input logic; static utility methods.
 * =============================================================================
 */
public class InputValidator {

    /** Shared scanner — reused across all input reads */
    private static final Scanner scanner = new Scanner(System.in);

    /** Private constructor — utility class should not be instantiated */
    private InputValidator() {}

    /**
     * Reads an integer from the user.
     * Keeps asking until a valid integer is entered.
     *
     * @return A valid integer from the user
     */
    public static int readInt() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("  Please enter a valid number: ");
            }
        }
    }

    /**
     * Reads a positive double value from the user.
     * Validates: must be a number AND must be > 0.
     *
     * @param fieldName Name of the value being entered (for error messages)
     * @return A valid positive double
     */
    public static double readPositiveDouble(String fieldName) {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                double value = Double.parseDouble(line);
                if (value <= 0) {
                    System.out.printf("  %s must be greater than 0. Try again: ", fieldName);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.printf("  Invalid number for %s. Try again: ", fieldName);
            }
        }
    }

    /**
     * Reads a non-negative double value (0 is allowed, negatives are not).
     *
     * @param fieldName Name of the value being entered
     * @return A valid non-negative double
     */
    public static double readNonNegativeDouble(String fieldName) {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                double value = Double.parseDouble(line);
                if (value < 0) {
                    System.out.printf("  %s cannot be negative. Try again: ", fieldName);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.printf("  Invalid number for %s. Try again: ", fieldName);
            }
        }
    }

    /**
     * Reads a non-empty string from the user.
     *
     * @return A non-empty trimmed string
     */
    public static String readString() {
        while (true) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.print("  Please enter a non-empty value: ");
        }
    }

    /**
     * Reads a yes/no answer from the user.
     * Accepts: y, yes, n, no (case insensitive)
     *
     * @param prompt The question to ask
     * @return true for yes, false for no
     */
    public static boolean readYesNo(String prompt) {
        System.out.print("  " + prompt + " (y/n): ");
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no"))  return false;
            System.out.print("  Please type 'y' or 'n': ");
        }
    }

    /**
     * Closes the scanner when done.
     * Should be called when the application exits.
     */
    public static void close() {
        scanner.close();
    }
}
