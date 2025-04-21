package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/",
            "/index.jsp",
            "/views/index.jsp",
            "/api/auth/login",
            "/api/auth/register",
            "/about",
            "/contact", 
            "/css/*",
            "/js/*",
            "/images/*"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        if (path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        if (isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            chain.doFilter(request, response);
            return;
        }

        httpResponse.sendRedirect(contextPath + "/api/auth/login");
    }

    private boolean isPublicEndpoint(String path) {
        if (PUBLIC_ENDPOINTS.contains(path)) {
            return true;
        }

        for (String endpoint : PUBLIC_ENDPOINTS) {
            if (endpoint.endsWith("/*")) {
                String prefix = endpoint.substring(0, endpoint.length() - 1);
                if (path.startsWith(prefix)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
