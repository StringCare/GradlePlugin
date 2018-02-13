
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Date;

public class CredentialUtils {

    private static String key = null;
    private static String until = null;
    private static String error = null;
    private static boolean variantLocated = false;
    private static boolean moduleLocated = false;
    private static final String LIB = "/jniLib/";
    private static final String MALACATON = "libmalacaton-lib.dylib";

    static {
        loadLib(MALACATON);
    }

    private CredentialUtils() {
        // nothing to do here..
    }

    public static String getKey(String module, String variant, boolean debug) {
        return getKey("tatat");
    }

    private static void parseTrace(String module, String variant, String line, boolean debug) {

        if (line.toLowerCase().contains("downloading")) {
            if (debug) {
                PrintUtils.print(module, line, debug);
            }
        } else if (line.toLowerCase().contains("unzipping")) {
            if (debug) {
                PrintUtils.print(module, line, debug);
            }
        } else if (line.toLowerCase().contains("permissions")) {
            if (debug) {
                PrintUtils.print(module, line, debug);
            }
        } else if (line.toLowerCase().contains("config:") && moduleLocated && variantLocated) {
            boolean valid = !line.split(": ")[1].trim().equalsIgnoreCase("none");
            if (!valid) {
                key = line.split(": ")[1].trim();
                PrintUtils.print(module, "\uD83E\uDD2F no config defined for variant " + variant, true);
                if (debug) {
                    until = key;
                }
            } else if (debug){
                PrintUtils.print(module, "Module: " + module, true);
                PrintUtils.print(module, "Variant: " + variant, true);
            }

        } else if (line.toLowerCase().contains("sha") && moduleLocated && variantLocated) {
            key = line.split(" ")[1];
            if (debug) {
                PrintUtils.print(module, line, debug);
            }
        } else if (line.toLowerCase().contains("error")) {
            error = line.split(": ")[1];
        } else if (line.toLowerCase().contains("valid until") && moduleLocated && variantLocated) {
            until = line.split(": ")[1];
            if (debug) {
                PrintUtils.print(module, line, debug);
            }
        } else if (line.toLowerCase().contains("store") && moduleLocated && variantLocated) {
            if (debug) {
                PrintUtils.print(module, line, debug);
            }
        } else if (line.toLowerCase().contains("variant") && moduleLocated) {
            String locV = line.split(" ")[1];
            if (locV.equals(variant)) {
                variantLocated = true;
            }
        } else if (line.toLowerCase().contains(":" + module)) {
            moduleLocated = true;
        }
    }

    private static void loadLib(String name) {
        String path = "ML_" + new Date().getTime();
        name = name + ".dll";
        try {
            // have to use a stream
            InputStream i = StringCare.class.getResourceAsStream(name);
            // always write to different location
            File fileOut = new File(System.getProperty("java.io.tmpdir") + "/" + path + LIB + name);
            OutputStream out = org.apache.commons.io.FileUtils.openOutputStream(fileOut);
            IOUtils.copy(i, out);
            i.close();
            out.close();
            System.load(fileOut.toString());
        } catch (Exception e) {
            // PrintUtils.print("error: " + e.getLocalizedMessage());
        }
    }

    private static native String getKey(Object object);

}
