package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Animal;
import models.ConsultaVeterinaria;
import models.Funcionario;
import models.Funcionario.Cargo;
import repositories.AnimalRepository;
import repositories.ConsultaVeterinariaRepository;
import repositories.FuncionarioRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet({ "/consulta", "/consulta/nova", "/consulta/salvar", "/consulta/detalhes", "/consulta/editar",
    "/consulta/historico" })
public class ConsultaVeterinariaServlet extends HttpServlet {

  private final ConsultaVeterinariaRepository consultaRepo = new ConsultaVeterinariaRepository();
  private final AnimalRepository animalRepo = new AnimalRepository();
  private final FuncionarioRepository funcionarioRepo = new FuncionarioRepository();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    switch (path) {
      case "/consulta":
        listarConsultas(req, resp);
        break;
      case "/consulta/nova":
        mostrarFormulario(req, resp, null);
        break;
      case "/consulta/editar":
        editarConsulta(req, resp);
        break;
      case "/consulta/detalhes":
        detalhesConsulta(req, resp);
        break;
      case "/consulta/historico":
        mostrarHistorico(req, resp);
        break;
      default:
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    if ("/consulta/salvar".equals(path)) {
      salvarConsulta(req, resp);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  private void listarConsultas(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String statusParam = req.getParameter("status");
    String animalParam = req.getParameter("animal");
    String tipoParam = req.getParameter("tipo");
    String veterinarioParam = req.getParameter("veterinario");
    String pageParam = req.getParameter("page");

    List<Animal> animais = animalRepo.findAll();
    req.setAttribute("animais", animais);

    List<Funcionario> veterinarios = funcionarioRepo.findByCargo(Cargo.VETERINARIO);
    req.setAttribute("veterinarios", veterinarios);

    List<ConsultaVeterinaria> consultas = consultaRepo.findAll();

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
      consultas
          .removeIf(c -> c.getVeterinario() == null || !c.getVeterinario().getId().toString().equals(veterinarioParam));
    }

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
    int totalItems = consultas.size();
    int totalPages = (int) Math.ceil((double) totalItems / pageSize);
    int fromIndex = (page - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, totalItems);
    List<ConsultaVeterinaria> pageList = totalItems > 0
        ? consultas.subList(Math.min(fromIndex, totalItems), Math.min(toIndex, totalItems))
        : new ArrayList<>();

    req.setAttribute("consultas", pageList);
    req.setAttribute("page", page);
    req.setAttribute("totalPages", totalPages);

    req.getRequestDispatcher("/WEB-INF/views/consulta/list.jsp").forward(req, resp);
  }

  private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, ConsultaVeterinaria consulta)
      throws ServletException, IOException {
    List<Animal> animais = animalRepo.findAll();
    List<Funcionario> veterinarios = funcionarioRepo.findByCargo(Cargo.VETERINARIO);
    req.setAttribute("animais", animais);
    req.setAttribute("veterinarios", veterinarios);
    if (consulta != null) {
      req.setAttribute("consulta", consulta);
    }
    req.getRequestDispatcher("/WEB-INF/views/consulta/form.jsp").forward(req, resp);
  }

  private void editarConsulta(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null) {
      resp.sendRedirect(req.getContextPath() + "/consulta");
      return;
    }
    Optional<ConsultaVeterinaria> consultaOpt = consultaRepo.findById(UUID.fromString(id));
    if (consultaOpt.isPresent()) {
      List<Animal> animais = animalRepo.findAll();
      List<Funcionario> veterinarios = funcionarioRepo.findByCargo(Cargo.VETERINARIO);

      req.setAttribute("animais", animais);
      req.setAttribute("veterinarios", veterinarios);
      req.setAttribute("consulta", consultaOpt.get());
      req.getRequestDispatcher("/WEB-INF/views/consulta/edit.jsp").forward(req, resp);
    } else {
      resp.sendRedirect(req.getContextPath() + "/consulta");
    }
  }

  private void detalhesConsulta(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null) {
      resp.sendRedirect(req.getContextPath() + "/consulta");
      return;
    }
    Optional<ConsultaVeterinaria> consultaOpt = consultaRepo.findById(UUID.fromString(id));
    if (consultaOpt.isPresent()) {
      req.setAttribute("consulta", consultaOpt.get());
      req.getRequestDispatcher("/WEB-INF/views/consulta/details.jsp").forward(req, resp);
    } else {
      resp.sendRedirect(req.getContextPath() + "/consulta");
    }
  }

  private void mostrarHistorico(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    List<Animal> animais = animalRepo.findAll();
    req.setAttribute("animais", animais);

    String animalId = req.getParameter("animal");
    if (animalId != null && !animalId.isEmpty()) {
      Optional<Animal> animalOpt = animalRepo.findById(UUID.fromString(animalId));
      if (animalOpt.isPresent()) {
        Animal animal = animalOpt.get();
        List<ConsultaVeterinaria> historicoConsultas = consultaRepo.findByAnimalId(animal.getId());

        req.setAttribute("animal", animal);
        req.setAttribute("historicoConsultas", historicoConsultas);
      }
    }

    req.getRequestDispatcher("/WEB-INF/views/consulta/historico.jsp").forward(req, resp);
  }

  private void salvarConsulta(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String id = req.getParameter("id");
      UUID animalId = UUID.fromString(req.getParameter("animal"));
      Animal animal = animalRepo.findById(animalId).orElse(null);
      ConsultaVeterinaria.TipoConsulta tipo = ConsultaVeterinaria.TipoConsulta
          .valueOf(req.getParameter("tipoConsulta"));
      ConsultaVeterinaria.StatusConsulta status = ConsultaVeterinaria.StatusConsulta
          .valueOf(req.getParameter("status"));

      LocalDateTime dataHora = null;
      if (req.getParameter("dataHora") != null && !req.getParameter("dataHora").isEmpty()) {
        dataHora = LocalDateTime.parse(req.getParameter("dataHora"));
      }

      String veterinarioId = req.getParameter("veterinario");
      Funcionario veterinario = null;
      if (veterinarioId != null && !veterinarioId.isEmpty()) {
        veterinario = funcionarioRepo.findById(UUID.fromString(veterinarioId)).orElse(null);
      } else {
        Object userObj = req.getSession().getAttribute("user");
        if (userObj instanceof dtos.auth.LoginResponse) {
          dtos.auth.LoginResponse loginResponse = (dtos.auth.LoginResponse) userObj;
          UUID userId = loginResponse.getUserDetails().getId();
          Funcionario currentUser = funcionarioRepo.findById(userId).orElse(null);
          if (currentUser != null && currentUser.getCargo() == Cargo.VETERINARIO) {
            veterinario = currentUser;
          }
        }
      }

      String diagnostico = req.getParameter("diagnostico");
      String tratamento = req.getParameter("tratamento");
      String medicamentos = req.getParameter("medicamentos");
      String observacoes = req.getParameter("observacoes");

      boolean acompanhamentoNecessario = req.getParameter("acompanhamentoNecessario") != null;

      LocalDateTime dataRetorno = null;
      if (acompanhamentoNecessario && req.getParameter("dataRetorno") != null
          && !req.getParameter("dataRetorno").isEmpty()) {
        dataRetorno = LocalDateTime.parse(req.getParameter("dataRetorno"));
      }

      ConsultaVeterinaria consulta;
      if (id != null && !id.isEmpty()) {
        consulta = consultaRepo.findById(UUID.fromString(id)).orElse(new ConsultaVeterinaria());
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

      if (id != null && !id.isEmpty()) {
        consultaRepo.update(consulta);
      } else {
        consultaRepo.save(consulta);
      }
      resp.sendRedirect(req.getContextPath() + "/consulta");
    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", "Erro ao salvar consulta: " + e.getMessage());
      String id = req.getParameter("id");
      if (id != null && !id.isEmpty()) {
        editarConsulta(req, resp);
      } else {
        mostrarFormulario(req, resp, null);
      }
    }
  }
}
