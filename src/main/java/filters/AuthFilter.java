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

    private static final String ROLE_ADMINISTRADOR = "ADMINISTRADOR";
    private static final String ROLE_VETERINARIO = "VETERINARIO";
    private static final String ROLE_MANUTENCAO = "MANUTENCAO";
    private static final String ROLE_VISITANTE = "VISITANTE";

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

    private static final Map<String, Set<String>> ROLE_ACCESS_MAP = new HashMap<>();

    static {
        initializeRoleAccessMap();
    }

    private static void initializeRoleAccessMap() {
        // Rotas de acesso para visitantes
        Set<String> visitorRoles = new HashSet<>(Arrays.asList(ROLE_VISITANTE, ROLE_ADMINISTRADOR));
        Arrays.asList(
                "/dashboard/visitor",
                "/ingresso",
                "/ingresso/comprar",
                "/ingresso/detalhes",
                "/ingresso/utilizar",
                "/animal/galeria").forEach(route -> ROLE_ACCESS_MAP.put(route, visitorRoles));

        // Rotas apenas para admin
        Set<String> adminRoles = Collections.singleton(ROLE_ADMINISTRADOR);
        Arrays.asList(
                "/dashboard/admin",
                "/customer", "/customer/novo", "/customer/editar", "/customer/detalhes", "/customer/excluir",
                "/funcionario", "/funcionario/novo", "/funcionario/editar", "/funcionario/detalhes",
                "/funcionario/excluir", "/funcionario/veterinario",
                "/ingresso/admin",
                "/relatorio/consultas", "/relatorio/vendas").forEach(route -> ROLE_ACCESS_MAP.put(route, adminRoles));

        // Rotas para veterinários e admin
        Set<String> vetRoles = new HashSet<>(Arrays.asList(ROLE_VETERINARIO, ROLE_ADMINISTRADOR));
        Arrays.asList(
                "/consulta", "/consulta/nova", "/consulta/detalhes", "/consulta/editar", "/consulta/historico",
                "/alimentacao", "/alimentacao/novo", "/alimentacao/editar")
                .forEach(route -> ROLE_ACCESS_MAP.put(route, vetRoles));

        // Rota para todos os funcionários
        Set<String> allStaffRoles = new HashSet<>(
                Arrays.asList(ROLE_VETERINARIO, ROLE_MANUTENCAO, ROLE_ADMINISTRADOR));
        ROLE_ACCESS_MAP.put("/dashboard/funcionario", allStaffRoles);

        // Rotas para manutenção e admin
        Set<String> manutencaoRoles = new HashSet<>(Arrays.asList(ROLE_MANUTENCAO, ROLE_ADMINISTRADOR));
        Arrays.asList(
                "/manutencao", "/manutencao/novo", "/manutencao/salvar", "/manutencao/detalhes", "/manutencao/editar",
                "/habitat/novo").forEach(route -> ROLE_ACCESS_MAP.put(route, manutencaoRoles));

        // Rotas que qualquer usuário logado pode acessar
        Set<String> loggedInRoles = new HashSet<>(
                Arrays.asList(ROLE_VISITANTE, ROLE_VETERINARIO, ROLE_MANUTENCAO, ROLE_ADMINISTRADOR));
        Arrays.asList(
                "/perfil", "/auth/logout", "/habitat").forEach(route -> ROLE_ACCESS_MAP.put(route, loggedInRoles));

        // Rotas específicas
        ROLE_ACCESS_MAP.put("/animal", new HashSet<>(Arrays.asList(ROLE_VETERINARIO, ROLE_ADMINISTRADOR)));
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
        if (ROLE_ADMINISTRADOR.equals(userRole)) {
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