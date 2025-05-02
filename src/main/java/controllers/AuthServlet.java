package controllers;

import dtos.auth.LoginRequest;
import dtos.auth.LoginResponse;
import dtos.auth.RegisterRequest;
import dtos.auth.RegisterResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import repositories.AuthRepository;

import java.io.IOException;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    private final AuthRepository authRepository;

    public AuthServlet() {
        this.authRepository = new AuthRepository();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Endpoint inválido");
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
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint não encontrado");
                break;
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        LoginRequest loginRequest = new LoginRequest(email, password);
        LoginResponse loginResponse = authRepository.login(loginRequest);

        if (loginResponse.isAuthenticated()) {
            HttpSession session = request.getSession();
            session.setAttribute("user", loginResponse);

            String redirectUrl;
            String role = loginResponse.getRole();

            if ("VISITANTE".equals(role)) {
                redirectUrl = "/dashboard/visitor";
            } else if ("ADMINISTRADOR".equals(role)) {
                redirectUrl = "/dashboard/admin";
            } else {
                redirectUrl = "/dashboard/funcionario";
            }

            response.sendRedirect(request.getContextPath() + redirectUrl);
        } else {
            request.setAttribute("errorMessage", loginResponse.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
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

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "As senhas não coincidem");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(nome, email, password, isFuncionario, cargoStr);
        RegisterResponse registerResponse = authRepository.register(registerRequest);

        if (registerResponse.success()) {
            request.setAttribute("successMessage", registerResponse.message());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", registerResponse.message());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/login".equals(pathInfo)) {
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } else if ("/register".equals(pathInfo)) {
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        } else if ("/logout".equals(pathInfo)) {
            handleLogout(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
