package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
      resp.sendRedirect(req.getContextPath() + "/auth/login");
      return;
    }

    Object user = session.getAttribute("user");
    String role = (String) session.getAttribute("role");
    if (role == null && user != null) {
      try {
        role = (String) user.getClass().getMethod("getRole").invoke(user);
      } catch (Exception ignored) {
      }
    }

    if ("VISITANTE".equalsIgnoreCase(role)) {
      req.getRequestDispatcher("/WEB-INF/views/perfil/visitor.jsp").forward(req, resp);
    } else if ("ADMINISTRADOR".equalsIgnoreCase(role)) {
      req.getRequestDispatcher("/WEB-INF/views/perfil/admin.jsp").forward(req, resp);
    } else {
      req.getRequestDispatcher("/WEB-INF/views/perfil/funcionario.jsp").forward(req, resp);
    }
  }
}
