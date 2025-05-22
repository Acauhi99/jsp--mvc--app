package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Animal;
import models.Animal.Classe;
import models.Animal.Genero;
import models.Animal.StatusSaude;
import models.Habitat;
import models.Habitat.TipoAmbiente;
import repositories.AnimalRepository;
import repositories.HabitatRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/animal/*")
public class AnimalServlet extends BaseServlet {
  private final AnimalRepository animalRepository;
  private final HabitatRepository habitatRepository;

  public AnimalServlet() {
    this.animalRepository = new AnimalRepository();
    this.habitatRepository = new HabitatRepository();
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

    } catch (IllegalArgumentException e) {
      request.setAttribute("erro", "Erro de validação: " + e.getMessage());
      showNewAnimalForm(request, response);
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
    int offset = (page - 1) * pageSize;

    List<Animal> animais = animalRepository.findFiltered(especie, classe, genero, tipoAmbiente, offset, pageSize);
    long total = animalRepository.countFiltered(especie, classe, genero, tipoAmbiente);
    int totalPages = (int) Math.ceil((double) total / pageSize);

    request.setAttribute("animais", animais);
    request.setAttribute("page", page);
    request.setAttribute("totalPages", totalPages);
    request.setAttribute("especie", especie);
    request.setAttribute("classe", classe);
    request.setAttribute("genero", genero);
    request.setAttribute("tipoAmbiente", tipoAmbiente);

    request.setAttribute("classes", Classe.values());
    request.setAttribute("generos", Genero.values());
    request.setAttribute("tiposAmbiente", TipoAmbiente.values());

    forwardToView(request, response, "/WEB-INF/views/animal/list.jsp");
  }

  private void showAnimalDetails(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");

    if (id == null || id.trim().isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/animal/galeria");
      return;
    }

    UUID animalId = parseUUID(id);
    Optional<Animal> optAnimal = animalRepository.findById(animalId);

    if (optAnimal.isEmpty()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Animal não encontrado");
      return;
    }

    request.setAttribute("animal", optAnimal.get());
    forwardToView(request, response, "/WEB-INF/views/animal/details.jsp");
  }

  private void showNewAnimalForm(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    request.setAttribute("classes", Classe.values());
    request.setAttribute("generos", Genero.values());
    request.setAttribute("statusSaude", StatusSaude.values());
    request.setAttribute("habitats", habitatRepository.findAll());

    forwardToView(request, response, "/WEB-INF/views/animal/form.jsp");
  }

  private void processCreateAnimal(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String nome = request.getParameter("nome");
    String especie = request.getParameter("especie");
    String nomeCientifico = request.getParameter("nomeCientifico");
    String classe = request.getParameter("classe");
    String genero = request.getParameter("genero");
    String habitatId = request.getParameter("habitatId");
    String statusSaude = request.getParameter("statusSaude");
    String dataChegada = request.getParameter("dataChegada");
    String detalhesSaude = request.getParameter("detalhesSaude");

    validateRequiredFields(nome, especie);

    Animal animal = new Animal();
    animal.setNome(nome);
    animal.setEspecie(especie);
    animal.setNomeCientifico(nomeCientifico);

    if (classe != null && !classe.isEmpty()) {
      animal.setClasse(Classe.valueOf(classe));
    }

    if (genero != null && !genero.isEmpty()) {
      animal.setGenero(Genero.valueOf(genero));
    }

    if (statusSaude != null && !statusSaude.isEmpty()) {
      animal.setStatusSaude(StatusSaude.valueOf(statusSaude));
    }

    animal.setDetalhesSaude(detalhesSaude);

    if (dataChegada != null && !dataChegada.isEmpty()) {
      animal.setDataChegada(LocalDate.parse(dataChegada));
    }

    if (habitatId != null && !habitatId.isEmpty()) {
      UUID id = parseUUID(habitatId);
      Habitat habitat = habitatRepository.findById(id).orElse(null);
      animal.setHabitat(habitat);
    }

    animalRepository.save(animal);

    response.sendRedirect(request.getContextPath() + "/animal/galeria");
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

  private UUID parseUUID(String uuidStr) {
    if (uuidStr == null || uuidStr.trim().isEmpty()) {
      throw new IllegalArgumentException("UUID não pode ser nulo ou vazio");
    }
    try {
      return UUID.fromString(uuidStr);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Formato de UUID inválido: " + uuidStr);
    }
  }

  private void validateRequiredFields(String nome, String especie) throws IllegalArgumentException {
    if (nome == null || nome.trim().isEmpty()) {
      throw new IllegalArgumentException("Nome do animal é obrigatório");
    }
    if (especie == null || especie.trim().isEmpty()) {
      throw new IllegalArgumentException("Espécie do animal é obrigatória");
    }
  }
}
