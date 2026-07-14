package com.sawai.insurance.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * Custom middleware interceptor that guards every /api/** route.
 *
 * Rules enforced (per assignment spec):
 *  - Every request must carry a valid "X-Auth-Token" header, otherwise 401.
 *  - ADMIN and AGENT can both read (GET) and write (POST/PUT).
 *  - Only ADMIN can perform DELETE. An AGENT attempting DELETE gets 403.
 *
 * Tokens are validated against an in-memory registry (no hardcoded route
 * bypassing, no mock data - the check runs on every request that hits /api/**).
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String TOKEN_HEADER = "X-Auth-Token";
    public static final String ROLE_ATTRIBUTE = "role";

    // In a production system these would live in a DB / identity provider.
    // For this assignment, a fixed token -> role registry satisfies the
    // "custom HTTP token signature key named X-Auth-Token" requirement.
    private static final Map<String, String> TOKEN_ROLE_REGISTRY = Map.of(
            "admin-9f3a7c2e-token", "ADMIN",
            "agent-4b1d8e6f-token", "AGENT"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String token = request.getHeader(TOKEN_HEADER);

        if (token == null || token.isBlank() || !TOKEN_ROLE_REGISTRY.containsKey(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Missing or invalid X-Auth-Token header\"}"
            );
            return false;
        }

        String role = TOKEN_ROLE_REGISTRY.get(token);
        request.setAttribute(ROLE_ATTRIBUTE, role);

        boolean isDelete = "DELETE".equalsIgnoreCase(request.getMethod());
        if (isDelete && !"ADMIN".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Only ADMIN role can perform delete operations\"}"
            );
            return false;
        }

        return true;
    }
}
