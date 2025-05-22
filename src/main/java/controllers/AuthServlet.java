package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Customer;
import models.Funcionario;
import repositories.CustomerRepository;
import repositories.FuncionarioRepository;
import utils.PasswordUtils;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/auth/*")
public class AuthServlet extends BaseServlet {

    private final CustomerRepository customerRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AuthServlet() {
        this.customerRepository = new CustomerRepository();
        this.funcionarioRepository = new FuncionarioRepository();
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

        if (isNullOrEmpty(email) || isNullOrEmpty(password)) {
            request.setAttribute("errorMessage", "Email e senha são obrigatórios");
            showLoginForm(request, response);
            return;
        }

        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findByEmail(email);
        if (funcionarioOpt.isPresent()) {
            Funcionario funcionario = funcionarioOpt.get();
            if (PasswordUtils.verifyPassword(password, funcionario.getPassword())) {
                createFuncionarioSession(request, funcionario);
                redirectToDashboard(request, response, funcionario.getCargo().toString());
                return;
            }
        }

        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (PasswordUtils.verifyPassword(password, customer.getPassword())) {
                createCustomerSession(request, customer);
                redirectToDashboard(request, response, ROLE_VISITANTE);
                return;
            }
        }

        request.setAttribute("errorMessage", "Email ou senha inválidos");
        showLoginForm(request, response);
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        boolean isFuncionario = "true".equals(request.getParameter("isFuncionario"));
        String cargoStr = request.getParameter("cargo");

        if (isNullOrEmpty(nome) || isNullOrEmpty(email) || isNullOrEmpty(password)) {
            request.setAttribute("errorMessage", "Nome, email e senha são obrigatórios");
            showRegisterForm(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "As senhas não coincidem");
            showRegisterForm(request, response);
            return;
        }

        if (customerRepository.findByEmail(email).isPresent() ||
                funcionarioRepository.findByEmail(email).isPresent()) {
            request.setAttribute("errorMessage", "Email já está em uso");
            showRegisterForm(request, response);
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);

        if (isFuncionario) {
            try {
                Funcionario.Cargo cargo = Funcionario.Cargo.valueOf(cargoStr);
                Funcionario funcionario = new Funcionario(nome, email, hashedPassword, cargo);
                funcionarioRepository.save(funcionario);
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "Cargo inválido selecionado");
                showRegisterForm(request, response);
                return;
            }
        } else {
            Customer customer = new Customer(nome, email, hashedPassword);
            customerRepository.save(customer);
        }

        request.setAttribute("successMessage", "Cadastro realizado com sucesso");
        showLoginForm(request, response);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

    private void createFuncionarioSession(HttpServletRequest request, Funcionario funcionario) {
        HttpSession session = request.getSession();
        session.setAttribute("user", funcionario);
        session.setAttribute("role", funcionario.getCargo().toString());
        session.setAttribute("userId", funcionario.getId());
        session.setAttribute("userName", funcionario.getNome());
        session.setAttribute("userEmail", funcionario.getEmail());
    }

    private void createCustomerSession(HttpServletRequest request, Customer customer) {
        HttpSession session = request.getSession();
        session.setAttribute("user", customer);
        session.setAttribute("role", ROLE_VISITANTE);
        session.setAttribute("userId", customer.getId());
        session.setAttribute("userName", customer.getNome());
        session.setAttribute("userEmail", customer.getEmail());
    }

    private void redirectToDashboard(HttpServletRequest request, HttpServletResponse response, String role)
            throws IOException {
        String redirectUrl;

        if (ROLE_VISITANTE.equals(role)) {
            redirectUrl = "/dashboard/visitor";
        } else if (ROLE_ADMINISTRADOR.equals(role)) {
            redirectUrl = "/dashboard/admin";
        } else {
            redirectUrl = "/dashboard/funcionario";
        }

        response.sendRedirect(request.getContextPath() + redirectUrl);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
