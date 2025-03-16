package stegochat.stegochat.security;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionManager {
    private static final Set<String> activeUsers = ConcurrentHashMap.newKeySet();

    public static void addUser(String username) {
        activeUsers.add(username);
    }

    public static void removeUser(String username) {
        activeUsers.remove(username);
    }

    public static boolean isUserConnected(String username) {
        return activeUsers.contains(username);
    }

    public static void clearAllSessions() {
        activeUsers.clear();
    }
}
