package main;

/**
 * Standard Java implementation af Main uden preview-features.
 */
public class Main {
    // Standard entry point kræver String[] args og public access
    public static void main(String[] args) {

        // System.out er en PrintStream instans. Det er standarden for konsol-output.
        System.out.println("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            // Vi kalder metoden på PrintStream objektet direkte
            System.out.println("i = " + i);
        }
    }
}