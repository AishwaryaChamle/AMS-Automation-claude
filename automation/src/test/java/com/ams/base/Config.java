package com.ams.base;

import java.io.InputStream;
import java.util.Properties;

/**
 * Reads settings from config.properties.
 * A value can be overridden on the command line, e.g. mvn test -Dheadless=true
 */
public final class Config {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            PROPS.load(in);
        } catch (Exception e) {
            throw new IllegalStateException("Could not load config.properties", e);
        }
    }

    private Config() {
    }

    public static String get(String key) {
        String value = System.getProperty(key);
        // An unresolved Maven placeholder (e.g. "${priorities}" when the priority question was skipped) is not a value
        if (value == null || value.startsWith("${")) {
            value = PROPS.getProperty(key, "");
        }
        return value.trim();
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String password() {
        return get("password");
    }

    public static int timeoutSeconds() {
        return Integer.parseInt(get("timeout.seconds"));
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    // AMS database connection (Database.md)
    public static String dbUrl() {
        return get("db.url");
    }

    public static String dbUser() {
        return get("db.user");
    }

    public static String dbPassword() {
        return get("db.password");
    }

    // User IDs for each role
    public static String agencyAdmin() {
        return get("user.agencyAdmin");
    }

    public static String hob() {
        return get("user.hob");
    }

    public static String supervisor() {
        return get("user.supervisor");
    }

    public static String agencySupervisor() {
        return get("user.agencySupervisor");
    }

    public static String lop() {
        return get("user.lop");
    }

    public static String marketing() {
        return get("user.marketing");
    }

    /** A second Agency Admin (branch NT01), only for test cases that need two Agency Admins. */
    public static String agencyAdmin2() {
        return get("user.agencyAdmin2");
    }
}
