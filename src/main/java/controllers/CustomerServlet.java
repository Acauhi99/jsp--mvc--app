package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Customer;
import dtos.auth.LoginResponse;
import repositories.CustomerRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import jakarta.persistence.EntityManager;

@WebServlet(urlPatterns = { "/customer", "/customer/novo", "/customer/editar", "/customer/detalhes",
    "/customer/excluir" })
public class CustomerServlet extends HttpServlet {

  private final CustomerRepository customerRepository;

  public CustomerServlet() {
    this.customerRepository = new CustomerRepository();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    HttpSession session = req.getSession(false);

    // Verificar autenticação
    if (session == null || session.getAttribute("user") == null) {
      resp.sendRedirect(req.getContextPath() + "/auth/login");
      return;
    }

    // Verificar se é administrador
    LoginResponse login = (LoginResponse) session.getAttribute("user");
    if (!"ADMINISTRADOR".equals(login.getRole())) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    // Processar as diferentes rotas
    switch (path) {
      case "/customer":
        listarVisitantes(req, resp);
        break;
      case "/customer/novo":
        exibirFormulario(req, resp, null);
        break;
      case "/customer/editar":
        editarVisitante(req, resp);
        break;
      case "/customer/detalhes":
        detalhesVisitante(req, resp);
        break;
      default:
        resp.sendRedirect(req.getContextPath() + "/customer");
        break;
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    HttpSession session = req.getSession(false);

    // Verificar autenticação
    if (session == null || session.getAttribute("user") == null) {
      resp.sendRedirect(req.getContextPath() + "/auth/login");
      return;
    }

    // Verificar se é administrador
    LoginResponse login = (LoginResponse) session.getAttribute("user");
    if (!"ADMINISTRADOR".equals(login.getRole())) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    // Processar as diferentes rotas
    if ("/customer/novo".equals(path) || "/customer/editar".equals(path)) {
      salvarVisitante(req, resp);
    } else if ("/customer/excluir".equals(path)) {
      excluirVisitante(req, resp);
    } else {
      resp.sendRedirect(req.getContextPath() + "/customer");
    }
  }

  private void listarVisitantes(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    EntityManager em = customerRepository.getEntityManager();
    List<Customer> visitantes;

    try {
      // Usar JOIN FETCH para carregar os ingressos antecipadamente
      visitantes = em
          .createQuery("SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.ingressosAdquiridos", Customer.class)
          .getResultList();
    } finally {
      em.close();
    }

    req.setAttribute("visitantes", visitantes);
    req.getRequestDispatcher("/WEB-INF/views/visitante/list.jsp").forward(req, resp);
  }

  private void exibirFormulario(HttpServletRequest req, HttpServletResponse resp, Customer visitante)
      throws ServletException, IOException {
    if (visitante != null) {
      req.setAttribute("visitante", visitante);
    }
    req.getRequestDispatcher("/WEB-INF/views/visitante/edit.jsp").forward(req, resp);
  }

  private void editarVisitante(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null || id.isEmpty()) {
      resp.sendRedirect(req.getContextPath() + "/customer");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      Optional<Customer> optVisitante = customerRepository.findById(uuid);

      if (optVisitante.isPresent()) {
        req.setAttribute("visitante", optVisitante.get());
        req.getRequestDispatcher("/WEB-INF/views/visitante/edit.jsp").forward(req, resp);
      } else {
        resp.sendRedirect(req.getContextPath() + "/customer");
      }
    } catch (IllegalArgumentException e) {
      resp.sendRedirect(req.getContextPath() + "/customer");
    }
  }

  private void detalhesVisitante(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null || id.isEmpty()) {
      resp.sendRedirect(req.getContextPath() + "/customer");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      Optional<Customer> optVisitante = customerRepository.findById(uuid);

      if (optVisitante.isPresent()) {
        req.setAttribute("visitante", optVisitante.get());
        req.getRequestDispatcher("/WEB-INF/views/visitante/details.jsp").forward(req, resp);
      } else {
        resp.sendRedirect(req.getContextPath() + "/customer");
      }
    } catch (IllegalArgumentException e) {
      resp.sendRedirect(req.getContextPath() + "/customer");
    }
  }

  private void salvarVisitante(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String id = req.getParameter("id");
    String nome = req.getParameter("nome");
    String email = req.getParameter("email");
    String password = req.getParameter("password");

    if (nome == null || nome.isEmpty() || email == null || email.isEmpty()) {
      req.setAttribute("erro", "Nome e email são obrigatórios.");
      if (id != null && !id.isEmpty()) {
        Customer visitante = new Customer();
        visitante.setId(UUID.fromString(id));
        visitante.setNome(nome);
        visitante.setEmail(email);
        exibirFormulario(req, resp, visitante);
      } else {
        exibirFormulario(req, resp, null);
      }
      return;
    }

    try {
      if (id != null && !id.isEmpty()) {
        // Editar existente
        UUID uuid = UUID.fromString(id);
        Optional<Customer> optVisitante = customerRepository.findById(uuid);

        if (optVisitante.isPresent()) {
          Customer visitante = optVisitante.get();
          visitante.setNome(nome);
          visitante.setEmail(email);
          if (password != null && !password.isEmpty()) {
            visitante.setPassword(password); // Idealmente, hash a senha
          }

          customerRepository.update(visitante);
          resp.sendRedirect(req.getContextPath() + "/customer?mensagem=Visitante atualizado com sucesso!");
        } else {
          resp.sendRedirect(req.getContextPath() + "/customer");
        }
      } else {
        // Novo visitante
        if (password == null || password.isEmpty()) {
          req.setAttribute("erro", "Senha é obrigatória para novo visitante.");
          exibirFormulario(req, resp, null);
          return;
        }

        // Verificar se email já existe
        if (customerRepository.findByEmail(email).isPresent()) {
          req.setAttribute("erro", "Email já cadastrado.");
          Customer visitante = new Customer();
          visitante.setNome(nome);
          visitante.setEmail(email);
          exibirFormulario(req, resp, visitante);
          return;
        }

        Customer visitante = new Customer(nome, email, password); // Idealmente, hash a senha
        customerRepository.save(visitante);
        resp.sendRedirect(req.getContextPath() + "/customer?mensagem=Visitante cadastrado com sucesso!");
      }
    } catch (Exception e) {
      req.setAttribute("erro", "Erro ao salvar visitante: " + e.getMessage());

      Customer visitante = new Customer();
      if (id != null && !id.isEmpty()) {
        visitante.setId(UUID.fromString(id));
      }
      visitante.setNome(nome);
      visitante.setEmail(email);

      exibirFormulario(req, resp, visitante);
    }
  }

  private void excluirVisitante(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null || id.isEmpty()) {
      resp.sendRedirect(req.getContextPath() + "/customer");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      customerRepository.delete(uuid);

      // Usando URLEncoder para codificar a mensagem corretamente
      String mensagem = java.net.URLEncoder.encode("Visitante excluído com sucesso!", "UTF-8");
      resp.sendRedirect(req.getContextPath() + "/customer?mensagem=" + mensagem);
    } catch (Exception e) {
      String erro = java.net.URLEncoder.encode("Erro ao excluir visitante: " + e.getMessage(), "UTF-8");
      resp.sendRedirect(req.getContextPath() + "/customer?erro=" + erro);
    }
  }
}
