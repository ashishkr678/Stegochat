package stegochat.stegochat.security;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    static {
        loadSystemProperties();
    }

    public static String get(String key) {
        return dotenv.get(key);
    }

    public static void loadSystemProperties() {
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }
}
