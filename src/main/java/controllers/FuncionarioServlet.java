package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Funcionario;
import models.Funcionario.Cargo;
import handlers.FuncionarioHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@WebServlet(urlPatterns = { "/funcionario", "/funcionario/veterinario", "/funcionario/novo", "/funcionario/editar",
    "/funcionario/detalhes", "/funcionario/excluir" })
public class FuncionarioServlet extends BaseServlet {

  private final FuncionarioHandler funcionarioHandler;

  public FuncionarioServlet() {
    this.funcionarioHandler = new FuncionarioHandler();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String path = request.getServletPath();

    if (!requireRole(request, response, ROLE_ADMINISTRADOR)) {
      return;
    }

    switch (path) {
      case "/funcionario":
        listarFuncionarios(request, response);
        break;
      case "/funcionario/veterinario":
        listarVeterinarios(request, response);
        break;
      case "/funcionario/novo":
        exibirFormulario(request, response, null);
        break;
      case "/funcionario/editar":
        editarFuncionario(request, response);
        break;
      case "/funcionario/detalhes":
        detalhesFuncionario(request, response);
        break;
      default:
        response.sendRedirect(request.getContextPath() + "/funcionario");
        break;
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String path = request.getServletPath();

    if (!requireRole(request, response, ROLE_ADMINISTRADOR)) {
      return;
    }

    if ("/funcionario/novo".equals(path) || "/funcionario/editar".equals(path)) {
      salvarFuncionario(request, response);
    } else if ("/funcionario/excluir".equals(path)) {
      excluirFuncionario(request, response);
    } else {
      response.sendRedirect(request.getContextPath() + "/funcionario");
    }
  }

  private void listarFuncionarios(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    List<Funcionario> funcionarios = funcionarioHandler.listarTodosFuncionarios();
    request.setAttribute("funcionarios", funcionarios);
    forwardToView(request, response, "/WEB-INF/views/funcionario/list.jsp");
  }

  private void listarVeterinarios(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    List<Funcionario> veterinarios = funcionarioHandler.listarPorCargo(Cargo.VETERINARIO);
    request.setAttribute("veterinarios", veterinarios);
    forwardToView(request, response, "/WEB-INF/views/veterinario/list.jsp");
  }

  private void exibirFormulario(HttpServletRequest request, HttpServletResponse response, Funcionario funcionario)
      throws ServletException, IOException {
    String tipoFuncionario = request.getParameter("tipo");
    if (tipoFuncionario == null) {
      tipoFuncionario = "VETERINARIO";
    }

    if (funcionario != null) {
      request.setAttribute("funcionario", funcionario);
    }

    request.setAttribute("tipoFuncionario", tipoFuncionario);

    if ("VETERINARIO".equals(tipoFuncionario)) {
      forwardToView(request, response, "/WEB-INF/views/veterinario/edit.jsp");
    } else {
      forwardToView(request, response, "/WEB-INF/views/funcionario/edit.jsp");
    }
  }

  private void editarFuncionario(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    Optional<Funcionario> optFuncionario = funcionarioHandler.buscarPorId(id);

    if (optFuncionario.isPresent()) {
      Funcionario funcionario = optFuncionario.get();
      request.setAttribute("funcionario", funcionario);

      if (Cargo.VETERINARIO.equals(funcionario.getCargo())) {
        request.setAttribute("tipoFuncionario", "VETERINARIO");
        forwardToView(request, response, "/WEB-INF/views/veterinario/edit.jsp");
      } else {
        request.setAttribute("tipoFuncionario", funcionario.getCargo().toString());
        forwardToView(request, response, "/WEB-INF/views/funcionario/edit.jsp");
      }
    } else {
      response.sendRedirect(request.getContextPath() + "/funcionario");
    }
  }

  private void detalhesFuncionario(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    Optional<Funcionario> optFuncionario = funcionarioHandler.buscarPorId(id);

    if (optFuncionario.isPresent()) {
      Funcionario funcionario = optFuncionario.get();
      request.setAttribute("funcionario", funcionario);

      if (Cargo.VETERINARIO.equals(funcionario.getCargo())) {
        forwardToView(request, response, "/WEB-INF/views/veterinario/details.jsp");
      } else {
        forwardToView(request, response, "/WEB-INF/views/funcionario/details.jsp");
      }
    } else {
      response.sendRedirect(request.getContextPath() + "/funcionario");
    }
  }

  private void salvarFuncionario(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    String nome = request.getParameter("nome");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String cargoStr = request.getParameter("cargo");

    try {
      Funcionario funcionario;

      if (id != null && !id.isEmpty()) {
        funcionario = funcionarioHandler.atualizarFuncionario(id, nome, email, password, cargoStr);
      } else {
        funcionario = funcionarioHandler.criarFuncionario(nome, email, password, cargoStr);
      }

      String destino = funcionario.getCargo() == Cargo.VETERINARIO ? "/funcionario/veterinario" : "/funcionario";
      String acao = (id != null && !id.isEmpty()) ? "atualizado" : "cadastrado";
      String mensagem = URLEncoder.encode("Funcionário " + acao + " com sucesso!", "UTF-8");

      response.sendRedirect(request.getContextPath() + destino + "?mensagem=" + mensagem);
    } catch (Exception e) {
      request.setAttribute("erro", e.getMessage());

      Funcionario funcionario = new Funcionario();
      if (id != null && !id.isEmpty()) {
        try {
          funcionario.setId(UUID.fromString(id));
        } catch (IllegalArgumentException ignored) {
        }
      }
      funcionario.setNome(nome != null ? nome : "");
      funcionario.setEmail(email != null ? email : "");

      try {
        if (cargoStr != null && !cargoStr.isEmpty()) {
          funcionario.setCargo(Cargo.valueOf(cargoStr));
        } else {
          funcionario.setCargo(Cargo.VETERINARIO);
        }
      } catch (IllegalArgumentException ignored) {
        funcionario.setCargo(Cargo.VETERINARIO);
      }

      exibirFormulario(request, response, funcionario);
    }
  }

  private void excluirFuncionario(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    String origem = request.getParameter("origem");

    if (origem == null) {
      origem = "/funcionario/veterinario";
    }

    Optional<Funcionario> optFunc = funcionarioHandler.buscarPorId(id);
    boolean isVeterinario = false;

    if (optFunc.isPresent()) {
      isVeterinario = Cargo.VETERINARIO.equals(optFunc.get().getCargo());
    }

    try {
      funcionarioHandler.excluirFuncionario(id);

      String destino = isVeterinario && "/funcionario/veterinario".equals(origem)
          ? "/funcionario/veterinario"
          : "/funcionario";

      String mensagem = URLEncoder.encode("Funcionário excluído com sucesso!", "UTF-8");
      response.sendRedirect(request.getContextPath() + destino + "?mensagem=" + mensagem);
    } catch (Exception e) {
      String erro = URLEncoder.encode("Erro ao excluir funcionário: " + e.getMessage(), "UTF-8");
      response.sendRedirect(request.getContextPath() + origem + "?erro=" + erro);
    }
  }
}
