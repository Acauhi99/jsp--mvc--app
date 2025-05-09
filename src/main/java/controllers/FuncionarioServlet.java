package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Funcionario;
import models.Funcionario.Cargo;
import dtos.auth.LoginResponse;
import repositories.FuncionarioRepository;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@WebServlet(urlPatterns = { "/funcionario", "/funcionario/veterinario", "/funcionario/novo", "/funcionario/editar",
    "/funcionario/detalhes",
    "/funcionario/excluir" })
public class FuncionarioServlet extends HttpServlet {

  private final FuncionarioRepository funcionarioRepository;

  public FuncionarioServlet() {
    this.funcionarioRepository = new FuncionarioRepository();
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
      case "/funcionario":
        listarFuncionarios(req, resp);
        break;
      case "/funcionario/veterinario":
        listarVeterinarios(req, resp);
        break;
      case "/funcionario/novo":
        exibirFormulario(req, resp, null);
        break;
      case "/funcionario/editar":
        editarFuncionario(req, resp);
        break;
      case "/funcionario/detalhes":
        detalhesFuncionario(req, resp);
        break;
      default:
        resp.sendRedirect(req.getContextPath() + "/funcionario");
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
    if ("/funcionario/novo".equals(path) || "/funcionario/editar".equals(path)) {
      salvarFuncionario(req, resp);
    } else if ("/funcionario/excluir".equals(path)) {
      excluirFuncionario(req, resp);
    } else {
      resp.sendRedirect(req.getContextPath() + "/funcionario");
    }
  }

  private void listarFuncionarios(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    List<Funcionario> funcionarios = funcionarioRepository.findAll();
    req.setAttribute("funcionarios", funcionarios);
    req.getRequestDispatcher("/WEB-INF/views/funcionario/list.jsp").forward(req, resp);
  }

  private void listarVeterinarios(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    List<Funcionario> veterinarios = funcionarioRepository.findByCargo(Cargo.VETERINARIO);
    req.setAttribute("veterinarios", veterinarios);
    req.getRequestDispatcher("/WEB-INF/views/veterinario/list.jsp").forward(req, resp);
  }

  private void exibirFormulario(HttpServletRequest req, HttpServletResponse resp, Funcionario funcionario)
      throws ServletException, IOException {
    String tipoFuncionario = req.getParameter("tipo");
    if (tipoFuncionario == null) {
      tipoFuncionario = "VETERINARIO"; // Valor padrão
    }

    if (funcionario != null) {
      req.setAttribute("funcionario", funcionario);
    }

    req.setAttribute("tipoFuncionario", tipoFuncionario);

    // Verificar qual página de edição deve ser carregada com base no tipo
    if ("VETERINARIO".equals(tipoFuncionario)) {
      req.getRequestDispatcher("/WEB-INF/views/veterinario/edit.jsp").forward(req, resp);
    } else {
      req.getRequestDispatcher("/WEB-INF/views/funcionario/edit.jsp").forward(req, resp);
    }
  }

  private void editarFuncionario(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null || id.isEmpty()) {
      resp.sendRedirect(req.getContextPath() + "/funcionario");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      Optional<Funcionario> optFuncionario = funcionarioRepository.findById(uuid);

      if (optFuncionario.isPresent()) {
        Funcionario funcionario = optFuncionario.get();
        req.setAttribute("funcionario", funcionario);

        // Encaminhar para a página de edição apropriada com base no cargo
        if (Cargo.VETERINARIO.equals(funcionario.getCargo())) {
          req.setAttribute("tipoFuncionario", "VETERINARIO");
          req.getRequestDispatcher("/WEB-INF/views/veterinario/edit.jsp").forward(req, resp);
        } else {
          req.setAttribute("tipoFuncionario", funcionario.getCargo().toString());
          req.getRequestDispatcher("/WEB-INF/views/funcionario/edit.jsp").forward(req, resp);
        }
      } else {
        resp.sendRedirect(req.getContextPath() + "/funcionario");
      }
    } catch (IllegalArgumentException e) {
      resp.sendRedirect(req.getContextPath() + "/funcionario");
    }
  }

  private void detalhesFuncionario(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null || id.isEmpty()) {
      resp.sendRedirect(req.getContextPath() + "/funcionario");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      Optional<Funcionario> optFuncionario = funcionarioRepository.findById(uuid);

      if (optFuncionario.isPresent()) {
        Funcionario funcionario = optFuncionario.get();
        req.setAttribute("funcionario", funcionario);

        // Encaminhar para a página de detalhes apropriada com base no cargo
        if (Cargo.VETERINARIO.equals(funcionario.getCargo())) {
          req.getRequestDispatcher("/WEB-INF/views/veterinario/details.jsp").forward(req, resp);
        } else {
          req.getRequestDispatcher("/WEB-INF/views/funcionario/details.jsp").forward(req, resp);
        }
      } else {
        resp.sendRedirect(req.getContextPath() + "/funcionario");
      }
    } catch (IllegalArgumentException e) {
      resp.sendRedirect(req.getContextPath() + "/funcionario");
    }
  }

  private void salvarFuncionario(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id");
    String nome = req.getParameter("nome");
    String email = req.getParameter("email");
    String password = req.getParameter("password");
    String cargoStr = req.getParameter("cargo");

    if (nome == null || nome.isEmpty() || email == null || email.isEmpty() || cargoStr == null || cargoStr.isEmpty()) {
      req.setAttribute("erro", "Nome, email e cargo são obrigatórios.");

      if (id != null && !id.isEmpty()) {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(UUID.fromString(id));
        funcionario.setNome(nome);
        funcionario.setEmail(email);
        try {
          funcionario.setCargo(Cargo.valueOf(cargoStr));
        } catch (IllegalArgumentException e) {
          // Caso o cargo não seja um valor válido, use um valor padrão
          funcionario.setCargo(Cargo.VETERINARIO);
        }
        exibirFormulario(req, resp, funcionario);
      } else {
        exibirFormulario(req, resp, null);
      }
      return;
    }

    // Converter string para enum Cargo
    Cargo cargo;
    try {
      cargo = Cargo.valueOf(cargoStr);
    } catch (IllegalArgumentException e) {
      req.setAttribute("erro", "Cargo inválido.");
      exibirFormulario(req, resp, null);
      return;
    }

    try {
      if (id != null && !id.isEmpty()) {
        // Editar existente
        UUID uuid = UUID.fromString(id);
        Optional<Funcionario> optFuncionario = funcionarioRepository.findById(uuid);

        if (optFuncionario.isPresent()) {
          Funcionario funcionario = optFuncionario.get();
          funcionario.setNome(nome);
          funcionario.setEmail(email);
          funcionario.setCargo(cargo);
          if (password != null && !password.isEmpty()) {
            funcionario.setPassword(password); // Idealmente, hash a senha
          }

          funcionarioRepository.update(funcionario);
          String destino = cargo == Cargo.VETERINARIO ? "/funcionario/veterinario" : "/funcionario";
          resp.sendRedirect(req.getContextPath() + destino + "?mensagem="
              + URLEncoder.encode("Funcionário atualizado com sucesso!", "UTF-8"));
        } else {
          resp.sendRedirect(req.getContextPath() + "/funcionario");
        }
      } else {
        // Novo funcionário
        if (password == null || password.isEmpty()) {
          req.setAttribute("erro", "Senha é obrigatória para novo funcionário.");
          exibirFormulario(req, resp, null);
          return;
        }

        // Verificar se email já existe
        if (funcionarioRepository.findByEmail(email).isPresent()) {
          req.setAttribute("erro", "Email já cadastrado.");
          Funcionario funcionario = new Funcionario();
          funcionario.setNome(nome);
          funcionario.setEmail(email);
          funcionario.setCargo(cargo);
          exibirFormulario(req, resp, funcionario);
          return;
        }

        Funcionario funcionario = new Funcionario(nome, email, password, cargo);
        funcionarioRepository.save(funcionario);
        String destino = cargo == Cargo.VETERINARIO ? "/funcionario/veterinario" : "/funcionario";
        resp.sendRedirect(req.getContextPath() + destino + "?mensagem="
            + URLEncoder.encode("Funcionário cadastrado com sucesso!", "UTF-8"));
      }
    } catch (Exception e) {
      req.setAttribute("erro", "Erro ao salvar funcionário: " + e.getMessage());

      Funcionario funcionario = new Funcionario();
      if (id != null && !id.isEmpty()) {
        funcionario.setId(UUID.fromString(id));
      }
      funcionario.setNome(nome);
      funcionario.setEmail(email);
      funcionario.setCargo(cargo);

      exibirFormulario(req, resp, funcionario);
    }
  }

  private void excluirFuncionario(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id");
    String origem = req.getParameter("origem");

    if (origem == null) {
      origem = "/funcionario/veterinario";
    }

    if (id == null || id.isEmpty()) {
      resp.sendRedirect(req.getContextPath() + origem);
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

      // Determinar para onde redirecionar com base na origem
      String destino = isVeterinario && "/funcionario/veterinario".equals(origem)
          ? "/funcionario/veterinario"
          : "/funcionario";

      String mensagem = URLEncoder.encode("Funcionário excluído com sucesso!", "UTF-8");
      resp.sendRedirect(req.getContextPath() + destino + "?mensagem=" + mensagem);
    } catch (Exception e) {
      String erro = URLEncoder.encode("Erro ao excluir funcionário: " + e.getMessage(), "UTF-8");
      resp.sendRedirect(req.getContextPath() + origem + "?erro=" + erro);
    }
  }
}
