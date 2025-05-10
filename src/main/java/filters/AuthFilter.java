package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

@WebFilter("/*")
public class AuthFilter implements Filter {

    // Endpoints públicos (acessíveis sem login)
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/",
            "/index.jsp",
            "/mapa",
            "/about",
            "/contact",
            "/auth/login",
            "/auth/register",
            "/css/*",
            "/js/*",
            "/images/*");

    // Mapeamento de URLs e funções permitidas
    private static final Map<String, Set<String>> ROLE_ACCESS_MAP = new HashMap<>();

    static {
        // Admin tem acesso a todas as rotas restritas
        Set<String> adminRoles = Collections.singleton("ADMINISTRADOR");

        // Rotas de acesso para visitantes
        Set<String> visitorRoles = new HashSet<>(Arrays.asList("VISITANTE", "ADMINISTRADOR"));
        ROLE_ACCESS_MAP.put("/dashboard/visitor", visitorRoles);
        ROLE_ACCESS_MAP.put("/ingresso", visitorRoles);
        ROLE_ACCESS_MAP.put("/ingresso/comprar", visitorRoles);
        ROLE_ACCESS_MAP.put("/ingresso/detalhes", visitorRoles);
        ROLE_ACCESS_MAP.put("/ingresso/utilizar", visitorRoles);
        ROLE_ACCESS_MAP.put("/animal/galeria", visitorRoles);

        // Rotas apenas para admin
        ROLE_ACCESS_MAP.put("/dashboard/admin", adminRoles);
        ROLE_ACCESS_MAP.put("/customer", adminRoles);
        ROLE_ACCESS_MAP.put("/customer/novo", adminRoles);
        ROLE_ACCESS_MAP.put("/customer/editar", adminRoles);
        ROLE_ACCESS_MAP.put("/customer/detalhes", adminRoles);
        ROLE_ACCESS_MAP.put("/customer/excluir", adminRoles);
        ROLE_ACCESS_MAP.put("/funcionario", adminRoles);
        ROLE_ACCESS_MAP.put("/funcionario/novo", adminRoles);
        ROLE_ACCESS_MAP.put("/funcionario/editar", adminRoles);
        ROLE_ACCESS_MAP.put("/funcionario/detalhes", adminRoles);
        ROLE_ACCESS_MAP.put("/funcionario/excluir", adminRoles);
        ROLE_ACCESS_MAP.put("/funcionario/veterinario", adminRoles);
        ROLE_ACCESS_MAP.put("/ingresso/admin", adminRoles);
        ROLE_ACCESS_MAP.put("/relatorio/consultas", adminRoles);
        ROLE_ACCESS_MAP.put("/relatorio/vendas", adminRoles);

        // Rotas para veterinários e admin
        Set<String> vetRoles = new HashSet<>(Arrays.asList("VETERINARIO", "ADMINISTRADOR"));
        ROLE_ACCESS_MAP.put("/dashboard/funcionario",
                new HashSet<>(Arrays.asList("VETERINARIO", "MANUTENCAO", "ADMINISTRADOR")));
        ROLE_ACCESS_MAP.put("/consulta", vetRoles);
        ROLE_ACCESS_MAP.put("/consulta/nova", vetRoles);
        ROLE_ACCESS_MAP.put("/consulta/detalhes", vetRoles);
        ROLE_ACCESS_MAP.put("/consulta/editar", vetRoles);
        ROLE_ACCESS_MAP.put("/consulta/historico", vetRoles);
        ROLE_ACCESS_MAP.put("/alimentacao", vetRoles);
        ROLE_ACCESS_MAP.put("/alimentacao/novo", vetRoles);
        ROLE_ACCESS_MAP.put("/alimentacao/editar", vetRoles);

        // Rotas para manutenção e admin
        Set<String> manutencaoRoles = new HashSet<>(Arrays.asList("MANUTENCAO", "ADMINISTRADOR"));
        ROLE_ACCESS_MAP.put("/manutencao", manutencaoRoles);
        ROLE_ACCESS_MAP.put("/manutencao/novo", manutencaoRoles);
        ROLE_ACCESS_MAP.put("/manutencao/salvar", manutencaoRoles);
        ROLE_ACCESS_MAP.put("/manutencao/detalhes", manutencaoRoles);
        ROLE_ACCESS_MAP.put("/manutencao/editar", manutencaoRoles);

        // Rotas que qualquer usuário logado pode acessar
        Set<String> loggedInRoles = new HashSet<>(
                Arrays.asList("VISITANTE", "VETERINARIO", "MANUTENCAO", "ADMINISTRADOR"));
        ROLE_ACCESS_MAP.put("/perfil", loggedInRoles);
        ROLE_ACCESS_MAP.put("/auth/logout", loggedInRoles);
        ROLE_ACCESS_MAP.put("/habitat", loggedInRoles);
        ROLE_ACCESS_MAP.put("/habitat/novo", new HashSet<>(Arrays.asList("MANUTENCAO", "ADMINISTRADOR")));

        ROLE_ACCESS_MAP.put("/animal", new HashSet<>(Arrays.asList("VETERINARIO", "ADMINISTRADOR")));
    }

    private static final Set<String> VALID_ROUTES = new HashSet<>(Arrays.asList(
            "/", "/about", "/contact", "/mapa",
            "/auth/login", "/auth/register", "/auth/logout",
            "/dashboard/admin", "/dashboard/visitor", "/dashboard/funcionario",
            "/animal", "/animal/galeria",
            "/habitat", "/habitat/novo",
            "/funcionario", "/funcionario/veterinario", "/funcionario/novo", "/funcionario/editar",
            "/funcionario/detalhes", "/funcionario/excluir",
            "/customer", "/customer/novo", "/customer/editar", "/customer/detalhes", "/customer/excluir",
            "/ingresso", "/ingresso/comprar", "/ingresso/admin", "/ingresso/detalhes", "/ingresso/utilizar",
            "/consulta", "/consulta/nova", "/consulta/detalhes", "/consulta/editar", "/consulta/historico",
            "/alimentacao", "/alimentacao/novo", "/alimentacao/editar",
            "/manutencao", "/manutencao/novo", "/manutencao/salvar", "/manutencao/detalhes", "/manutencao/editar",
            "/perfil",
            "/relatorio/consultas", "/relatorio/vendas"));

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

        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        String normalizedPath = normalizePath(path);

        if (!isValidRoute(normalizedPath)) {
            httpRequest.getRequestDispatcher("/WEB-INF/views/notfound.jsp").forward(request, response);
            return;
        }

        if (isPublicEndpoint(normalizedPath)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(contextPath + "/auth/login");
            return;
        }

        String userRole = (String) session.getAttribute("role");
        if (!hasAccess(normalizedPath, userRole)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Você não tem permissão para acessar esta página");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isStaticResource(String path) {
        return path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/");
    }

    private String normalizePath(String path) {
        String basePath = path.contains("?") ? path.substring(0, path.indexOf("?")) : path;

        if (basePath.equals("/animal/galeria")) {
            return "/animal/galeria";
        }

        for (String validRoute : VALID_ROUTES) {
            if (basePath.startsWith(validRoute + "/")) {
                return validRoute;
            }
        }

        return basePath;
    }

    private boolean isValidRoute(String path) {
        if (isStaticResource(path)) {
            return true;
        }

        if (VALID_ROUTES.contains(path)) {
            return true;
        }

        return isPublicEndpoint(path);
    }

    private boolean isPublicEndpoint(String path) {
        for (String endpoint : PUBLIC_ENDPOINTS) {
            if (endpoint.endsWith("/*")) {
                String prefix = endpoint.substring(0, endpoint.length() - 2);
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else if (path.equals(endpoint)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAccess(String path, String userRole) {
        if ("ADMINISTRADOR".equals(userRole)) {
            return true;
        }

        Set<String> allowedRoles = ROLE_ACCESS_MAP.get(path);
        if (allowedRoles == null) {
            return false;
        }

        return allowedRoles.contains(userRole);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}