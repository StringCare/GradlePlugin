import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintUtils {

    private static String variant;
    private static String module;
    private static final Logger logger = LoggerFactory.getLogger(StringCare.class);

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
                _print(":" + module + ":" + message);
            } else {
                _print("\t" + message);
            }
        } else {
            _print(message);
        }
    }

    public static void print(String module, String message, boolean tab) {
        if (module != null) {
            if (!tab) {
                _print(":" + module + ":" + message);
            } else {
                _print("\t" + message);
            }
        } else {
            _print(message);
        }
    }

    public static void print(String module, String message) {
        print(module, message, false);
    }

    private static void _print(String value) {
        logger.info(value);
    }

    public static String uncapitalize(String value) {
        return value.substring(0, 1).toLowerCase() + value.substring(1, value.length());
    }
}
