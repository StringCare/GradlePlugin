package com.efraespada.stringobfuscatorplugin;

public class PrintUtils {

    private final static String TAG = "stringobfuscatorplugin";
    private static String variant;
    private static String module;

    private PrintUtils() {
        // nothing to do here
    }

    public static void init(String module, String variant) {
        PrintUtils.module = module;
        PrintUtils.variant = variant;
    }

    /**
     * prints messages (for gradle console)
     * @param message
     */
    public static void print(String message) {
        if (variant != null && module != null) {
            System.out.println(":" + module + ":" + TAG + " - " + message);
        } else {
            System.out.println(message);
        }
    }

    public static String uncapitalize(String value) {
        return value.substring(0, 1).toLowerCase() + value.substring(1, value.length());
    }
}
