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
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
