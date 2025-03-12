package stegochat.stegochat.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import stegochat.stegochat.dto.UserDTO;

import java.util.Map;

public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            HttpSession session = httpRequest.getSession(false);

            UserDTO userProfile = (session != null) ? (UserDTO) session.getAttribute("userProfile") : null;
            if (userProfile != null) {
                attributes.put("userProfile", userProfile);
                attributes.put("username", userProfile.getUsername());
                return true;
            }

            String token = CookieUtil.extractJwtFromCookie(httpRequest);

            if (token != null) {
                try {
                    Claims claims = jwtUtil.validateToken(token);
                    String username = claims.getSubject();
                    attributes.put("username", username);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception == null) {
            System.out.println("WebSocket handshake successful.");
        } else {
            System.out.println("WebSocket handshake failed: " + exception.getMessage());
        }
    }
}

