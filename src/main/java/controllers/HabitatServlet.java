package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Habitat;
import models.Habitat.TipoAmbiente;
import handlers.HabitatHandler;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = { "/habitat", "/habitat/novo", "/habitat/editar", "/habitat/excluir" })
public class HabitatServlet extends BaseServlet {
  private final HabitatHandler habitatHandler;

  public HabitatServlet() {
    this.habitatHandler = new HabitatHandler();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getServletPath();

    if ("/habitat/novo".equals(path)) {
      if (!requireAnyRole(request, response, ROLE_MANUTENCAO, ROLE_ADMINISTRADOR)) {
        return;
      }

      request.setAttribute("tiposAmbiente", TipoAmbiente.values());
      forwardToView(request, response, "/WEB-INF/views/habitat/form.jsp");
      return;
    }

    if ("/habitat/editar".equals(path)) {
      if (!requireAnyRole(request, response, ROLE_MANUTENCAO, ROLE_ADMINISTRADOR)) {
        return;
      }

      editarHabitat(request, response);
      return;
    }

    if (!requireAuthentication(request, response)) {
      return;
    }

    listarHabitats(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getServletPath();

    if (!requireAnyRole(request, response, ROLE_MANUTENCAO, ROLE_ADMINISTRADOR)) {
      return;
    }

    if ("/habitat/novo".equals(path) || "/habitat/editar".equals(path)) {
      salvarHabitat(request, response);
    } else if ("/habitat/excluir".equals(path)) {
      excluirHabitat(request, response);
    }
  }

  private void listarHabitats(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String tipoAmbiente = request.getParameter("tipoAmbiente");
    List<Habitat> habitats = habitatHandler.listarHabitatsPorTipoAmbiente(tipoAmbiente);

    request.setAttribute("habitats", habitats);
    request.setAttribute("tiposAmbiente", TipoAmbiente.values());
    request.setAttribute("tipoAmbiente", tipoAmbiente);
    forwardToView(request, response, "/WEB-INF/views/habitat/list.jsp");
  }

  private void editarHabitat(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");

    try {
      habitatHandler.buscarHabitatPorId(id).ifPresentOrElse(
          habitat -> {
            request.setAttribute("habitat", habitat);
            request.setAttribute("tiposAmbiente", TipoAmbiente.values());
            try {
              forwardToView(request, response, "/WEB-INF/views/habitat/form.jsp");
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          },
          () -> {
            try {
              response.sendRedirect(request.getContextPath() + "/habitat?erro=Habitat não encontrado");
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          });
    } catch (Exception e) {
      response.sendRedirect(request.getContextPath() + "/habitat?erro=" + e.getMessage());
    }
  }

  private void salvarHabitat(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");
    String nome = request.getParameter("nome");
    String tipoAmbienteStr = request.getParameter("tipoAmbiente");
    String tamanhoStr = request.getParameter("tamanho");
    String capacidadeStr = request.getParameter("capacidadeMaximaAnimais");
    String publicoAcessivelStr = request.getParameter("publicoAcessivel");

    try {
      if (id != null && !id.isEmpty()) {
        habitatHandler.atualizarHabitat(id, nome, tipoAmbienteStr, tamanhoStr, capacidadeStr, publicoAcessivelStr);
        response.sendRedirect(request.getContextPath() + "/habitat?mensagem=Habitat atualizado com sucesso!");
      } else {
        habitatHandler.criarHabitat(nome, tipoAmbienteStr, tamanhoStr, capacidadeStr, publicoAcessivelStr);
        response.sendRedirect(request.getContextPath() + "/habitat?mensagem=Habitat cadastrado com sucesso!");
      }
    } catch (Exception e) {
      request.setAttribute("erro", e.getMessage());
      request.setAttribute("tiposAmbiente", TipoAmbiente.values());

      Habitat habitat = new Habitat();
      habitat.setNome(nome);
      try {
        if (tipoAmbienteStr != null && !tipoAmbienteStr.isEmpty()) {
          habitat.setTipoAmbiente(TipoAmbiente.valueOf(tipoAmbienteStr));
        }
      } catch (IllegalArgumentException ignored) {
      }

      if (tamanhoStr != null && !tamanhoStr.isEmpty()) {
        try {
          habitat.setTamanho(Double.parseDouble(tamanhoStr));
        } catch (NumberFormatException ignored) {
        }
      }

      if (capacidadeStr != null && !capacidadeStr.isEmpty()) {
        try {
          habitat.setCapacidadeMaximaAnimais(Integer.parseInt(capacidadeStr));
        } catch (NumberFormatException ignored) {
        }
      }

      habitat.setPublicoAcessivel("on".equals(publicoAcessivelStr));

      if (id != null && !id.isEmpty()) {
        try {
          request.setAttribute("habitatId", id);
        } catch (IllegalArgumentException ignored) {
        }
      }

      request.setAttribute("habitat", habitat);
      forwardToView(request, response, "/WEB-INF/views/habitat/form.jsp");
    }
  }

  private void excluirHabitat(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String id = request.getParameter("id");

    try {
      habitatHandler.excluirHabitat(id);
      response.sendRedirect(request.getContextPath() + "/habitat?mensagem=Habitat excluído com sucesso!");
    } catch (Exception e) {
      response.sendRedirect(request.getContextPath() +
          "/habitat?erro=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
    }
  }
}
