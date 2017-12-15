package com.stringcare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.stringcare.PrintUtils.print;

public class CredentialUtils {

    private static String key = null;
    private static String variant = null;
    private static String variantO = null;
    private static String module = null;
    private static String file = null;
    private static String error = null;
    private static boolean variantLocated = false;
    private static boolean moduleLocated = false;
    private static String stringPath = null;
    private static String version = null;
    private static boolean versionFound = false;
    private static boolean debug = false;

    private CredentialUtils() {
        // nothing to do here..
    }

    public static void init(String module, String variant, boolean debug) {
        CredentialUtils.module = module;
        CredentialUtils.variant = variant;
        CredentialUtils.debug = debug;
    }

    public static String getKey() {
        try {

            String cmd = "";
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                cmd = "gradlew.bat";
            } else {
                cmd = "gradlew";
                Runtime.getRuntime().exec("chmod +x ./" + cmd);
            }

            String command = "./" + cmd + " signingReport";

            InputStream is = Runtime.getRuntime().exec(command).getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader buff = new BufferedReader (isr);

            String line;
            while ((line = buff.readLine()) != null) {
                parseTrace(line);
                if (key != null) {
                    break;
                }
            }

        } catch (IOException e) {
            if (debug) e.printStackTrace();
        }
        return key;
    }

    private static void parseTrace(String line) {

        boolean mustPrint = false;

        if (line.toLowerCase().contains("downloading")) {
            mustPrint = true;
        } else if (line.toLowerCase().contains("unzipping")) {
            mustPrint = true;
        } else if (line.toLowerCase().contains("permissions")) {
            mustPrint = true;
        } else if (line.toLowerCase().contains("sha") && moduleLocated && variantLocated) {
            key = line.split(" ")[1];
        } else if (line.toLowerCase().contains("error")) {
            error = line.split(": ")[1];
        } else if (line.toLowerCase().contains("variant") && moduleLocated) {
            String locV = line.split(" ")[1];
            if (locV.equals(variant)) {
                print("Build Variant: " + locV);
                variantLocated = true;
            }
        } else if (line.toLowerCase().contains(":" + module)) {
            moduleLocated = true;
        }

        if (mustPrint)
            print(line);
    }

}
