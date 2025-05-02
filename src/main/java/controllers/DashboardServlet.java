package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dtos.auth.LoginResponse;

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
        
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Redirecionar para a dashboard apropriada com base no role
            if ("VISITANTE".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/dashboard/visitor");
            } else if ("ADMINISTRADOR".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/dashboard/admin");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard/funcionario");
            }
            return;
        }
        
        switch (pathInfo) {
            case "/admin":
                if (!"ADMINISTRADOR".equals(user.getRole())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso não autorizado");
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/views/dashboard/admin.jsp").forward(request, response);
                break;
                
            case "/funcionario":
                if ("VISITANTE".equals(user.getRole())) {
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
}
