package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Habitat;
import models.Habitat.TipoAmbiente;
import repositories.HabitatRepository;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = { "/habitat", "/habitat/novo" })
public class HabitatServlet extends HttpServlet {
  private final HabitatRepository habitatRepo;

  public HabitatServlet() {
    this.habitatRepo = new HabitatRepository();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getServletPath();

    if ("/habitat/novo".equals(path)) {
      if (!verificarAcesso(request, response)) {
        return;
      }

      request.setAttribute("tiposAmbiente", TipoAmbiente.values());
      request.getRequestDispatcher("/WEB-INF/views/habitat/form.jsp").forward(request, response);
      return;
    }

    listarHabitats(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getServletPath();

    if ("/habitat/novo".equals(path)) {
      if (!verificarAcesso(request, response)) {
        return;
      }

      cadastrarHabitat(request, response);
    }
  }

  private boolean verificarAcesso(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
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

  private void listarHabitats(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String tipoAmbiente = request.getParameter("tipoAmbiente");
    List<Habitat> habitats;

    if (tipoAmbiente != null && !tipoAmbiente.isEmpty()) {
      try {
        habitats = habitatRepo.findByTipoAmbiente(TipoAmbiente.valueOf(tipoAmbiente));
      } catch (IllegalArgumentException e) {
        habitats = habitatRepo.findAll();
      }
    } else {
      habitats = habitatRepo.findAll();
    }

    request.setAttribute("habitats", habitats);
    request.setAttribute("tiposAmbiente", TipoAmbiente.values());
    request.setAttribute("tipoAmbiente", tipoAmbiente);
    request.getRequestDispatcher("/WEB-INF/views/habitat/list.jsp").forward(request, response);
  }

  private void cadastrarHabitat(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String nome = request.getParameter("nome");
    String tipoAmbienteStr = request.getParameter("tipoAmbiente");
    String tamanhoStr = request.getParameter("tamanho");
    String capacidadeStr = request.getParameter("capacidadeMaximaAnimais");
    String publicoAcessivelStr = request.getParameter("publicoAcessivel");

    if (nome == null || nome.isEmpty() || tipoAmbienteStr == null || tipoAmbienteStr.isEmpty()) {
      request.setAttribute("erro", "Nome e tipo de ambiente são obrigatórios");
      request.setAttribute("tiposAmbiente", TipoAmbiente.values());
      request.getRequestDispatcher("/WEB-INF/views/habitat/form.jsp").forward(request, response);
      return;
    }

    try {
      Habitat habitat = new Habitat();
      habitat.setNome(nome);
      habitat.setTipoAmbiente(TipoAmbiente.valueOf(tipoAmbienteStr));

      if (tamanhoStr != null && !tamanhoStr.isEmpty()) {
        habitat.setTamanho(Double.parseDouble(tamanhoStr));
      }

      if (capacidadeStr != null && !capacidadeStr.isEmpty()) {
        habitat.setCapacidadeMaximaAnimais(Integer.parseInt(capacidadeStr));
      }

      habitat.setPublicoAcessivel("on".equals(publicoAcessivelStr));

      habitatRepo.save(habitat);

      response.sendRedirect(request.getContextPath() + "/habitat?mensagem=Habitat cadastrado com sucesso!");
    } catch (Exception e) {
      request.setAttribute("erro", "Erro ao cadastrar habitat: " + e.getMessage());
      request.setAttribute("tiposAmbiente", TipoAmbiente.values());
      request.getRequestDispatcher("/WEB-INF/views/habitat/form.jsp").forward(request, response);
    }
  }
}
