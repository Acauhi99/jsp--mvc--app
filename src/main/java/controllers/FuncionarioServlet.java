package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Funcionario;
import models.Funcionario.Cargo;
import repositories.FuncionarioRepository;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@WebServlet(urlPatterns = { "/funcionario", "/funcionario/veterinario", "/funcionario/novo", "/funcionario/editar",
    "/funcionario/detalhes", "/funcionario/excluir" })
public class FuncionarioServlet extends BaseServlet {

  private final FuncionarioRepository funcionarioRepository;

  public FuncionarioServlet() {
    this.funcionarioRepository = new FuncionarioRepository();
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
    List<Funcionario> funcionarios = funcionarioRepository.findAll();
    request.setAttribute("funcionarios", funcionarios);
    forwardToView(request, response, "/WEB-INF/views/funcionario/list.jsp");
  }

  private void listarVeterinarios(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    List<Funcionario> veterinarios = funcionarioRepository.findByCargo(Cargo.VETERINARIO);
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
    if (id == null || id.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/funcionario");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      Optional<Funcionario> optFuncionario = funcionarioRepository.findById(uuid);

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
    } catch (IllegalArgumentException e) {
      response.sendRedirect(request.getContextPath() + "/funcionario");
    }
  }

  private void detalhesFuncionario(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/funcionario");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      Optional<Funcionario> optFuncionario = funcionarioRepository.findById(uuid);

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
    } catch (IllegalArgumentException e) {
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

    if (nome == null || nome.isEmpty() || email == null || email.isEmpty() || cargoStr == null || cargoStr.isEmpty()) {
      request.setAttribute("erro", "Nome, email e cargo são obrigatórios.");

      if (id != null && !id.isEmpty()) {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(UUID.fromString(id));
        funcionario.setNome(nome);
        funcionario.setEmail(email);
        try {
          funcionario.setCargo(Cargo.valueOf(cargoStr));
        } catch (IllegalArgumentException e) {
          funcionario.setCargo(Cargo.VETERINARIO);
        }
        exibirFormulario(request, response, funcionario);
      } else {
        exibirFormulario(request, response, null);
      }
      return;
    }

    Cargo cargo;
    try {
      cargo = Cargo.valueOf(cargoStr);
    } catch (IllegalArgumentException e) {
      request.setAttribute("erro", "Cargo inválido.");
      exibirFormulario(request, response, null);
      return;
    }

    try {
      if (id != null && !id.isEmpty()) {
        UUID uuid = UUID.fromString(id);
        Optional<Funcionario> optFuncionario = funcionarioRepository.findById(uuid);

        if (optFuncionario.isPresent()) {
          Funcionario funcionario = optFuncionario.get();
          funcionario.setNome(nome);
          funcionario.setEmail(email);
          funcionario.setCargo(cargo);
          if (password != null && !password.isEmpty()) {
            funcionario.setPassword(password);
          }

          funcionarioRepository.update(funcionario);
          String destino = cargo == Cargo.VETERINARIO ? "/funcionario/veterinario" : "/funcionario";
          response.sendRedirect(request.getContextPath() + destino + "?mensagem="
              + URLEncoder.encode("Funcionário atualizado com sucesso!", "UTF-8"));
        } else {
          response.sendRedirect(request.getContextPath() + "/funcionario");
        }
      } else {
        if (password == null || password.isEmpty()) {
          request.setAttribute("erro", "Senha é obrigatória para novo funcionário.");
          exibirFormulario(request, response, null);
          return;
        }

        if (funcionarioRepository.findByEmail(email).isPresent()) {
          request.setAttribute("erro", "Email já cadastrado.");
          Funcionario funcionario = new Funcionario();
          funcionario.setNome(nome);
          funcionario.setEmail(email);
          funcionario.setCargo(cargo);
          exibirFormulario(request, response, funcionario);
          return;
        }

        Funcionario funcionario = new Funcionario(nome, email, password, cargo);
        funcionarioRepository.save(funcionario);
        String destino = cargo == Cargo.VETERINARIO ? "/funcionario/veterinario" : "/funcionario";
        response.sendRedirect(request.getContextPath() + destino + "?mensagem="
            + URLEncoder.encode("Funcionário cadastrado com sucesso!", "UTF-8"));
      }
    } catch (Exception e) {
      request.setAttribute("erro", "Erro ao salvar funcionário: " + e.getMessage());

      Funcionario funcionario = new Funcionario();
      if (id != null && !id.isEmpty()) {
        funcionario.setId(UUID.fromString(id));
      }
      funcionario.setNome(nome);
      funcionario.setEmail(email);
      funcionario.setCargo(cargo);

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

    if (id == null || id.isEmpty()) {
      response.sendRedirect(request.getContextPath() + origem);
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      Optional<Funcionario> optFunc = funcionarioRepository.findById(uuid);
      boolean isVeterinario = false;

      if (optFunc.isPresent()) {
        isVeterinario = Cargo.VETERINARIO.equals(optFunc.get().getCargo());
      }

      funcionarioRepository.delete(uuid);

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
