package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/dashboard/*")
public class DashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAuthentication(request, response)) {
            return;
        }

        String role = getUserRole(request);
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            redirectToDashboard(request, response, role);
            return;
        }

        switch (pathInfo) {
            case "/admin":
                if (!requireRole(request, response, ROLE_ADMINISTRADOR)) {
                    return;
                }
                forwardToView(request, response, "/WEB-INF/views/dashboard/admin.jsp");
                break;

            case "/funcionario":
                if (!requireAnyRole(request, response, ROLE_ADMINISTRADOR, ROLE_VETERINARIO, ROLE_MANUTENCAO)) {
                    return;
                }
                forwardToView(request, response, "/WEB-INF/views/dashboard/funcionario.jsp");
                break;

            case "/visitor":
                forwardToView(request, response, "/WEB-INF/views/dashboard/visitor.jsp");
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Dashboard não encontrada");
                break;
        }
    }

    private void redirectToDashboard(HttpServletRequest request, HttpServletResponse response, String role)
            throws IOException {
        if (ROLE_ADMINISTRADOR.equals(role)) {
            response.sendRedirect(request.getContextPath() + "/dashboard/admin");
        } else if (ROLE_VISITANTE.equals(role)) {
            response.sendRedirect(request.getContextPath() + "/dashboard/visitor");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard/funcionario");
        }
    }
}
