package stegochat.stegochat.security;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        if (request.getCookies() == null)
            return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "username".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public static String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public static Cookie createCookie(String name, String value, boolean httpOnly) {
        boolean isProduction = "prod".equals(System.getenv("PROFILES_ACTIVE"));

        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setPath("/");
        cookie.setSecure(isProduction);
        cookie.setMaxAge(value == null ? 0 : 10 * 60 * 60);

        return cookie;
    }

    public static void clearCookies(HttpServletResponse response) {
        Cookie jwtCookie = createCookie("jwt", "", true);
        jwtCookie.setMaxAge(0);

        Cookie usernameCookie = createCookie("username", "", false);
        usernameCookie.setMaxAge(0);

        Cookie jsessionCookie = new Cookie("JSESSIONID", "");
        jsessionCookie.setHttpOnly(true);
        jsessionCookie.setSecure(true);
        jsessionCookie.setPath("/");
        jsessionCookie.setMaxAge(0);

        response.addHeader("Set-Cookie", createCookieWithSameSite(jwtCookie, "Strict"));
        response.addHeader("Set-Cookie", createCookieWithSameSite(usernameCookie, "Strict"));
        response.addHeader("Set-Cookie", createCookieWithSameSite(jsessionCookie, "Strict"));
    }

    public static String getSameSiteValue() {
        return "prod".equals(System.getenv("PROFILES_ACTIVE")) ? "None" : "Lax";
    }
}
