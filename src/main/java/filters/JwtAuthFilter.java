package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JwtUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/api/*")
public class JwtAuthFilter implements Filter {
    
    // List of endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register"
    );
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        
        // Skip token validation for public endpoints
        if (isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        String authHeader = httpRequest.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Missing or invalid Authorization header");
            return;
        }
        
        String token = authHeader.substring(7);
        
        if (!JwtUtils.validateToken(token)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Invalid or expired token");
            return;
        }
        
        // Add user information to request attributes
        httpRequest.setAttribute("userId", JwtUtils.extractUserId(token));
        httpRequest.setAttribute("userEmail", JwtUtils.extractEmail(token));
        httpRequest.setAttribute("userRole", JwtUtils.extractRole(token));
        
        chain.doFilter(request, response);
    }
    
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.contains(path);
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void destroy() {
    }
}