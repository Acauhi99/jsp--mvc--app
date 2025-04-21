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
public class JwtAuthFilter implements Filter {
    
    // Lista de endpoints que não requerem autenticação
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/",                   // Página inicial
            "/index.jsp",          // Página inicial alternativa
            "/views/index.jsp",    // Página inicial pelo path completo
            "/api/auth/login",     // Login
            "/api/auth/register",  // Registro
            "/css/*",              // Recursos estáticos
            "/js/*",               // Recursos estáticos
            "/images/*"            // Recursos estáticos
    );
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Remover o contexto do path para comparação
        if (path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        
        // Verificar se é um endpoint público ou recurso estático
        if (isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar autenticação por sessão
        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            chain.doFilter(request, response);
            return;
        }
        
        // Se não estiver autenticado, redirecionar para login
        httpResponse.sendRedirect(contextPath + "/api/auth/login");
    }
    
    private boolean isPublicEndpoint(String path) {
        // Verificar endpoints exatos
        if (PUBLIC_ENDPOINTS.contains(path)) {
            return true;
        }
        
        // Verificar padrões com wildcard
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