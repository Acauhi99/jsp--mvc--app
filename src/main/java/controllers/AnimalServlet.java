package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Animal;
import models.Animal.Classe;
import models.Animal.Genero;
import models.Habitat.TipoAmbiente;
import repositories.AnimalRepository;
import java.io.IOException;
import java.util.List;

@WebServlet("/animal/*")
public class AnimalServlet extends HttpServlet {
  private AnimalRepository animalRepo = new AnimalRepository();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String path = req.getPathInfo();
    if (path == null || "/".equals(path) || "/galeria".equals(path)) {
      String especie = req.getParameter("especie");
      String classe = req.getParameter("classe");
      String genero = req.getParameter("genero");
      String tipoAmbiente = req.getParameter("tipoAmbiente");
      int page = 1;
      int pageSize = 10;
      try {
        page = Integer.parseInt(req.getParameter("page"));
      } catch (Exception ignored) {
      }
      int offset = (page - 1) * pageSize;

      List<Animal> animais = animalRepo.findFiltered(especie, classe, genero, tipoAmbiente, offset, pageSize);
      long total = animalRepo.countFiltered(especie, classe, genero, tipoAmbiente);
      int totalPages = (int) Math.ceil((double) total / pageSize);

      req.setAttribute("animais", animais);
      req.setAttribute("page", page);
      req.setAttribute("totalPages", totalPages);
      req.setAttribute("especie", especie);
      req.setAttribute("classe", classe);
      req.setAttribute("genero", genero);
      req.setAttribute("tipoAmbiente", tipoAmbiente);

      req.setAttribute("classes", Classe.values());
      req.setAttribute("generos", Genero.values());
      req.setAttribute("tiposAmbiente", TipoAmbiente.values());

      req.getRequestDispatcher("/WEB-INF/views/animal/list.jsp").forward(req, resp);
    } else if (path.startsWith("/detalhes")) {
      String id = req.getParameter("id");
      if (id != null) {
        Animal animal = animalRepo.findById(java.util.UUID.fromString(id)).orElse(null);
        req.setAttribute("animal", animal);
        req.getRequestDispatcher("/WEB-INF/views/animal/details.jsp").forward(req, resp);
      } else {
        resp.sendRedirect(req.getContextPath() + "/animal/galeria");
      }
    } else if (path.startsWith("/novo")) {
      // Carregue listas para selects
      req.setAttribute("classes", Classe.values());
      req.setAttribute("generos", Genero.values());
      // Carregue habitats do banco
      List<models.Habitat> habitats = new repositories.HabitatRepository().findAll();
      req.setAttribute("habitats", habitats);

      req.getRequestDispatcher("/WEB-INF/views/animal/form.jsp").forward(req, resp);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String path = req.getPathInfo();
    if (path != null && path.startsWith("/novo")) {
      // Obtenha os parâmetros do formulário
      String nome = req.getParameter("nome");
      String especie = req.getParameter("especie");
      String nomeCientifico = req.getParameter("nomeCientifico");
      String classe = req.getParameter("classe");
      String genero = req.getParameter("genero");
      String habitatId = req.getParameter("habitatId");
      String statusSaude = req.getParameter("statusSaude");
      String dataChegada = req.getParameter("dataChegada");
      String detalhesSaude = req.getParameter("detalhesSaude");

      // Crie o objeto Animal
      Animal animal = new Animal();
      animal.setNome(nome);
      animal.setEspecie(especie);
      animal.setNomeCientifico(nomeCientifico);
      animal.setClasse(Classe.valueOf(classe));
      animal.setGenero(Genero.valueOf(genero));
      animal.setStatusSaude(Animal.StatusSaude.valueOf(statusSaude));
      animal.setDetalhesSaude(detalhesSaude);

      // Parse dataChegada
      if (dataChegada != null && !dataChegada.isEmpty()) {
        animal.setDataChegada(java.time.LocalDate.parse(dataChegada));
      }

      // Buscar habitat
      if (habitatId != null && !habitatId.isEmpty()) {
        models.Habitat habitat = new repositories.HabitatRepository()
            .findById(java.util.UUID.fromString(habitatId)).orElse(null);
        animal.setHabitat(habitat);
      }

      // Salvar no banco
      animalRepo.save(animal);

      // Redirecionar para a lista
      resp.sendRedirect(req.getContextPath() + "/animal/galeria");
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
