package com.example.pokemon;

import com.example.pokemon.app.ConsoleApp;

public class Main {
    public static void main(String[] args) {
        installCleanupHook();
        ConsoleApp.main(args);
    }

    private static void installCleanupHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Running shutdown cleanup...");

            try {
                java.util.Enumeration<java.sql.Driver> drivers = java.sql.DriverManager.getDrivers();
                while (drivers.hasMoreElements()) {
                    java.sql.Driver driver = drivers.nextElement();
                    try { java.sql.DriverManager.deregisterDriver(driver); } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}

            try {
                Class<?> cleanupClass = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
                try {
                    java.lang.reflect.Method m = cleanupClass.getMethod("checkedShutdown");
                    m.invoke(null);
                } catch (NoSuchMethodException ns) {
                    try {
                        java.lang.reflect.Method m2 = cleanupClass.getMethod("shutdown");
                        m2.invoke(null);
                    } catch (NoSuchMethodException ns2) { /* ignore */ }
                }
            } catch (ClassNotFoundException cnf) {
                // not present - fine
            } catch (Throwable ignored) {}

            System.out.println("Shutdown cleanup finished.");
        }));
    }
}