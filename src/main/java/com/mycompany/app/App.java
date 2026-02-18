package com.mycompany.app;

/**
 * Main application class.
 */
public class App {

    /**
     * Returns the greeting message.
     *
     * @return message string
     */
    public String getMessage() {
        return "Hello World!";
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        System.out.println(new App().getMessage());
    }
}
