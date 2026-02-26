package com.example.pokemon.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public final class DBUtil {
    private static final String URL;
    private static final String USER;
    private static final String PASS;

    static {
        try (InputStream in = DBUtil.class.getResourceAsStream("/db.properties")) {
            if (in == null) throw new RuntimeException("db.properties not found in classpath (src/main/resources/db.properties)");
            Properties p = new Properties();
            p.load(in);
            URL = p.getProperty("db.url");
            USER = p.getProperty("db.user");
            PASS = p.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Cannot load DB config", e);
        }
    }

    private DBUtil() {}

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}