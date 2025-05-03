package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Habitat;
import models.Habitat.TipoAmbiente;
import repositories.HabitatRepository;

import java.io.IOException;
import java.util.List;

@WebServlet("/habitat")
public class HabitatServlet extends HttpServlet {
  private final HabitatRepository habitatRepo = new HabitatRepository();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
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
}
