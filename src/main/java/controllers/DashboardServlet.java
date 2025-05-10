package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/dashboard/*")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            redirectToDashboard(request, response, role);
            return;
        }

        switch (pathInfo) {
            case "/admin":
                if (!"ADMINISTRADOR".equals(role)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso não autorizado");
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/views/dashboard/admin.jsp").forward(request, response);
                break;

            case "/funcionario":
                if ("VISITANTE".equals(role)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso não autorizado");
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/views/dashboard/funcionario.jsp").forward(request, response);
                break;

            case "/visitor":
                request.getRequestDispatcher("/WEB-INF/views/dashboard/visitor.jsp").forward(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Dashboard não encontrada");
                break;
        }
    }

    private void redirectToDashboard(HttpServletRequest request, HttpServletResponse response, String role)
            throws IOException {
        if ("ADMINISTRADOR".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/dashboard/admin");
        } else if ("VISITANTE".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/dashboard/visitor");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard/funcionario");
        }
    }
}
