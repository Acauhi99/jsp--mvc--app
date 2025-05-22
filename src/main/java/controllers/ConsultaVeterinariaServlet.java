package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Animal;
import models.ConsultaVeterinaria;
import models.ConsultaVeterinaria.StatusConsulta;
import models.ConsultaVeterinaria.TipoConsulta;
import models.Funcionario;
import models.Funcionario.Cargo;
import repositories.AnimalRepository;
import repositories.ConsultaVeterinariaRepository;
import repositories.FuncionarioRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet("/consulta/*")
public class ConsultaVeterinariaServlet extends BaseServlet {

  private final ConsultaVeterinariaRepository consultaRepository;
  private final AnimalRepository animalRepository;
  private final FuncionarioRepository funcionarioRepository;

  public ConsultaVeterinariaServlet() {
    this.consultaRepository = new ConsultaVeterinariaRepository();
    this.animalRepository = new AnimalRepository();
    this.funcionarioRepository = new FuncionarioRepository();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    if (!requireAnyRole(request, response, ROLE_VETERINARIO, ROLE_ADMINISTRADOR)) {
      return;
    }

    String pathInfo = request.getPathInfo();

    try {
      if (pathInfo == null || pathInfo.equals("/")) {
        listarConsultas(request, response);
        return;
      }

      if (pathInfo.equals("/nova")) {
        mostrarFormulario(request, response, null);
        return;
      }

      if (pathInfo.equals("/editar")) {
        editarConsulta(request, response);
        return;
      }

      if (pathInfo.equals("/detalhes")) {
        detalhesConsulta(request, response);
        return;
      }

      if (pathInfo.equals("/historico")) {
        mostrarHistorico(request, response);
        return;
      }

      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro: " + e.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    if (!requireAnyRole(request, response, ROLE_VETERINARIO, ROLE_ADMINISTRADOR)) {
      return;
    }

    String pathInfo = request.getPathInfo();

    try {
      if (pathInfo != null && pathInfo.equals("/salvar")) {
        salvarConsulta(request, response);
        return;
      }

      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      request.setAttribute("error", "Erro ao processar solicitação: " + e.getMessage());
      doGet(request, response);
    }
  }

  private void listarConsultas(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    carregarDadosParaFiltros(request);

    String statusParam = request.getParameter("status");
    String animalParam = request.getParameter("animal");
    String tipoParam = request.getParameter("tipo");
    String veterinarioParam = request.getParameter("veterinario");

    List<ConsultaVeterinaria> consultas = obterConsultasFiltradas(
        statusParam, animalParam, tipoParam, veterinarioParam);

    int page = getPageParameter(request);
    int pageSize = 10;
    PageResult<ConsultaVeterinaria> pageResult = paginarResultados(consultas, page, pageSize);

    request.setAttribute("consultas", pageResult.items);
    request.setAttribute("page", pageResult.currentPage);
    request.setAttribute("totalPages", pageResult.totalPages);

    forwardToView(request, response, "/WEB-INF/views/consulta/list.jsp");
  }

  private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
      ConsultaVeterinaria consulta)
      throws ServletException, IOException {
    carregarDadosParaFormulario(request);

    if (consulta != null) {
      request.setAttribute("consulta", consulta);
    }

    forwardToView(request, response, "/WEB-INF/views/consulta/form.jsp");
  }

  private void editarConsulta(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/consulta");
      return;
    }

    try {
      UUID consultaId = UUID.fromString(id);
      Optional<ConsultaVeterinaria> consultaOpt = consultaRepository.findById(consultaId);

      if (consultaOpt.isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/consulta");
        return;
      }

      carregarDadosParaFormulario(request);
      request.setAttribute("consulta", consultaOpt.get());
      forwardToView(request, response, "/WEB-INF/views/consulta/edit.jsp");

    } catch (IllegalArgumentException e) {
      response.sendRedirect(request.getContextPath() + "/consulta");
    }
  }

  private void detalhesConsulta(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/consulta");
      return;
    }

    try {
      UUID consultaId = UUID.fromString(id);
      Optional<ConsultaVeterinaria> consultaOpt = consultaRepository.findById(consultaId);

      if (consultaOpt.isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/consulta");
        return;
      }

      request.setAttribute("consulta", consultaOpt.get());
      forwardToView(request, response, "/WEB-INF/views/consulta/details.jsp");

    } catch (IllegalArgumentException e) {
      response.sendRedirect(request.getContextPath() + "/consulta");
    }
  }

  private void mostrarHistorico(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    List<Animal> animais = animalRepository.findAll();
    request.setAttribute("animais", animais);

    String animalId = request.getParameter("animal");
    if (animalId != null && !animalId.isEmpty()) {
      try {
        UUID id = UUID.fromString(animalId);
        Optional<Animal> animalOpt = animalRepository.findById(id);

        if (animalOpt.isPresent()) {
          Animal animal = animalOpt.get();
          List<ConsultaVeterinaria> historicoConsultas = consultaRepository.findByAnimalId(animal.getId());

          request.setAttribute("animal", animal);
          request.setAttribute("historicoConsultas", historicoConsultas);
        }
      } catch (IllegalArgumentException e) {
      }
    }

    forwardToView(request, response, "/WEB-INF/views/consulta/historico.jsp");
  }

  private void salvarConsulta(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      String id = request.getParameter("id");

      UUID animalId = UUID.fromString(request.getParameter("animal"));
      Animal animal = animalRepository.findById(animalId)
          .orElseThrow(() -> new Exception("Animal não encontrado"));

      TipoConsulta tipo = TipoConsulta.valueOf(request.getParameter("tipoConsulta"));
      StatusConsulta status = StatusConsulta.valueOf(request.getParameter("status"));

      LocalDateTime dataHora = obterDataHora(request.getParameter("dataHora"));

      Funcionario veterinario = obterVeterinario(request);

      String diagnostico = request.getParameter("diagnostico");
      String tratamento = request.getParameter("tratamento");
      String medicamentos = request.getParameter("medicamentos");
      String observacoes = request.getParameter("observacoes");

      boolean acompanhamentoNecessario = request.getParameter("acompanhamentoNecessario") != null;
      LocalDateTime dataRetorno = null;
      if (acompanhamentoNecessario) {
        dataRetorno = obterDataHora(request.getParameter("dataRetorno"));
      }

      ConsultaVeterinaria consulta = criarOuAtualizarConsulta(id, animal, tipo, status, dataHora,
          veterinario, diagnostico, tratamento, medicamentos, observacoes,
          acompanhamentoNecessario, dataRetorno);

      if (id != null && !id.isEmpty()) {
        consultaRepository.update(consulta);
      } else {
        consultaRepository.save(consulta);
      }

      response.sendRedirect(request.getContextPath() + "/consulta");

    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("error", "Erro ao salvar consulta: " + e.getMessage());

      String id = request.getParameter("id");
      if (id != null && !id.isEmpty()) {
        editarConsulta(request, response);
      } else {
        mostrarFormulario(request, response, null);
      }
    }
  }

  private void carregarDadosParaFiltros(HttpServletRequest request) {
    List<Animal> animais = animalRepository.findAll();
    List<Funcionario> veterinarios = funcionarioRepository.findByCargo(Cargo.VETERINARIO);

    request.setAttribute("animais", animais);
    request.setAttribute("veterinarios", veterinarios);
    request.setAttribute("tiposConsulta", TipoConsulta.values());
    request.setAttribute("statusConsulta", StatusConsulta.values());
  }

  private void carregarDadosParaFormulario(HttpServletRequest request) {
    List<Animal> animais = animalRepository.findAll();
    List<Funcionario> veterinarios = funcionarioRepository.findByCargo(Cargo.VETERINARIO);

    request.setAttribute("animais", animais);
    request.setAttribute("veterinarios", veterinarios);
    request.setAttribute("tiposConsulta", TipoConsulta.values());
    request.setAttribute("statusConsulta", StatusConsulta.values());
  }

  private List<ConsultaVeterinaria> obterConsultasFiltradas(
      String statusParam, String animalParam, String tipoParam, String veterinarioParam) {

    List<ConsultaVeterinaria> consultas = consultaRepository.findAll();

    if (statusParam != null && !statusParam.isEmpty()) {
      consultas.removeIf(c -> !c.getStatus().name().equals(statusParam));
    }

    if (animalParam != null && !animalParam.isEmpty()) {
      consultas.removeIf(c -> !c.getAnimal().getId().toString().equals(animalParam));
    }

    if (tipoParam != null && !tipoParam.isEmpty()) {
      consultas.removeIf(c -> !c.getTipoConsulta().name().equals(tipoParam));
    }

    if (veterinarioParam != null && !veterinarioParam.isEmpty()) {
      consultas.removeIf(c -> c.getVeterinario() == null ||
          !c.getVeterinario().getId().toString().equals(veterinarioParam));
    }

    return consultas;
  }

  private static class PageResult<T> {
    final List<T> items;
    final int currentPage;
    final int totalPages;

    PageResult(List<T> items, int currentPage, int totalPages) {
      this.items = items;
      this.currentPage = currentPage;
      this.totalPages = totalPages;
    }
  }

  private <T> PageResult<T> paginarResultados(List<T> items, int page, int pageSize) {
    int totalItems = items.size();
    int totalPages = (int) Math.ceil((double) totalItems / pageSize);

    int fromIndex = (page - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, totalItems);

    List<T> pageItems = totalItems > 0
        ? items.subList(Math.min(fromIndex, totalItems), Math.min(toIndex, totalItems))
        : new ArrayList<>();

    return new PageResult<>(pageItems, page, totalPages);
  }

  private int getPageParameter(HttpServletRequest request) {
    int page = 1;
    try {
      String pageParam = request.getParameter("page");
      if (pageParam != null && !pageParam.isEmpty()) {
        page = Integer.parseInt(pageParam);
        if (page < 1) {
          page = 1;
        }
      }
    } catch (NumberFormatException ignored) {
    }
    return page;
  }

  private LocalDateTime obterDataHora(String dataHoraStr) {
    if (dataHoraStr != null && !dataHoraStr.isEmpty()) {
      return LocalDateTime.parse(dataHoraStr);
    }
    return null;
  }

  private Funcionario obterVeterinario(HttpServletRequest request) throws Exception {
    String veterinarioId = request.getParameter("veterinario");

    if (veterinarioId != null && !veterinarioId.isEmpty()) {
      return funcionarioRepository.findById(UUID.fromString(veterinarioId))
          .orElse(null);
    }

    HttpSession session = request.getSession(false);
    if (session != null) {
      Object userIdObj = session.getAttribute("userId");

      if (userIdObj instanceof UUID) {
        UUID userId = (UUID) userIdObj;
        Funcionario currentUser = funcionarioRepository.findById(userId).orElse(null);

        if (currentUser != null && currentUser.getCargo() == Cargo.VETERINARIO) {
          return currentUser;
        }
      }

      else {
        Object userObj = session.getAttribute("user");
        if (userObj instanceof Funcionario) {
          Funcionario currentUser = (Funcionario) userObj;

          if (currentUser.getCargo() == Cargo.VETERINARIO) {
            return currentUser;
          }
        }
      }
    }

    return null;
  }

  private ConsultaVeterinaria criarOuAtualizarConsulta(
      String id, Animal animal, TipoConsulta tipo, StatusConsulta status,
      LocalDateTime dataHora, Funcionario veterinario, String diagnostico,
      String tratamento, String medicamentos, String observacoes,
      boolean acompanhamentoNecessario, LocalDateTime dataRetorno) throws Exception {

    ConsultaVeterinaria consulta;

    if (id != null && !id.isEmpty()) {
      consulta = consultaRepository.findById(UUID.fromString(id))
          .orElse(new ConsultaVeterinaria());
    } else {
      consulta = new ConsultaVeterinaria();
    }

    consulta.setAnimal(animal);
    consulta.setTipoConsulta(tipo);
    consulta.setStatus(status);
    consulta.setDataHora(dataHora);
    consulta.setVeterinario(veterinario);
    consulta.setDiagnostico(diagnostico);
    consulta.setTratamento(tratamento);
    consulta.setMedicamentos(medicamentos);
    consulta.setObservacoes(observacoes);
    consulta.setAcompanhamentoNecessario(acompanhamentoNecessario);
    consulta.setDataRetorno(dataRetorno);

    return consulta;
  }
}
