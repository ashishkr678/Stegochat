package stegochat.stegochat.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import stegochat.stegochat.dto.UserDTO;

import java.util.Map;

public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);
    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        logger.info("Attempting WebSocket handshake...");

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            HttpSession session = httpRequest.getSession(false);

            // ✅ Check session-based authentication first
            UserDTO userProfile = (session != null) ? (UserDTO) session.getAttribute("userProfile") : null;
            if (userProfile != null) {
                logger.info("Authenticated WebSocket session via HTTP session: {}", userProfile.getUsername());
                attributes.put("userProfile", userProfile);
                attributes.put("username", userProfile.getUsername());
                WebSocketSessionManager.addUser(userProfile.getUsername());
                return true;
            }

            // ✅ Try authenticating via JWT token in cookies
            String token = CookieUtil.extractJwtFromCookie(httpRequest);
            if (token != null) {
                try {
                    Claims claims = jwtUtil.validateToken(token);
                    String username = claims.getSubject();

                    // ❌ Reject connection if user was previously logged out
                    if (!WebSocketSessionManager.isUserConnected(username)) {
                        logger.warn("WebSocket connection rejected: User {} was logged out.", username);
                        return false;
                    }

                    logger.info("Authenticated WebSocket session via JWT: {}", username);
                    attributes.put("username", username);
                    WebSocketSessionManager.addUser(username);
                    return true;
                } catch (Exception e) {
                    logger.error("Invalid WebSocket authentication token: {}", e.getMessage());
                    return false;
                }
            }
        }

        logger.warn("WebSocket handshake failed: No valid authentication found.");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception == null) {
            logger.info("WebSocket handshake successful.");
        } else {
            logger.error("WebSocket handshake failed: {}", exception.getMessage());
        }
    }
}
