package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/perfil")
public class PerfilServlet extends BaseServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (!requireAuthentication(req, resp)) {
      return;
    }

    String role = determineUserRole(req.getSession(false));
    forwardToAppropriateView(req, resp, role);
  }

  private String determineUserRole(HttpSession session) {
    String role = (String) session.getAttribute("role");
    if (role != null) {
      return role;
    }

    Object user = session.getAttribute("user");
    if (user == null) {
      return "";
    }

    Class<?> userClass = user.getClass();
    try {
      if (hasMethod(userClass, "getRole")) {
        Object roleObj = userClass.getMethod("getRole").invoke(user);
        return roleObj != null ? roleObj.toString() : "";
      } else if (hasMethod(userClass, "getCargo")) {
        Object cargoObj = userClass.getMethod("getCargo").invoke(user);
        return cargoObj != null ? cargoObj.toString() : "";
      }
    } catch (Exception e) {
      System.err.println("Error determining user role: " + e.getMessage());
    }

    return "";
  }

  private boolean hasMethod(Class<?> clazz, String methodName) {
    try {
      clazz.getMethod(methodName);
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }

  private void forwardToAppropriateView(HttpServletRequest req, HttpServletResponse resp, String role)
      throws ServletException, IOException {
    String viewPath;

    if (ROLE_VISITANTE.equalsIgnoreCase(role)) {
      viewPath = "/WEB-INF/views/perfil/visitor.jsp";
    } else if (ROLE_ADMINISTRADOR.equalsIgnoreCase(role)) {
      viewPath = "/WEB-INF/views/perfil/admin.jsp";
    } else {
      viewPath = "/WEB-INF/views/perfil/funcionario.jsp";
    }

    forwardToView(req, resp, viewPath);
  }
}