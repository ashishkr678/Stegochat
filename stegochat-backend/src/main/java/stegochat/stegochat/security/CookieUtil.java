package stegochat.stegochat.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class CookieUtil {

    public static String createCookieWithSameSite(Cookie cookie, String sameSite) {
        StringBuilder cookieBuilder = new StringBuilder();

        cookieBuilder.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
        cookieBuilder.append(" Path=").append(cookie.getPath()).append(";");
        cookieBuilder.append(" Max-Age=").append(cookie.getMaxAge()).append(";");
        if (cookie.getSecure()) {
            cookieBuilder.append(" Secure;");
        }
        if (cookie.isHttpOnly()) {
            cookieBuilder.append(" HttpOnly;");
        }
        cookieBuilder.append(" SameSite=").append(sameSite).append(";");

        return cookieBuilder.toString();
    }

    public static String extractUsernameFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "username".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
