package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Customer;
import handlers.CustomerHandler;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebServlet(urlPatterns = { "/customer", "/customer/novo", "/customer/editar", "/customer/detalhes",
    "/customer/excluir" })
public class CustomerServlet extends BaseServlet {

  private final CustomerHandler customerHandler;

  public CustomerServlet() {
    this.customerHandler = new CustomerHandler();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String path = request.getServletPath();

    if (!requireRole(request, response, ROLE_ADMINISTRADOR)) {
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

    if (!requireRole(request, response, ROLE_ADMINISTRADOR)) {
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

  private void listarVisitantes(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    List<Customer> visitantes = customerHandler.listarTodosVisitantes();
    request.setAttribute("visitantes", visitantes);
    forwardToView(request, response, "/WEB-INF/views/visitante/list.jsp");
  }

  private void exibirFormulario(HttpServletRequest request, HttpServletResponse response, Customer visitante)
      throws ServletException, IOException {
    if (visitante != null) {
      request.setAttribute("visitante", visitante);
    }
    forwardToView(request, response, "/WEB-INF/views/visitante/edit.jsp");
  }

  private void editarVisitante(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");

    Optional<Customer> optVisitante = customerHandler.buscarVisitantePorId(id);

    if (optVisitante.isPresent()) {
      request.setAttribute("visitante", optVisitante.get());
      forwardToView(request, response, "/WEB-INF/views/visitante/edit.jsp");
    } else {
      response.sendRedirect(request.getContextPath() + "/customer");
    }
  }

  private void detalhesVisitante(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");

    Optional<Customer> optVisitante = customerHandler.buscarVisitantePorIdComIngressos(id);

    if (optVisitante.isPresent()) {
      request.setAttribute("visitante", optVisitante.get());
      forwardToView(request, response, "/WEB-INF/views/visitante/details.jsp");
    } else {
      response.sendRedirect(request.getContextPath() + "/customer");
    }
  }

  private void salvarVisitante(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    String nome = request.getParameter("nome");
    String email = request.getParameter("email");
    String password = request.getParameter("password");

    try {
      if (id != null && !id.isEmpty()) {
        customerHandler.atualizarVisitante(id, nome, email, password);
        response.sendRedirect(request.getContextPath() + "/customer?mensagem=Visitante atualizado com sucesso!");
      } else {
        customerHandler.criarVisitante(nome, email, password);
        response.sendRedirect(request.getContextPath() + "/customer?mensagem=Visitante cadastrado com sucesso!");
      }
    } catch (Exception e) {
      request.setAttribute("erro", e.getMessage());

      Customer visitante = new Customer();
      if (id != null && !id.isEmpty()) {
        try {
          visitante.setId(UUID.fromString(id));
        } catch (IllegalArgumentException ignored) {
        }
      }
      visitante.setNome(nome != null ? nome : "");
      visitante.setEmail(email != null ? email : "");

      exibirFormulario(request, response, visitante);
    }
  }

  private void excluirVisitante(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");

    try {
      customerHandler.excluirVisitante(id);
      String mensagem = java.net.URLEncoder.encode("Visitante excluído com sucesso!", "UTF-8");
      response.sendRedirect(request.getContextPath() + "/customer?mensagem=" + mensagem);
    } catch (Exception e) {
      String erro = java.net.URLEncoder.encode("Erro ao excluir visitante: " + e.getMessage(), "UTF-8");
      response.sendRedirect(request.getContextPath() + "/customer?erro=" + erro);
    }
  }
}
