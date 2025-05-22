package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.ManutencaoHabitat;
import handlers.ManutencaoHabitatHandler;
import handlers.ManutencaoHabitatHandler.PageResult;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@WebServlet({ "/manutencao", "/manutencao/novo", "/manutencao/salvar", "/manutencao/detalhes", "/manutencao/editar" })
public class ManutencaoHabitatServlet extends BaseServlet {

  private final ManutencaoHabitatHandler manutencaoHandler;

  public ManutencaoHabitatServlet() {
    this.manutencaoHandler = new ManutencaoHabitatHandler();
  }

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
    String statusParam = req.getParameter("status");
    String prioridadeParam = req.getParameter("prioridade");
    String habitatParam = req.getParameter("habitat");
    String responsavelParam = req.getParameter("responsavel");

    List<ManutencaoHabitat> manutencoes = manutencaoHandler.listarManutencoes(
        statusParam, prioridadeParam, habitatParam, responsavelParam);

    int page = getPageParam(req);
    int pageSize = 10;
    PageResult<ManutencaoHabitat> pageResult = manutencaoHandler.aplicarPaginacao(manutencoes, page, pageSize);

    for (ManutencaoHabitat m : pageResult.getItems()) {
      if (m.getDataSolicitacao() != null) {
        req.setAttribute("dataSolicitacao_" + m.getId(), Timestamp.valueOf(m.getDataSolicitacao()));
      }
    }

    Map<String, Object> dadosSelecao = manutencaoHandler.carregarDadosSelecao();
    for (Map.Entry<String, Object> entry : dadosSelecao.entrySet()) {
      req.setAttribute(entry.getKey(), entry.getValue());
    }

    req.setAttribute("manutencoes", pageResult.getItems());
    req.setAttribute("page", pageResult.getCurrentPage());
    req.setAttribute("totalPages", pageResult.getTotalPages());
    forwardToView(req, resp, "/WEB-INF/views/manutencao/list.jsp");
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

  private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, ManutencaoHabitat manutencao)
      throws ServletException, IOException {
    Map<String, Object> dadosSelecao = manutencaoHandler.carregarDadosSelecao();
    for (Map.Entry<String, Object> entry : dadosSelecao.entrySet()) {
      req.setAttribute(entry.getKey(), entry.getValue());
    }

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

    manutencaoHandler.buscarPorId(id).ifPresentOrElse(
        manutencao -> {
          try {
            Map<String, Object> dadosSelecao = manutencaoHandler.carregarDadosSelecao();
            for (Map.Entry<String, Object> entry : dadosSelecao.entrySet()) {
              req.setAttribute(entry.getKey(), entry.getValue());
            }
            req.setAttribute("manutencao", manutencao);
            forwardToView(req, resp, "/WEB-INF/views/manutencao/edit.jsp");
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        },
        () -> {
          try {
            resp.sendRedirect(req.getContextPath() + "/manutencao");
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private void detalhesSolicitacao(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String id = req.getParameter("id");
    if (id == null) {
      resp.sendRedirect(req.getContextPath() + "/manutencao");
      return;
    }

    manutencaoHandler.buscarPorId(id).ifPresentOrElse(
        manutencao -> {
          try {
            req.setAttribute("manutencao", manutencao);
            forwardToView(req, resp, "/WEB-INF/views/manutencao/details.jsp");
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        },
        () -> {
          try {
            resp.sendRedirect(req.getContextPath() + "/manutencao");
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private void salvarSolicitacao(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      String id = req.getParameter("id");
      String habitatId = req.getParameter("habitat");
      String tipoManutencao = req.getParameter("tipoManutencao");
      String prioridade = req.getParameter("prioridade");
      String descricao = req.getParameter("descricao");
      String dataProgramada = req.getParameter("dataProgramada");
      String responsavelId = req.getParameter("responsavel");
      String status = req.getParameter("status");

      HttpSession session = req.getSession(false);
      UUID solicitanteId = null;

      if (session != null) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj instanceof UUID) {
          solicitanteId = (UUID) userIdObj;
        } else if (userIdObj instanceof String) {
          try {
            solicitanteId = UUID.fromString((String) userIdObj);
          } catch (IllegalArgumentException e) {
            throw new ServletException("ID de usuário inválido");
          }
        }
      }

      if (solicitanteId == null) {
        throw new ServletException("Usuário não autenticado corretamente");
      }

      manutencaoHandler.criarOuAtualizarManutencao(
          id, habitatId, tipoManutencao, prioridade, descricao,
          dataProgramada, solicitanteId, responsavelId, status);

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
}