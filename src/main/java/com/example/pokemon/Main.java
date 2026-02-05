package com.example.pokemon;

import com.example.pokemon.app.ConsoleApp;

/**
 * Main launcher class.
 * - installs a shutdown hook to clean up JDBC drivers and MySQL background thread
 * - delegates to the ConsoleApp to run the console UI
 */
public class Main {
    public static void main(String[] args) {
        installCleanupHook();

        // Delegate to the ConsoleApp console frontend
        ConsoleApp.main(args);
    }

    private static void installCleanupHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Running shutdown cleanup...");

            // Deregister JDBC drivers registered with DriverManager
            try {
                java.util.Enumeration<java.sql.Driver> drivers = java.sql.DriverManager.getDrivers();
                while (drivers.hasMoreElements()) {
                    java.sql.Driver driver = drivers.nextElement();
                    try {
                        java.sql.DriverManager.deregisterDriver(driver);
                        // System.out.println("Deregistered driver: " + driver);
                    } catch (java.sql.SQLException se) {
                        // ignore
                    }
                }
            } catch (Throwable t) {
                // ignore any problems deregistering drivers
            }

            // Try to shut down MySQL's AbandonedConnectionCleanupThread (Connector/J)
            try {
                Class<?> cleanupClass = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
                try {
                    // newer Connector/J provides checkedShutdown()
                    java.lang.reflect.Method m = cleanupClass.getMethod("checkedShutdown");
                    m.invoke(null);
                } catch (NoSuchMethodException ns) {
                    // fallback to shutdown()
                    try {
                        java.lang.reflect.Method m2 = cleanupClass.getMethod("shutdown");
                        m2.invoke(null);
                    } catch (NoSuchMethodException ns2) {
                        // nothing we can do
                    }
                }
            } catch (ClassNotFoundException cnf) {
                // cleanup class not present; that's okay (older/newer drivers)
            } catch (Throwable t) {
                // ignore any reflection/invocation problems
            }

            System.out.println("Shutdown cleanup finished.");
        }));
    }
}
