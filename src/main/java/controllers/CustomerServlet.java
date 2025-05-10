package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Customer;
import repositories.CustomerRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@WebServlet(urlPatterns = { "/customer", "/customer/novo", "/customer/editar", "/customer/detalhes",
    "/customer/excluir" })
public class CustomerServlet extends HttpServlet {

  private final CustomerRepository customerRepository;

  public CustomerServlet() {
    this.customerRepository = new CustomerRepository();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String path = request.getServletPath();

    if (!verificarAcesso(request, response)) {
      return;
    }

    switch (path) {
      case "/customer":
        listarVisitantes(request, response);
        break;
      case "/customer/novo":
        exibirFormulario(request, response, null);
        break;
      case "/customer/editar":
        editarVisitante(request, response);
        break;
      case "/customer/detalhes":
        detalhesVisitante(request, response);
        break;
      default:
        response.sendRedirect(request.getContextPath() + "/customer");
        break;
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String path = request.getServletPath();

    if (!verificarAcesso(request, response)) {
      return;
    }

    if ("/customer/novo".equals(path) || "/customer/editar".equals(path)) {
      salvarVisitante(request, response);
    } else if ("/customer/excluir".equals(path)) {
      excluirVisitante(request, response);
    } else {
      response.sendRedirect(request.getContextPath() + "/customer");
    }
  }

  private boolean verificarAcesso(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute("user") == null) {
      response.sendRedirect(request.getContextPath() + "/auth/login");
      return false;
    }

    String role = (String) session.getAttribute("role");
    if (!"ADMINISTRADOR".equals(role)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return false;
    }

    return true;
  }

  private void listarVisitantes(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    List<Customer> visitantes = customerRepository.findAllWithIngressos();
    request.setAttribute("visitantes", visitantes);
    request.getRequestDispatcher("/WEB-INF/views/visitante/list.jsp").forward(request, response);
  }

  private void exibirFormulario(HttpServletRequest request, HttpServletResponse response, Customer visitante)
      throws ServletException, IOException {
    if (visitante != null) {
      request.setAttribute("visitante", visitante);
    }
    request.getRequestDispatcher("/WEB-INF/views/visitante/edit.jsp").forward(request, response);
  }

  private void editarVisitante(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/customer");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      Optional<Customer> optVisitante = customerRepository.findById(uuid);

      if (optVisitante.isPresent()) {
        request.setAttribute("visitante", optVisitante.get());
        request.getRequestDispatcher("/WEB-INF/views/visitante/edit.jsp").forward(request, response);
      } else {
        response.sendRedirect(request.getContextPath() + "/customer");
      }
    } catch (IllegalArgumentException e) {
      response.sendRedirect(request.getContextPath() + "/customer");
    }
  }

  private void detalhesVisitante(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/customer");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);

      Optional<Customer> optVisitante = customerRepository.findByIdWithIngressos(uuid);

      if (optVisitante.isPresent()) {
        request.setAttribute("visitante", optVisitante.get());
        request.getRequestDispatcher("/WEB-INF/views/visitante/details.jsp").forward(request, response);
      } else {
        response.sendRedirect(request.getContextPath() + "/customer");
      }
    } catch (IllegalArgumentException e) {
      response.sendRedirect(request.getContextPath() + "/customer");
    }
  }

  private void salvarVisitante(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    String nome = request.getParameter("nome");
    String email = request.getParameter("email");
    String password = request.getParameter("password");

    if (nome == null || nome.isEmpty() || email == null || email.isEmpty()) {
      request.setAttribute("erro", "Nome e email são obrigatórios.");
      if (id != null && !id.isEmpty()) {
        Customer visitante = new Customer();
        visitante.setId(UUID.fromString(id));
        visitante.setNome(nome);
        visitante.setEmail(email);
        exibirFormulario(request, response, visitante);
      } else {
        exibirFormulario(request, response, null);
      }
      return;
    }

    try {
      if (id != null && !id.isEmpty()) {
        UUID uuid = UUID.fromString(id);
        Optional<Customer> optVisitante = customerRepository.findById(uuid);

        if (optVisitante.isPresent()) {
          Customer visitante = optVisitante.get();
          visitante.setNome(nome);
          visitante.setEmail(email);
          if (password != null && !password.isEmpty()) {
            visitante.setPassword(password);
          }

          customerRepository.update(visitante);
          response.sendRedirect(request.getContextPath() + "/customer?mensagem=Visitante atualizado com sucesso!");
        } else {
          response.sendRedirect(request.getContextPath() + "/customer");
        }
      } else {
        if (password == null || password.isEmpty()) {
          request.setAttribute("erro", "Senha é obrigatória para novo visitante.");
          exibirFormulario(request, response, null);
          return;
        }

        if (customerRepository.findByEmail(email).isPresent()) {
          request.setAttribute("erro", "Email já cadastrado.");
          Customer visitante = new Customer();
          visitante.setNome(nome);
          visitante.setEmail(email);
          exibirFormulario(request, response, visitante);
          return;
        }

        Customer visitante = new Customer(nome, email, password);
        customerRepository.save(visitante);
        response.sendRedirect(request.getContextPath() + "/customer?mensagem=Visitante cadastrado com sucesso!");
      }
    } catch (Exception e) {
      request.setAttribute("erro", "Erro ao salvar visitante: " + e.getMessage());

      Customer visitante = new Customer();
      if (id != null && !id.isEmpty()) {
        visitante.setId(UUID.fromString(id));
      }
      visitante.setNome(nome);
      visitante.setEmail(email);

      exibirFormulario(request, response, visitante);
    }
  }

  private void excluirVisitante(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/customer");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      customerRepository.delete(uuid);

      String mensagem = java.net.URLEncoder.encode("Visitante excluído com sucesso!", "UTF-8");
      response.sendRedirect(request.getContextPath() + "/customer?mensagem=" + mensagem);
    } catch (Exception e) {
      String erro = java.net.URLEncoder.encode("Erro ao excluir visitante: " + e.getMessage(), "UTF-8");
      response.sendRedirect(request.getContextPath() + "/customer?erro=" + erro);
    }
  }
}
