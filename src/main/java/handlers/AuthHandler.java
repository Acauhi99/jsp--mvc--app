package handlers;

import models.Customer;
import models.Funcionario;
import repositories.AuthRepository;
import repositories.CustomerRepository;
import repositories.FuncionarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AuthHandler {

  private final AuthRepository authRepository;
  private final CustomerRepository customerRepository;
  private final FuncionarioRepository funcionarioRepository;

  public static final String ROLE_VISITANTE = "VISITANTE";
  public static final String ROLE_ADMINISTRADOR = "ADMINISTRADOR";
  public static final String ROLE_VETERINARIO = "VETERINARIO";
  public static final String ROLE_MANUTENCAO = "MANUTENCAO";

  public AuthHandler() {
    this.customerRepository = new CustomerRepository();
    this.funcionarioRepository = new FuncionarioRepository();
    this.authRepository = new AuthRepository();
  }

  public Map<String, Object> login(String email, String password) {
    Map<String, Object> result = new HashMap<>();
    result.put("success", false);

    if (isNullOrEmpty(email) || isNullOrEmpty(password)) {
      result.put("message", "Email e senha são obrigatórios");
      return result;
    }

    Map<String, Object> authResult = authRepository.login(email, password);

    if ((Boolean) authResult.get("authenticated")) {
      result.put("success", true);
      result.put("user", authResult.get("user"));
      result.put("userId", authResult.get("userId"));
      result.put("nome", authResult.get("nome"));
      result.put("email", authResult.get("email"));
      result.put("role", authResult.get("role"));

      // Determinar URL de redirecionamento com base no papel
      String role = (String) authResult.get("role");
      String redirectUrl;

      if (ROLE_VISITANTE.equals(role)) {
        redirectUrl = "/dashboard/visitor";
      } else if (ROLE_ADMINISTRADOR.equals(role)) {
        redirectUrl = "/dashboard/admin";
      } else {
        redirectUrl = "/dashboard/funcionario";
      }

      result.put("redirectUrl", redirectUrl);
    } else {
      result.put("message", authResult.get("message"));
    }

    return result;
  }

  public Map<String, Object> register(String nome, String email, String password,
      String confirmPassword, boolean isFuncionario,
      String cargoStr) {
    Map<String, Object> result = new HashMap<>();
    result.put("success", false);

    if (isNullOrEmpty(nome) || isNullOrEmpty(email) || isNullOrEmpty(password)) {
      result.put("message", "Nome, email e senha são obrigatórios");
      return result;
    }

    if (!password.equals(confirmPassword)) {
      result.put("message", "As senhas não coincidem");
      return result;
    }

    if (customerRepository.findByEmail(email).isPresent() ||
        funcionarioRepository.findByEmail(email).isPresent()) {
      result.put("message", "Email já está em uso");
      return result;
    }

    if (isFuncionario) {
      Map<String, Object> registerResult = authRepository.registerFuncionario(
          nome, email, password, cargoStr);

      if ((Boolean) registerResult.get("success")) {
        result.put("success", true);
        result.put("message", "Funcionário registrado com sucesso");
      } else {
        result.put("message", registerResult.get("message"));
      }
    } else {
      Map<String, Object> registerResult = authRepository.registerCustomer(
          nome, email, password);

      if ((Boolean) registerResult.get("success")) {
        result.put("success", true);
        result.put("message", "Cliente registrado com sucesso");
      } else {
        result.put("message", registerResult.get("message"));
      }
    }

    return result;
  }

  public void createUserSession(HttpServletRequest request, Object user, String role,
      UUID userId, String userName, String userEmail) {
    HttpSession session = request.getSession();
    session.setAttribute("user", user);
    session.setAttribute("role", role);
    session.setAttribute("userId", userId);
    session.setAttribute("userName", userName);
    session.setAttribute("userEmail", userEmail);
  }

  public void createFuncionarioSession(HttpServletRequest request, Funcionario funcionario) {
    createUserSession(
        request,
        funcionario,
        funcionario.getCargo().toString(),
        funcionario.getId(),
        funcionario.getNome(),
        funcionario.getEmail());
  }

  public void createCustomerSession(HttpServletRequest request, Customer customer) {
    createUserSession(
        request,
        customer,
        ROLE_VISITANTE,
        customer.getId(),
        customer.getNome(),
        customer.getEmail());
  }

  public void logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
  }

  public Optional<Funcionario> findFuncionarioByEmail(String email) {
    return funcionarioRepository.findByEmail(email);
  }

  public Optional<Customer> findCustomerByEmail(String email) {
    return customerRepository.findByEmail(email);
  }

  public String getDashboardUrl(String role) {
    if (ROLE_VISITANTE.equals(role)) {
      return "/dashboard/visitor";
    } else if (ROLE_ADMINISTRADOR.equals(role)) {
      return "/dashboard/admin";
    } else {
      return "/dashboard/funcionario";
    }
  }

  private boolean isNullOrEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }
}
