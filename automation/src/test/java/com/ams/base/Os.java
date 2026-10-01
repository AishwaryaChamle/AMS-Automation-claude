package com.ams.base;

import java.util.Locale;

/** The operating system the tests run on (Windows, macOS or Linux). */
public final class Os {

    private static final String NAME = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

    private Os() {
    }

    public static boolean isWindows() {
        return NAME.contains("win");
    }

    public static boolean isMac() {
        return NAME.contains("mac") || NAME.contains("darwin");
    }

    public static boolean isLinux() {
        return !isWindows() && !isMac();
    }
}
