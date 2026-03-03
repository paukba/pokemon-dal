package com.example.pokemon.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * DBUtil reads DB configuration from environment variables if present:
 *
 * - DB_URL
 * - DB_USER
 * - DB_PASSWORD
 *
 * If env vars are not present, it falls back to src/main/resources/db.properties
 * which must contain keys: db.url, db.user, db.password
 *
 * This makes the app easy to run locally (use db.properties) and easy to host
 * on platforms like Railway/Heroku (set env vars).
 */
public class DBUtil {
    private static final String ENV_URL = "DB_URL";
    private static final String ENV_USER = "DB_USER";
    private static final String ENV_PASSWORD = "DB_PASSWORD";

    private static final Properties fileProps = new Properties();
    static {
        // load properties file if available
        try (InputStream in = DBUtil.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                fileProps.load(in);
            }
        } catch (Exception ex) {
            System.err.println("Warning: unable to load db.properties from classpath: " + ex.getMessage());
        }

        // Attempt to load JDBC driver (MySQL)
        try {
            // modern drivers register themselves; this is just defensive
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Throwable t) {
            // not fatal — driver may auto-register
        }
    }

    public static Connection getConnection() throws Exception {
        String url = System.getenv(ENV_URL);
        String user = System.getenv(ENV_USER);
        String pass = System.getenv(ENV_PASSWORD);

        if (isEmpty(url)) {
            url = fileProps.getProperty("db.url");
        }
        if (isEmpty(user)) {
            user = fileProps.getProperty("db.user");
        }
        if (isEmpty(pass)) {
            pass = fileProps.getProperty("db.password");
        }

        if (isEmpty(url)) {
            throw new IllegalStateException("Database URL is not configured. Set DB_URL env var or provide src/main/resources/db.properties");
        }

        // Provide simple log so hosting environment can show connection info (without password)
        System.out.println("DB connecting to: " + url + " (user=" + (user==null? "null": user) + ")");

        if (user == null) return DriverManager.getConnection(url);
        return DriverManager.getConnection(url, user, pass);
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}