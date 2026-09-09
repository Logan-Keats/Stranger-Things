package com.strangerthings;

/**
 * JVM entry point for IntelliJ Run.
 * Do not extend {@link javafx.application.Application} here — that triggers
 * "JavaFX runtime components are missing" when JavaFX is only on the classpath.
 * {@link Main} still extends Application and starts the UI.
 */
public class Launcher {

    public static void main(String[] args) {
        Main.main(args);
    }
}
