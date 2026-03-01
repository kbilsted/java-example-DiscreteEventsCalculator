package main;

/**
 * Standard Java implementation of Main without preview features.
 */
public class Main {
    // Standard entry point requires String[] args and public access
    public static void main(String[] args) {

        // System.out is a PrintStream instance. It is the standard for console output.
        System.out.println("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            // We call the method on the PrintStream object directly
            System.out.println("i = " + i);
        }
    }
}
