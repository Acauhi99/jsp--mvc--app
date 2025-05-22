package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

  // ROLES constants for better type safety
  public static final String ROLE_ADMINISTRADOR = "ADMINISTRADOR";
  public static final String ROLE_VETERINARIO = "VETERINARIO";
  public static final String ROLE_MANUTENCAO = "MANUTENCAO";
  public static final String ROLE_VISITANTE = "VISITANTE";

  protected boolean requireAuthentication(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
      resp.sendRedirect(req.getContextPath() + "/auth/login");
      return false;
    }
    return true;
  }

  protected boolean requireRole(HttpServletRequest req, HttpServletResponse resp, String role)
      throws IOException {
    if (!requireAuthentication(req, resp)) {
      return false;
    }

    HttpSession session = req.getSession(false);
    String userRole = (String) session.getAttribute("role");

    if (ROLE_ADMINISTRADOR.equals(userRole)) {
      return true;
    }

    if (!role.equals(userRole)) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso não autorizado");
      return false;
    }

    return true;
  }

  protected boolean requireAnyRole(HttpServletRequest req, HttpServletResponse resp, String... roles)
      throws IOException {
    if (!requireAuthentication(req, resp)) {
      return false;
    }

    HttpSession session = req.getSession(false);
    String userRole = (String) session.getAttribute("role");

    if (ROLE_ADMINISTRADOR.equals(userRole)) {
      return true;
    }

    for (String role : roles) {
      if (role.equals(userRole)) {
        return true;
      }
    }

    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso não autorizado");
    return false;
  }

  protected void forwardToView(HttpServletRequest req, HttpServletResponse resp, String viewPath)
      throws ServletException, IOException {
    req.getRequestDispatcher(viewPath).forward(req, resp);
  }

  protected String getUserRole(HttpServletRequest req) {
    HttpSession session = req.getSession(false);
    return session != null ? (String) session.getAttribute("role") : null;
  }
}