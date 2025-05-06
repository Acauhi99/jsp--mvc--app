package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Funcionario;
import models.Funcionario.Cargo;
import models.Habitat;
import models.ManutencaoHabitat;
import models.ManutencaoHabitat.StatusManutencao;
import models.ManutencaoHabitat.PrioridadeManutencao;
import models.ManutencaoHabitat.TipoManutencao;
import repositories.HabitatRepository;
import repositories.ManutencaoHabitatRepository;
import repositories.FuncionarioRepository;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet({ "/manutencao", "/manutencao/novo", "/manutencao/salvar", "/manutencao/detalhes", "/manutencao/editar" })
public class ManutencaoHabitatServlet extends HttpServlet {

  private final ManutencaoHabitatRepository manutencaoRepo = new ManutencaoHabitatRepository();
  private final HabitatRepository habitatRepo = new HabitatRepository();
  private final FuncionarioRepository funcionarioRepo = new FuncionarioRepository();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    switch (path) {
      case "/manutencao":
        listarSolicitacoes(req, resp);
        break;
      case "/manutencao/novo":
        mostrarFormulario(req, resp, null);
        break;
      case "/manutencao/editar":
        editarSolicitacao(req, resp);
        break;
      case "/manutencao/detalhes":
        detalhesSolicitacao(req, resp);
        break;
      default:
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    if ("/manutencao/salvar".equals(path)) {
      salvarSolicitacao(req, resp);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  private void listarSolicitacoes(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // Parâmetros de filtro
    String statusParam = req.getParameter("status");
    String prioridadeParam = req.getParameter("prioridade");
    String habitatParam = req.getParameter("habitat");
    String responsavelParam = req.getParameter("responsavel");
    String pageParam = req.getParameter("page");

    // Busca todos habitats para o filtro do select
    List<Habitat> habitats = habitatRepo.findAll();
    req.setAttribute("habitats", habitats);

    // Busca todos manutentores para o filtro do select
    List<Funcionario> manutentores = funcionarioRepo.findByCargo(Cargo.MANUTENCAO);
    req.setAttribute("manutentores", manutentores);

    // Busca todas manutenções
    List<ManutencaoHabitat> manutencoes = manutencaoRepo.findAll();

    // Filtragem
    if (statusParam != null && !statusParam.isEmpty()) {
      manutencoes.removeIf(m -> !m.getStatus().name().equals(statusParam));
    }
    if (prioridadeParam != null && !prioridadeParam.isEmpty()) {
      manutencoes.removeIf(m -> !m.getPrioridade().name().equals(prioridadeParam));
    }
    if (habitatParam != null && !habitatParam.isEmpty()) {
      manutencoes.removeIf(m -> !m.getHabitat().getId().toString().equals(habitatParam));
    }
    if (responsavelParam != null && !responsavelParam.isEmpty()) {
      manutencoes
          .removeIf(m -> m.getResponsavel() == null || !m.getResponsavel().getId().toString().equals(responsavelParam));
    }

    // Paginação
    int pageSize = 10;
    int page = 1;
    if (pageParam != null) {
      try {
        page = Integer.parseInt(pageParam);
        if (page < 1)
          page = 1;
      } catch (NumberFormatException ignored) {
      }
    }
    int totalItems = manutencoes.size();
    int totalPages = (int) Math.ceil((double) totalItems / pageSize);
    int fromIndex = (page - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, totalItems);
    List<ManutencaoHabitat> pageList = manutencoes.subList(
        Math.min(fromIndex, totalItems), Math.min(toIndex, totalItems));

    // Adiciona data de solicitação como atributo
    for (ManutencaoHabitat m : manutencoes) {
      if (m.getDataSolicitacao() != null) {
        req.setAttribute("dataSolicitacao_" + m.getId(), Timestamp.valueOf(m.getDataSolicitacao()));
      }
    }

    req.setAttribute("manutencoes", pageList);
    req.setAttribute("page", page);
    req.setAttribute("totalPages", totalPages);

    req.getRequestDispatcher("/WEB-INF/views/manutencao/list.jsp").forward(req, resp);
  }

  private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, ManutencaoHabitat manutencao)
      throws ServletException, IOException {
    List<Habitat> habitats = habitatRepo.findAll();
    List<Funcionario> manutentores = funcionarioRepo.findByCargo(Cargo.MANUTENCAO);
    req.setAttribute("habitats", habitats);
    req.setAttribute("manutentores", manutentores);
    if (manutencao != null) {
      req.setAttribute("manutencao", manutencao);
    }
    req.getRequestDispatcher("/WEB-INF/views/manutencao/form.jsp").forward(req, resp);
  }

  private void editarSolicitacao(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null) {
      resp.sendRedirect(req.getContextPath() + "/manutencao");
      return;
    }
    Optional<ManutencaoHabitat> manutencaoOpt = manutencaoRepo.findById(UUID.fromString(id));
    if (manutencaoOpt.isPresent()) {
      List<Habitat> habitats = habitatRepo.findAll();
      List<Funcionario> manutentores = funcionarioRepo.findByCargo(Cargo.MANUTENCAO);

      req.setAttribute("habitats", habitats);
      req.setAttribute("manutentores", manutentores);
      req.setAttribute("manutencao", manutencaoOpt.get());
      req.getRequestDispatcher("/WEB-INF/views/manutencao/edit.jsp").forward(req, resp);
    } else {
      resp.sendRedirect(req.getContextPath() + "/manutencao");
    }
  }

  private void detalhesSolicitacao(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null) {
      resp.sendRedirect(req.getContextPath() + "/manutencao");
      return;
    }
    Optional<ManutencaoHabitat> manutencaoOpt = manutencaoRepo.findById(UUID.fromString(id));
    if (manutencaoOpt.isPresent()) {
      req.setAttribute("manutencao", manutencaoOpt.get());
      req.getRequestDispatcher("/WEB-INF/views/manutencao/details.jsp").forward(req, resp);
    } else {
      resp.sendRedirect(req.getContextPath() + "/manutencao");
    }
  }

  private void salvarSolicitacao(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      String id = req.getParameter("id");
      UUID habitatId = UUID.fromString(req.getParameter("habitat"));
      Habitat habitat = habitatRepo.findById(habitatId).orElse(null);
      TipoManutencao tipo = TipoManutencao.valueOf(req.getParameter("tipoManutencao"));
      PrioridadeManutencao prioridade = PrioridadeManutencao.valueOf(req.getParameter("prioridade"));
      String descricao = req.getParameter("descricao");
      LocalDateTime dataProgramada = null;
      if (req.getParameter("dataProgramada") != null && !req.getParameter("dataProgramada").isEmpty()) {
        dataProgramada = LocalDateTime.parse(req.getParameter("dataProgramada"));
      }

      Object userObj = req.getSession().getAttribute("user");
      Funcionario solicitante = null;

      if (userObj instanceof dtos.auth.LoginResponse) {
        dtos.auth.LoginResponse loginResponse = (dtos.auth.LoginResponse) userObj;
        UUID userId = loginResponse.getUserDetails().getId();
        solicitante = funcionarioRepo.findById(userId).orElse(null);
      } else if (userObj instanceof Funcionario) {
        solicitante = (Funcionario) userObj;
      }

      if (solicitante == null) {
        throw new ServletException("Usuário não autenticado corretamente");
      }

      ManutencaoHabitat manutencao;
      if (id != null && !id.isEmpty()) {
        manutencao = manutencaoRepo.findById(UUID.fromString(id)).orElse(new ManutencaoHabitat());
      } else {
        manutencao = new ManutencaoHabitat();
        manutencao.setStatus(StatusManutencao.PENDENTE);
        manutencao.setDataSolicitacao(LocalDateTime.now());
      }
      manutencao.setHabitat(habitat);
      manutencao.setTipoManutencao(tipo);
      manutencao.setPrioridade(prioridade);
      manutencao.setDescricao(descricao);
      manutencao.setDataProgramada(dataProgramada);
      manutencao.setSolicitante(solicitante);

      String responsavelId = req.getParameter("responsavel");
      Funcionario responsavel = null;
      if (responsavelId != null && !responsavelId.isEmpty()) {
        responsavel = funcionarioRepo.findById(UUID.fromString(responsavelId)).orElse(null);
      }
      manutencao.setResponsavel(responsavel);

      if (id != null && !id.isEmpty()) {
        manutencaoRepo.update(manutencao);
      } else {
        manutencaoRepo.save(manutencao);
      }
      resp.sendRedirect(req.getContextPath() + "/manutencao");
    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", "Erro ao salvar solicitação: " + e.getMessage());
      String id = req.getParameter("id");
      if (id != null && !id.isEmpty()) {
        editarSolicitacao(req, resp);
      } else {
        mostrarFormulario(req, resp, null);
      }
    }
  }
}
