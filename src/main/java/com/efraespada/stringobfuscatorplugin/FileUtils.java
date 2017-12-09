package com.efraespada.stringobfuscatorplugin;

import java.io.*;

import static com.efraespada.stringobfuscatorplugin.PrintUtils.print;

public class FileUtils {

    private static String variant;
    private static String module;

    private FileUtils() {
        // nothing to do here
    }

    public static void init(String module, String variant) {
        FileUtils.module = module;
        FileUtils.variant = variant;
    }

    public static String getTextFromFilePath(String path) {
        if (path == null) {
            return "";
        }
        String xml = "";
        String inputFilePath = getCurrentPath() + path;

        if (true) print("getting data from -> " + inputFilePath);

        String message = "";

        File file = new File(inputFilePath);
        try {
            FileInputStream stream = new FileInputStream(file);
            message = getString(new BufferedReader(new InputStreamReader(stream,"UTF-8")));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return message;
    }

    private static String getCurrentPath() {
        String path = new File(".").getAbsolutePath().replace(".", "").replace(module + File.separator,"");
        return path;
    }

    public static String getString(BufferedReader br) {
        StringBuilder builder = new StringBuilder();

        try {
            String aux = "";

            while ((aux = br.readLine()) != null) {
                builder.append(aux);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}
