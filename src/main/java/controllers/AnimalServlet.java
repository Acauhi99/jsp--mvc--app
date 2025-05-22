package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Animal;
import repositories.AnimalRepository;
import repositories.HabitatRepository;
import handlers.AnimalHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/animal/*")
public class AnimalServlet extends BaseServlet {
  private final AnimalHandler animalHandler;

  public AnimalServlet() {
    AnimalRepository animalRepository = new AnimalRepository();
    HabitatRepository habitatRepository = new HabitatRepository();
    this.animalHandler = new AnimalHandler(animalRepository, habitatRepository);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String pathInfo = request.getPathInfo();

    try {
      if (pathInfo != null && pathInfo.equals("/galeria")) {
        if (!requireAuthentication(request, response)) {
          return;
        }
        showAnimalList(request, response);
        return;
      }

      if (!requireAnyRole(request, response, ROLE_VETERINARIO, ROLE_ADMINISTRADOR)) {
        return;
      }

      if (pathInfo == null || "/".equals(pathInfo)) {
        showAnimalList(request, response);
        return;
      }

      if (pathInfo.startsWith("/detalhes")) {
        showAnimalDetails(request, response);
        return;
      }

      if (pathInfo.startsWith("/novo")) {
        showNewAnimalForm(request, response);
        return;
      }

      response.sendError(HttpServletResponse.SC_NOT_FOUND);

    } catch (IllegalArgumentException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro inválido: " + e.getMessage());
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
      if (pathInfo != null && pathInfo.startsWith("/novo")) {
        processCreateAnimal(request, response);
        return;
      }
      response.sendError(HttpServletResponse.SC_NOT_FOUND);

    } catch (Exception e) {
      request.setAttribute("erro", "Erro ao processar solicitação: " + e.getMessage());
      showNewAnimalForm(request, response);
    }
  }

  private void showAnimalList(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String especie = request.getParameter("especie");
    String classe = request.getParameter("classe");
    String genero = request.getParameter("genero");
    String tipoAmbiente = request.getParameter("tipoAmbiente");

    int page = getPageParameter(request);
    int pageSize = 10;

    List<Animal> animais = animalHandler.listarAnimaisFiltrados(especie, classe, genero, tipoAmbiente, page, pageSize);
    long total = animalHandler.contarAnimaisFiltrados(especie, classe, genero, tipoAmbiente);
    int totalPages = (int) Math.ceil((double) total / pageSize);

    request.setAttribute("animais", animais);
    request.setAttribute("page", page);
    request.setAttribute("totalPages", totalPages);
    request.setAttribute("especie", especie);
    request.setAttribute("classe", classe);
    request.setAttribute("genero", genero);
    request.setAttribute("tipoAmbiente", tipoAmbiente);

    // Obter dados para formulários e filtros
    Map<String, Object> dados = animalHandler.obterDadosFormulario();
    for (Map.Entry<String, Object> entry : dados.entrySet()) {
      request.setAttribute(entry.getKey(), entry.getValue());
    }

    forwardToView(request, response, "/WEB-INF/views/animal/list.jsp");
  }

  private void showAnimalDetails(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");

    if (id == null || id.trim().isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/animal/galeria");
      return;
    }

    Optional<Animal> optAnimal = animalHandler.buscarPorId(id);

    if (optAnimal.isEmpty()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Animal não encontrado");
      return;
    }

    request.setAttribute("animal", optAnimal.get());
    forwardToView(request, response, "/WEB-INF/views/animal/details.jsp");
  }

  private void showNewAnimalForm(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Obter dados para o formulário
    Map<String, Object> dados = animalHandler.obterDadosFormulario();
    for (Map.Entry<String, Object> entry : dados.entrySet()) {
      request.setAttribute(entry.getKey(), entry.getValue());
    }

    forwardToView(request, response, "/WEB-INF/views/animal/form.jsp");
  }

  private void processCreateAnimal(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      String nome = request.getParameter("nome");
      String especie = request.getParameter("especie");
      String nomeCientifico = request.getParameter("nomeCientifico");
      String classe = request.getParameter("classe");
      String genero = request.getParameter("genero");
      String habitatId = request.getParameter("habitatId");
      String statusSaude = request.getParameter("statusSaude");
      String dataChegada = request.getParameter("dataChegada");
      String detalhesSaude = request.getParameter("detalhesSaude");

      animalHandler.criarAnimal(nome, especie, nomeCientifico, classe, genero, habitatId,
          statusSaude, dataChegada, detalhesSaude);

      response.sendRedirect(request.getContextPath() + "/animal/galeria");
    } catch (Exception e) {
      request.setAttribute("erro", e.getMessage());
      showNewAnimalForm(request, response);
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
