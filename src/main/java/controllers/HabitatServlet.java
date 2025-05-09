package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Habitat;
import models.Habitat.TipoAmbiente;
import dtos.auth.LoginResponse;
import repositories.HabitatRepository;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = { "/habitat", "/habitat/novo" })
public class HabitatServlet extends HttpServlet {
  private final HabitatRepository habitatRepo = new HabitatRepository();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String path = req.getServletPath();

    if ("/habitat/novo".equals(path)) {
      // Verificar se é administrador
      HttpSession session = req.getSession(false);
      if (session != null && session.getAttribute("user") != null) {
        LoginResponse login = (LoginResponse) session.getAttribute("user");
        if (!"ADMINISTRADOR".equals(login.getRole())) {
          resp.sendError(HttpServletResponse.SC_FORBIDDEN);
          return;
        }
      } else {
        resp.sendRedirect(req.getContextPath() + "/auth/login");
        return;
      }

      // Preparar dados para o formulário
      req.setAttribute("tiposAmbiente", TipoAmbiente.values());
      req.getRequestDispatcher("/WEB-INF/views/habitat/form.jsp").forward(req, resp);
      return;
    }

    // Código existente para listagem de habitats
    String tipoAmbiente = req.getParameter("tipoAmbiente");
    List<Habitat> habitats;
    if (tipoAmbiente != null && !tipoAmbiente.isEmpty()) {
      habitats = habitatRepo.findByTipoAmbiente(TipoAmbiente.valueOf(tipoAmbiente));
    } else {
      habitats = habitatRepo.findAll();
    }
    req.setAttribute("habitats", habitats);
    req.setAttribute("tiposAmbiente", TipoAmbiente.values());
    req.setAttribute("tipoAmbiente", tipoAmbiente);
    req.getRequestDispatcher("/WEB-INF/views/habitat/list.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String path = req.getServletPath();

    if ("/habitat/novo".equals(path)) {
      // Verificar se é administrador
      HttpSession session = req.getSession(false);
      if (session != null && session.getAttribute("user") != null) {
        LoginResponse login = (LoginResponse) session.getAttribute("user");
        if (!"ADMINISTRADOR".equals(login.getRole())) {
          resp.sendError(HttpServletResponse.SC_FORBIDDEN);
          return;
        }
      } else {
        resp.sendRedirect(req.getContextPath() + "/auth/login");
        return;
      }

      // Obter parâmetros do formulário
      String nome = req.getParameter("nome");
      String tipoAmbienteStr = req.getParameter("tipoAmbiente");
      String tamanhoStr = req.getParameter("tamanho");
      String capacidadeStr = req.getParameter("capacidadeMaximaAnimais");
      String publicoAcessivelStr = req.getParameter("publicoAcessivel");

      // Validar campos obrigatórios
      if (nome == null || nome.isEmpty() || tipoAmbienteStr == null || tipoAmbienteStr.isEmpty()) {
        req.setAttribute("erro", "Nome e tipo de ambiente são obrigatórios");
        req.setAttribute("tiposAmbiente", TipoAmbiente.values());
        req.getRequestDispatcher("/WEB-INF/views/habitat/form.jsp").forward(req, resp);
        return;
      }

      try {
        // Criar objeto Habitat
        Habitat habitat = new Habitat();
        habitat.setNome(nome);
        habitat.setTipoAmbiente(TipoAmbiente.valueOf(tipoAmbienteStr));

        // Converter e configurar campos numéricos
        if (tamanhoStr != null && !tamanhoStr.isEmpty()) {
          habitat.setTamanho(Double.parseDouble(tamanhoStr));
        }

        if (capacidadeStr != null && !capacidadeStr.isEmpty()) {
          habitat.setCapacidadeMaximaAnimais(Integer.parseInt(capacidadeStr));
        }

        // Configurar boolean
        habitat.setPublicoAcessivel("on".equals(publicoAcessivelStr));

        // Salvar no banco
        habitatRepo.save(habitat);

        // Redirecionar para a lista com mensagem de sucesso
        resp.sendRedirect(req.getContextPath() + "/habitat?mensagem=Habitat cadastrado com sucesso!");
      } catch (Exception e) {
        // Em caso de erro, volta para o formulário
        req.setAttribute("erro", "Erro ao cadastrar habitat: " + e.getMessage());
        req.setAttribute("tiposAmbiente", TipoAmbiente.values());
        req.getRequestDispatcher("/WEB-INF/views/habitat/form.jsp").forward(req, resp);
      }
    }
  }
}
