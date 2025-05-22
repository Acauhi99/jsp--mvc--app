package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Customer;
import models.Funcionario;
import handlers.AuthHandler;

import java.io.IOException;
import java.util.Map;

@WebServlet("/auth/*")
public class AuthServlet extends BaseServlet {

    private final AuthHandler authHandler;

    public AuthServlet() {
        this.authHandler = new AuthHandler();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        switch (pathInfo) {
            case "/login":
                showLoginForm(request, response);
                break;
            case "/register":
                showRegisterForm(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (pathInfo) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register":
                handleRegister(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void showLoginForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardToView(request, response, "/WEB-INF/views/login.jsp");
    }

    private void showRegisterForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardToView(request, response, "/WEB-INF/views/register.jsp");
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Map<String, Object> result = authHandler.login(email, password);

        if ((Boolean) result.get("success")) {
            Object user = result.get("user");

            if (user instanceof Funcionario) {
                authHandler.createFuncionarioSession(request, (Funcionario) user);
            } else if (user instanceof Customer) {
                authHandler.createCustomerSession(request, (Customer) user);
            }

            response.sendRedirect(request.getContextPath() + result.get("redirectUrl"));
        } else {
            request.setAttribute("errorMessage", result.get("message"));
            showLoginForm(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        boolean isFuncionario = "true".equals(request.getParameter("isFuncionario"));
        String cargoStr = request.getParameter("cargo");

        Map<String, Object> result = authHandler.register(
                nome, email, password, confirmPassword, isFuncionario, cargoStr);

        if ((Boolean) result.get("success")) {
            request.setAttribute("successMessage", result.get("message"));
            showLoginForm(request, response);
        } else {
            request.setAttribute("errorMessage", result.get("message"));
            showRegisterForm(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        authHandler.logout(request);
        response.sendRedirect(request.getContextPath() + "/");
    }
}
