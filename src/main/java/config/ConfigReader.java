package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads {@code config-<env>.properties} from the classpath.
 * The environment comes from -Denv and defaults to "staging".
 * A system property with the same key overrides the value in the file,
 * so CI can change settings without editing files.
 */
public final class ConfigReader {

    private static final String DEFAULT_ENV = "staging";
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
    }

    public static String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Config key '" + key + "' is not defined in "
                    + fileName() + " and was not passed as -D" + key);
        }
        return value.trim();
    }

    /**
     * Only the literal string "true" (case-insensitive) returns true.
     * Values such as "yes" or "1" are treated as false.
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Config key '" + key + "' must be an integer but was '" + value + "'", e);
        }
    }

    private static Properties load() {
        String fileName = fileName();
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                throw new IllegalStateException("Config file '" + fileName + "' not found on the classpath");
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config file '" + fileName + "'", e);
        }
    }

    private static String fileName() {
        return "config-" + System.getProperty("env", DEFAULT_ENV) + ".properties";
    }
}