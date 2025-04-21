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
import models.Funcionario.Cargo;

import java.io.IOException;

@WebServlet("/api/auth/*")
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid endpoint");
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
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
                break;
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        boolean isFuncionario = "true".equals(request.getParameter("isFuncionario"));
        
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .isFuncionario(isFuncionario)
                .build();
        
        LoginResponse loginResponse = authRepository.login(loginRequest);
        
        if (loginResponse.isAuthenticated()) {
            // Armazenar dados do usuário na sessão
            HttpSession session = request.getSession();
            session.setAttribute("user", loginResponse);
            session.setAttribute("token", loginResponse.getToken());
            
            // Redirecionar para a página principal
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            // Exibir mensagem de erro e voltar para a página de login
            request.setAttribute("errorMessage", loginResponse.getMessage());
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        boolean isFuncionario = "true".equals(request.getParameter("isFuncionario"));
        
        // Validação de senha
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }
        
        RegisterRequest registerRequest = RegisterRequest.builder()
                .nome(nome)
                .email(email)
                .password(password)
                .isFuncionario(isFuncionario)
                .build();
                
        if (isFuncionario) {
            try {
                String cargoStr = request.getParameter("cargo");
                Cargo cargo = Cargo.valueOf(cargoStr);
                registerRequest.setCargo(cargo);
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "Invalid position selected");
                request.getRequestDispatcher("/views/register.jsp").forward(request, response);
                return;
            }
        }
        
        RegisterResponse registerResponse = authRepository.register(registerRequest);
        
        if (registerResponse.isSuccess()) {
            // Redirecionar para a página de login com mensagem de sucesso
            request.setAttribute("successMessage", registerResponse.getMessage());
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        } else {
            // Exibir mensagem de erro e voltar para a página de registro
            request.setAttribute("errorMessage", registerResponse.getMessage());
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
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
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        } else if ("/register".equals(pathInfo)) {
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
        } else if ("/logout".equals(pathInfo)) {
            handleLogout(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
