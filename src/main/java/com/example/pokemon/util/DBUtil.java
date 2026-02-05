package com.example.pokemon.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {
    private static final String PROPS_FILE = "/db.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream in = DBUtil.class.getResourceAsStream(PROPS_FILE)) {
            Properties p = new Properties();
            p.load(in);
            url = p.getProperty("db.url");
            user = p.getProperty("db.user");
            password = p.getProperty("db.password");
            // Load driver optional for modern drivers but safe:
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Cannot load DB config from " + PROPS_FILE, e);
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }
}
