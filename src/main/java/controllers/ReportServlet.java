package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import repositories.ConsultaVeterinariaRepository;
import repositories.IngressoRepository;
import repositories.FuncionarioRepository;
import models.ConsultaVeterinaria;
import models.Ingresso;
import models.Funcionario;
import models.Funcionario.Cargo;
import models.ConsultaVeterinaria.TipoConsulta;
import models.ConsultaVeterinaria.StatusConsulta;
import models.Ingresso.TipoIngresso;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.DoubleSummaryStatistics;

@WebServlet(urlPatterns = { "/relatorio/consultas", "/relatorio/vendas" })
public class ReportServlet extends HttpServlet {

  private final ConsultaVeterinariaRepository consultaRepo = new ConsultaVeterinariaRepository();
  private final IngressoRepository ingressoRepo = new IngressoRepository();
  private final FuncionarioRepository funcionarioRepo = new FuncionarioRepository();

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    HttpSession session = req.getSession(false);

    if (session == null || session.getAttribute("user") == null) {
      resp.sendRedirect(req.getContextPath() + "/auth/login");
      return;
    }

    String role = (String) session.getAttribute("role");
    if (!"ADMINISTRADOR".equals(role)) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

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

    LocalDateTime dataInicio = LocalDateTime.now().minusDays(30);
    LocalDateTime dataFim = LocalDateTime.now();

    if (dataInicioStr != null && !dataInicioStr.isEmpty()) {
      dataInicio = LocalDateTime.parse(dataInicioStr + "T00:00:00");
    }

    if (dataFimStr != null && !dataFimStr.isEmpty()) {
      dataFim = LocalDateTime.parse(dataFimStr + "T23:59:59");
    }

    List<ConsultaVeterinaria> todasConsultas = consultaRepo.findByDateRange(dataInicio, dataFim);
    List<ConsultaVeterinaria> consultas = filtrarConsultas(todasConsultas, veterinarioId, tipoConsulta, statusConsulta);

    Map<TipoConsulta, Long> consultasPorTipo = inicializarMapaTipoConsulta();
    Map<StatusConsulta, Long> consultasPorStatus = inicializarMapaStatusConsulta();
    Map<String, Long> consultasPorVeterinario = new HashMap<>();

    calcularEstatisticasConsulta(todasConsultas, consultasPorTipo, consultasPorStatus, consultasPorVeterinario);
    List<Funcionario> veterinarios = funcionarioRepo.findByCargo(Cargo.VETERINARIO);

    req.setAttribute("consultas", consultas);
    req.setAttribute("totalConsultas", todasConsultas.size());
    req.setAttribute("consultasPorTipo", consultasPorTipo);
    req.setAttribute("consultasPorStatus", consultasPorStatus);
    req.setAttribute("consultasPorVeterinario", consultasPorVeterinario);
    req.setAttribute("veterinarios", veterinarios);
    req.setAttribute("tiposConsulta", TipoConsulta.values());
    req.setAttribute("statusConsulta", StatusConsulta.values());
    req.setAttribute("dataInicio", dataInicio.format(DATE_FORMATTER));
    req.setAttribute("dataFim", dataFim.format(DATE_FORMATTER));

    req.getRequestDispatcher("/WEB-INF/views/reports/consultas.jsp").forward(req, resp);
  }

  private Map<TipoConsulta, Long> inicializarMapaTipoConsulta() {
    Map<TipoConsulta, Long> mapa = new HashMap<>();
    for (TipoConsulta tipo : TipoConsulta.values()) {
      mapa.put(tipo, 0L);
    }
    return mapa;
  }

  private Map<StatusConsulta, Long> inicializarMapaStatusConsulta() {
    Map<StatusConsulta, Long> mapa = new HashMap<>();
    for (StatusConsulta status : StatusConsulta.values()) {
      mapa.put(status, 0L);
    }
    return mapa;
  }

  private List<ConsultaVeterinaria> filtrarConsultas(List<ConsultaVeterinaria> consultas,
      String veterinarioId,
      String tipoConsulta,
      String statusConsulta) {
    return consultas.stream()
        .filter(c -> veterinarioId == null || veterinarioId.isEmpty() ||
            (c.getVeterinario() != null && c.getVeterinario().getId().toString().equals(veterinarioId)))
        .filter(c -> tipoConsulta == null || tipoConsulta.isEmpty() ||
            (c.getTipoConsulta() != null && c.getTipoConsulta().toString().equals(tipoConsulta)))
        .filter(c -> statusConsulta == null || statusConsulta.isEmpty() ||
            (c.getStatus() != null && c.getStatus().toString().equals(statusConsulta)))
        .collect(Collectors.toList());
  }

  private void calcularEstatisticasConsulta(List<ConsultaVeterinaria> consultas,
      Map<TipoConsulta, Long> consultasPorTipo,
      Map<StatusConsulta, Long> consultasPorStatus,
      Map<String, Long> consultasPorVeterinario) {
    for (ConsultaVeterinaria consulta : consultas) {
      if (consulta.getTipoConsulta() != null) {
        consultasPorTipo.put(consulta.getTipoConsulta(),
            consultasPorTipo.getOrDefault(consulta.getTipoConsulta(), 0L) + 1);
      }

      if (consulta.getStatus() != null) {
        consultasPorStatus.put(consulta.getStatus(),
            consultasPorStatus.getOrDefault(consulta.getStatus(), 0L) + 1);
      }

      if (consulta.getVeterinario() != null) {
        String nomeDr = consulta.getVeterinario().getNome();
        consultasPorVeterinario.put(nomeDr,
            consultasPorVeterinario.getOrDefault(nomeDr, 0L) + 1);
      }
    }
  }

  private void gerarRelatorioVendas(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String dataInicioStr = req.getParameter("dataInicio");
    String dataFimStr = req.getParameter("dataFim");
    String tipoIngresso = req.getParameter("tipoIngresso");

    LocalDateTime dataInicio = LocalDateTime.now().minusDays(30);
    LocalDateTime dataFim = LocalDateTime.now();

    if (dataInicioStr != null && !dataInicioStr.isEmpty()) {
      dataInicio = LocalDateTime.parse(dataInicioStr + "T00:00:00");
    }

    if (dataFimStr != null && !dataFimStr.isEmpty()) {
      dataFim = LocalDateTime.parse(dataFimStr + "T23:59:59");
    }

    List<Ingresso> todosIngressos = ingressoRepo.findAll();
    List<Ingresso> ingressos = filtrarIngressos(todosIngressos, dataInicio, dataFim, tipoIngresso);

    double valorTotal = ingressos.stream().mapToDouble(Ingresso::getValor).sum();
    int totalIngressos = ingressos.size();

    Map<TipoIngresso, List<Ingresso>> ingressosPorTipo = ingressos.stream()
        .collect(Collectors.groupingBy(Ingresso::getTipo));

    Map<TipoIngresso, DoubleSummaryStatistics> estatisticasPorTipo = calcularEstatisticasPorTipo(ingressosPorTipo);
    Map<String, Double> vendasPorDia = calcularVendasPorDia(ingressos, dataInicio, dataFim);

    int paginaAtual = 1;
    int tamanhoPagina = 10;
    int totalRegistros = ingressos.size();
    int totalPaginas = (int) Math.ceil((double) totalRegistros / tamanhoPagina);
    int inicio = 1;
    int fim = totalPaginas;

    try {
      String paginaParam = req.getParameter("pagina");
      if (paginaParam != null && !paginaParam.isEmpty()) {
        paginaAtual = Integer.parseInt(paginaParam);
        if (paginaAtual < 1)
          paginaAtual = 1;
        if (paginaAtual > totalPaginas && totalPaginas > 0)
          paginaAtual = totalPaginas;
      }
    } catch (NumberFormatException e) {
      paginaAtual = 1;
    }

    inicio = Math.max(1, paginaAtual - 2);
    fim = Math.min(totalPaginas, paginaAtual + 2);

    if (fim - inicio < 4 && totalPaginas >= 5) {
      if (paginaAtual < 3) {
        fim = Math.min(5, totalPaginas);
      } else if (paginaAtual > totalPaginas - 2) {
        inicio = Math.max(1, totalPaginas - 4);
      }
    }

    List<Ingresso> ingressosPaginados;
    if (ingressos.isEmpty()) {
      ingressosPaginados = ingressos;
    } else {
      int inicioPaginacao = (paginaAtual - 1) * tamanhoPagina;
      int fimPaginacao = Math.min(inicioPaginacao + tamanhoPagina, ingressos.size());
      ingressosPaginados = ingressos.subList(inicioPaginacao, fimPaginacao);
    }

    long ingressosUtilizados = ingressos.stream().filter(Ingresso::isUtilizado).count();
    double percentualUtilizados = totalIngressos > 0 ? (double) ingressosUtilizados / totalIngressos * 100 : 0;

    req.setAttribute("ingressos", ingressos);
    req.setAttribute("ingressosPaginados", ingressosPaginados);
    req.setAttribute("paginaAtual", paginaAtual);
    req.setAttribute("totalPaginas", totalPaginas);
    req.setAttribute("tamanhoPagina", tamanhoPagina);
    req.setAttribute("totalRegistros", totalRegistros);
    req.setAttribute("inicio", inicio);
    req.setAttribute("fim", fim);

    req.setAttribute("totalIngressos", totalIngressos);
    req.setAttribute("valorTotal", valorTotal);
    req.setAttribute("ingressosPorTipo", ingressosPorTipo);
    req.setAttribute("estatisticasPorTipo", estatisticasPorTipo);
    req.setAttribute("tiposIngresso", TipoIngresso.values());
    req.setAttribute("dataInicio", dataInicio.format(DATE_FORMATTER));
    req.setAttribute("dataFim", dataFim.format(DATE_FORMATTER));
    req.setAttribute("vendasPorDia", vendasPorDia);

    req.setAttribute("ingressosUtilizados", ingressosUtilizados);
    req.setAttribute("percentualUtilizados", percentualUtilizados);

    req.getRequestDispatcher("/WEB-INF/views/reports/vendas.jsp").forward(req, resp);
  }

  private List<Ingresso> filtrarIngressos(List<Ingresso> ingressos,
      LocalDateTime dataInicio,
      LocalDateTime dataFim,
      String tipoIngresso) {
    return ingressos.stream()
        .filter(i -> i.getDataCompra() != null &&
            (i.getDataCompra().isAfter(dataInicio) && i.getDataCompra().isBefore(dataFim)))
        .filter(i -> tipoIngresso == null || tipoIngresso.isEmpty() ||
            i.getTipo().toString().equals(tipoIngresso))
        .collect(Collectors.toList());
  }

  private Map<TipoIngresso, DoubleSummaryStatistics> calcularEstatisticasPorTipo(
      Map<TipoIngresso, List<Ingresso>> ingressosPorTipo) {
    Map<TipoIngresso, DoubleSummaryStatistics> estatisticas = new HashMap<>();
    for (TipoIngresso tipo : TipoIngresso.values()) {
      List<Ingresso> ingressosTipo = ingressosPorTipo.getOrDefault(tipo, new ArrayList<>());
      DoubleSummaryStatistics stats = ingressosTipo.stream()
          .mapToDouble(Ingresso::getValor)
          .summaryStatistics();
      estatisticas.put(tipo, stats);
    }
    return estatisticas;
  }

  private Map<String, Double> calcularVendasPorDia(List<Ingresso> ingressos,
      LocalDateTime dataInicio,
      LocalDateTime dataFim) {
    Map<String, Double> vendasPorDia = new HashMap<>();
    LocalDateTime atual = dataInicio;

    while (!atual.isAfter(dataFim)) {
      String diaFormatado = atual.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      LocalDateTime diaAtual = atual;

      double valorDia = ingressos.stream()
          .filter(i -> i.getDataCompra() != null &&
              i.getDataCompra().toLocalDate().equals(diaAtual.toLocalDate()))
          .mapToDouble(Ingresso::getValor)
          .sum();

      vendasPorDia.put(diaFormatado, valorDia);
      atual = atual.plusDays(1);
    }

    return vendasPorDia;
  }
}