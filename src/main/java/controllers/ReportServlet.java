package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import repositories.ConsultaVeterinariaRepository;
import repositories.IngressoRepository;
import repositories.FuncionarioRepository;
import handlers.ReportHandler;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = { "/relatorio/consultas", "/relatorio/vendas" })
public class ReportServlet extends BaseServlet {

  private final ReportHandler reportHandler;

  public ReportServlet() {
    ConsultaVeterinariaRepository consultaRepo = new ConsultaVeterinariaRepository();
    IngressoRepository ingressoRepo = new IngressoRepository();
    FuncionarioRepository funcionarioRepo = new FuncionarioRepository();

    this.reportHandler = new ReportHandler(consultaRepo, ingressoRepo, funcionarioRepo);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!requireRole(req, resp, ROLE_ADMINISTRADOR)) {
      return;
    }

    String path = req.getServletPath();

    if ("/relatorio/consultas".equals(path)) {
      gerarRelatorioConsultas(req, resp);
    } else if ("/relatorio/vendas".equals(path)) {
      gerarRelatorioVendas(req, resp);
    } else {
      resp.sendRedirect(req.getContextPath() + "/dashboard/admin");
    }
  }

  private void gerarRelatorioConsultas(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String dataInicioStr = req.getParameter("dataInicio");
    String dataFimStr = req.getParameter("dataFim");
    String veterinarioId = req.getParameter("veterinarioId");
    String tipoConsulta = req.getParameter("tipoConsulta");
    String statusConsulta = req.getParameter("statusConsulta");

    Map<String, Object> relatorio = reportHandler.gerarRelatorioConsultas(
        dataInicioStr, dataFimStr, veterinarioId, tipoConsulta, statusConsulta);

    for (Map.Entry<String, Object> entry : relatorio.entrySet()) {
      req.setAttribute(entry.getKey(), entry.getValue());
    }

    forwardToView(req, resp, "/WEB-INF/views/reports/consultas.jsp");
  }

  private void gerarRelatorioVendas(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String dataInicioStr = req.getParameter("dataInicio");
    String dataFimStr = req.getParameter("dataFim");
    String tipoIngresso = req.getParameter("tipoIngresso");

    int paginaAtual = 1;
    int tamanhoPagina = 10;

    try {
      String paginaParam = req.getParameter("pagina");
      if (paginaParam != null && !paginaParam.isEmpty()) {
        paginaAtual = Integer.parseInt(paginaParam);
        if (paginaAtual < 1) {
          paginaAtual = 1;
        }
      }
    } catch (NumberFormatException e) {
      paginaAtual = 1;
    }

    Map<String, Object> relatorio = reportHandler.gerarRelatorioVendas(
        dataInicioStr, dataFimStr, tipoIngresso, paginaAtual, tamanhoPagina);

    for (Map.Entry<String, Object> entry : relatorio.entrySet()) {
      req.setAttribute(entry.getKey(), entry.getValue());
    }

    forwardToView(req, resp, "/WEB-INF/views/reports/vendas.jsp");
  }
}