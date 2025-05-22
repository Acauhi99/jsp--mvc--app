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
public class ManutencaoHabitatServlet extends BaseServlet {

  private final ManutencaoHabitatRepository manutencaoRepo = new ManutencaoHabitatRepository();
  private final HabitatRepository habitatRepo = new HabitatRepository();
  private final FuncionarioRepository funcionarioRepo = new FuncionarioRepository();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!requireAnyRole(req, resp, ROLE_MANUTENCAO, ROLE_ADMINISTRADOR)) {
      return;
    }

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
    if (!requireAnyRole(req, resp, ROLE_MANUTENCAO, ROLE_ADMINISTRADOR)) {
      return;
    }

    String path = req.getServletPath();
    if ("/manutencao/salvar".equals(path)) {
      salvarSolicitacao(req, resp);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  private void listarSolicitacoes(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    StatusManutencao status = null;
    PrioridadeManutencao prioridade = null;
    UUID habitatId = null;
    UUID responsavelId = null;

    String statusParam = req.getParameter("status");
    if (statusParam != null && !statusParam.isEmpty()) {
      try {
        status = StatusManutencao.valueOf(statusParam);
      } catch (IllegalArgumentException e) {
      }
    }

    String prioridadeParam = req.getParameter("prioridade");
    if (prioridadeParam != null && !prioridadeParam.isEmpty()) {
      try {
        prioridade = PrioridadeManutencao.valueOf(prioridadeParam);
      } catch (IllegalArgumentException e) {
      }
    }

    String habitatParam = req.getParameter("habitat");
    if (habitatParam != null && !habitatParam.isEmpty()) {
      try {
        habitatId = UUID.fromString(habitatParam);
      } catch (IllegalArgumentException e) {
      }
    }

    String responsavelParam = req.getParameter("responsavel");
    if (responsavelParam != null && !responsavelParam.isEmpty()) {
      try {
        responsavelId = UUID.fromString(responsavelParam);
      } catch (IllegalArgumentException e) {
      }
    }

    List<ManutencaoHabitat> manutencoes = manutencaoRepo.findWithFilters(status, prioridade, habitatId, responsavelId);

    List<ManutencaoHabitat> pageList = aplicarPaginacao(req, resp, manutencoes);

    for (ManutencaoHabitat m : pageList) {
      if (m.getDataSolicitacao() != null) {
        req.setAttribute("dataSolicitacao_" + m.getId(), Timestamp.valueOf(m.getDataSolicitacao()));
      }
    }

    carregarDadosSelecao(req);
    req.setAttribute("manutencoes", pageList);
    forwardToView(req, resp, "/WEB-INF/views/manutencao/list.jsp");
  }

  private List<ManutencaoHabitat> aplicarPaginacao(HttpServletRequest req, HttpServletResponse resp,
      List<ManutencaoHabitat> lista) {
    int pageSize = 10;
    int page = getPageParam(req);
    int totalItems = lista.size();
    int totalPages = (int) Math.ceil((double) totalItems / pageSize);
    int fromIndex = (page - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, totalItems);

    req.setAttribute("page", page);
    req.setAttribute("totalPages", totalPages);

    if (totalItems == 0) {
      return new ArrayList<>();
    }

    return lista.subList(Math.min(fromIndex, totalItems), Math.min(toIndex, totalItems));
  }

  private int getPageParam(HttpServletRequest req) {
    String pageParam = req.getParameter("page");
    if (pageParam == null) {
      return 1;
    }

    try {
      int page = Integer.parseInt(pageParam);
      return page < 1 ? 1 : page;
    } catch (NumberFormatException e) {
      return 1;
    }
  }

  private void carregarDadosSelecao(HttpServletRequest req) {
    List<Habitat> habitats = habitatRepo.findAll();
    List<Funcionario> manutentores = funcionarioRepo.findByCargo(Cargo.MANUTENCAO);

    req.setAttribute("habitats", habitats);
    req.setAttribute("manutentores", manutentores);
    req.setAttribute("tiposManutencao", TipoManutencao.values());
    req.setAttribute("prioridades", PrioridadeManutencao.values());
    req.setAttribute("statusOpcoes", StatusManutencao.values());
  }

  private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, ManutencaoHabitat manutencao)
      throws ServletException, IOException {
    carregarDadosSelecao(req);

    if (manutencao != null) {
      req.setAttribute("manutencao", manutencao);
    }

    forwardToView(req, resp, "/WEB-INF/views/manutencao/form.jsp");
  }

  private void editarSolicitacao(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null) {
      resp.sendRedirect(req.getContextPath() + "/manutencao");
      return;
    }

    try {
      UUID uuid = UUID.fromString(id);
      Optional<ManutencaoHabitat> manutencaoOpt = manutencaoRepo.findById(uuid);

      if (manutencaoOpt.isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/manutencao");
        return;
      }

      carregarDadosSelecao(req);
      req.setAttribute("manutencao", manutencaoOpt.get());
      forwardToView(req, resp, "/WEB-INF/views/manutencao/edit.jsp");

    } catch (IllegalArgumentException e) {
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

    try {
      UUID uuid = UUID.fromString(id);
      Optional<ManutencaoHabitat> manutencaoOpt = manutencaoRepo.findById(uuid);

      if (manutencaoOpt.isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/manutencao");
        return;
      }

      req.setAttribute("manutencao", manutencaoOpt.get());
      forwardToView(req, resp, "/WEB-INF/views/manutencao/details.jsp");

    } catch (IllegalArgumentException e) {
      resp.sendRedirect(req.getContextPath() + "/manutencao");
    }
  }

  private void salvarSolicitacao(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      String id = req.getParameter("id");
      Habitat habitat = obterHabitat(req);
      TipoManutencao tipo = obterTipoManutencao(req);
      PrioridadeManutencao prioridade = obterPrioridade(req);
      String descricao = req.getParameter("descricao");
      LocalDateTime dataProgramada = obterDataProgramada(req);
      Funcionario solicitante = obterSolicitante(req);

      if (solicitante == null) {
        throw new ServletException("Usuário não autenticado corretamente");
      }

      ManutencaoHabitat manutencao = criarOuAtualizarManutencao(id, habitat, tipo, prioridade,
          descricao, dataProgramada, solicitante);
      Funcionario responsavel = obterResponsavel(req);
      manutencao.setResponsavel(responsavel);

      if (id != null && !id.isEmpty()) {
        manutencaoRepo.update(manutencao);
      } else {
        manutencaoRepo.save(manutencao);
      }

      resp.sendRedirect(req.getContextPath() + "/manutencao");

    } catch (Exception e) {
      req.setAttribute("error", "Erro ao salvar solicitação: " + e.getMessage());
      String id = req.getParameter("id");

      if (id != null && !id.isEmpty()) {
        editarSolicitacao(req, resp);
      } else {
        mostrarFormulario(req, resp, null);
      }
    }
  }

  private Habitat obterHabitat(HttpServletRequest req) throws Exception {
    String habitatId = req.getParameter("habitat");
    if (habitatId == null || habitatId.isEmpty()) {
      throw new Exception("Habitat é obrigatório");
    }

    UUID uuid = UUID.fromString(habitatId);
    return habitatRepo.findById(uuid)
        .orElseThrow(() -> new Exception("Habitat não encontrado"));
  }

  private TipoManutencao obterTipoManutencao(HttpServletRequest req) {
    String tipoStr = req.getParameter("tipoManutencao");
    return TipoManutencao.valueOf(tipoStr);
  }

  private PrioridadeManutencao obterPrioridade(HttpServletRequest req) {
    String prioridadeStr = req.getParameter("prioridade");
    return PrioridadeManutencao.valueOf(prioridadeStr);
  }

  private LocalDateTime obterDataProgramada(HttpServletRequest req) {
    String dataStr = req.getParameter("dataProgramada");
    if (dataStr == null || dataStr.isEmpty()) {
      return null;
    }
    return LocalDateTime.parse(dataStr);
  }

  private Funcionario obterSolicitante(HttpServletRequest req) {
    HttpSession session = req.getSession(false);
    if (session == null) {
      return null;
    }

    Object userObj = session.getAttribute("user");
    if (userObj instanceof Funcionario) {
      return (Funcionario) userObj;
    }

    Object userIdObj = session.getAttribute("userId");
    if (userIdObj instanceof UUID) {
      UUID userId = (UUID) userIdObj;
      return funcionarioRepo.findById(userId).orElse(null);
    }

    if (userIdObj instanceof String) {
      try {
        UUID userId = UUID.fromString((String) userIdObj);
        return funcionarioRepo.findById(userId).orElse(null);
      } catch (IllegalArgumentException e) {
        return null;
      }
    }

    return null;
  }

  private Funcionario obterResponsavel(HttpServletRequest req) {
    String responsavelId = req.getParameter("responsavel");
    if (responsavelId == null || responsavelId.isEmpty()) {
      return null;
    }

    UUID uuid = UUID.fromString(responsavelId);
    return funcionarioRepo.findById(uuid).orElse(null);
  }

  private ManutencaoHabitat criarOuAtualizarManutencao(String id, Habitat habitat,
      TipoManutencao tipo,
      PrioridadeManutencao prioridade,
      String descricao,
      LocalDateTime dataProgramada,
      Funcionario solicitante) {
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

    return manutencao;
  }
}