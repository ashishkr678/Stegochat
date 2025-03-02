package stegochat.stegochat.config;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvConfig {
    private static Dotenv dotenv;

    static {
        dotenv = Dotenv.configure().load();
    }

    public static String get(String key) {
        return dotenv.get(key);
    }

    public static void loadSystemProperties() {
        System.setProperty("DB_URL", get("DB_URL"));
        System.setProperty("DB_NAME", get("DB_NAME"));
    }
}