package com.efraespada.stringobfuscatorplugin;

public class PrintUtils {

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
        print(message, false);
    }
    public static void print(String message, boolean tab) {
        if (variant != null && module != null) {
            if (!tab) {
                System.out.println(":" + module + ":" + message);
            } else {
                System.out.println("\t" + message);
            }
        } else {
            System.out.println(message);
        }
    }

    public static String uncapitalize(String value) {
        return value.substring(0, 1).toLowerCase() + value.substring(1, value.length());
    }
}
