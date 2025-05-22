package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Animal;
import models.ConsultaVeterinaria;
import handlers.ConsultaVeterinariaHandler;
import handlers.ConsultaVeterinariaHandler.PageResult;

import java.io.IOException;
import java.util.*;

@WebServlet("/consulta/*")
public class ConsultaVeterinariaServlet extends BaseServlet {

  private final ConsultaVeterinariaHandler consultaHandler;

  public ConsultaVeterinariaServlet() {
    this.consultaHandler = new ConsultaVeterinariaHandler();
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

    Map<String, Object> dados = consultaHandler.carregarDadosParaFormularioEFiltros();
    dados.forEach(request::setAttribute);

    String statusParam = request.getParameter("status");
    String animalParam = request.getParameter("animal");
    String tipoParam = request.getParameter("tipo");
    String veterinarioParam = request.getParameter("veterinario");

    List<ConsultaVeterinaria> consultas = consultaHandler.obterConsultasFiltradas(
        statusParam, animalParam, tipoParam, veterinarioParam);

    int page = getPageParameter(request);
    int pageSize = 10;
    PageResult<ConsultaVeterinaria> pageResult = consultaHandler.paginarResultados(consultas, page, pageSize);

    request.setAttribute("consultas", pageResult.items);
    request.setAttribute("page", pageResult.currentPage);
    request.setAttribute("totalPages", pageResult.totalPages);

    forwardToView(request, response, "/WEB-INF/views/consulta/list.jsp");
  }

  private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
      ConsultaVeterinaria consulta)
      throws ServletException, IOException {

    Map<String, Object> dados = consultaHandler.carregarDadosParaFormularioEFiltros();
    dados.forEach(request::setAttribute);

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

    Optional<ConsultaVeterinaria> consultaOpt = consultaHandler.buscarConsultaPorId(id);

    if (consultaOpt.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/consulta");
      return;
    }

    Map<String, Object> dados = consultaHandler.carregarDadosParaFormularioEFiltros();
    dados.forEach(request::setAttribute);

    request.setAttribute("consulta", consultaOpt.get());
    forwardToView(request, response, "/WEB-INF/views/consulta/edit.jsp");
  }

  private void detalhesConsulta(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    if (id == null || id.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/consulta");
      return;
    }

    Optional<ConsultaVeterinaria> consultaOpt = consultaHandler.buscarConsultaPorId(id);

    if (consultaOpt.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/consulta");
      return;
    }

    request.setAttribute("consulta", consultaOpt.get());
    forwardToView(request, response, "/WEB-INF/views/consulta/details.jsp");
  }

  private void mostrarHistorico(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    List<Animal> animais = consultaHandler.listarAnimais();
    request.setAttribute("animais", animais);

    String animalId = request.getParameter("animal");
    if (animalId != null && !animalId.isEmpty()) {
      Optional<Animal> animalOpt = consultaHandler.buscarAnimalPorId(animalId);

      if (animalOpt.isPresent()) {
        Animal animal = animalOpt.get();
        List<ConsultaVeterinaria> historicoConsultas = consultaHandler.obterHistoricoConsultas(animalId);

        request.setAttribute("animal", animal);
        request.setAttribute("historicoConsultas", historicoConsultas);
      }
    }

    forwardToView(request, response, "/WEB-INF/views/consulta/historico.jsp");
  }

  private void salvarConsulta(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      String id = request.getParameter("id");
      String animalId = request.getParameter("animal");
      String tipoConsulta = request.getParameter("tipoConsulta");
      String status = request.getParameter("status");
      String dataHora = request.getParameter("dataHora");
      String veterinarioId = request.getParameter("veterinario");
      String diagnostico = request.getParameter("diagnostico");
      String tratamento = request.getParameter("tratamento");
      String medicamentos = request.getParameter("medicamentos");
      String observacoes = request.getParameter("observacoes");
      boolean acompanhamentoNecessario = request.getParameter("acompanhamentoNecessario") != null;
      String dataRetorno = request.getParameter("dataRetorno");

      consultaHandler.salvarConsulta(
          id, animalId, tipoConsulta, status, dataHora, veterinarioId,
          diagnostico, tratamento, medicamentos, observacoes,
          acompanhamentoNecessario, dataRetorno);

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
}
