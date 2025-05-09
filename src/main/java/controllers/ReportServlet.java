package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import dtos.auth.LoginResponse;
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

    // Verificar autenticação
    if (session == null || session.getAttribute("user") == null) {
      resp.sendRedirect(req.getContextPath() + "/auth/login");
      return;
    }

    // Verificar se é administrador
    LoginResponse login = (LoginResponse) session.getAttribute("user");
    if (!"ADMINISTRADOR".equals(login.getRole())) {
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
    // Obter parâmetros de filtro
    String dataInicioStr = req.getParameter("dataInicio");
    String dataFimStr = req.getParameter("dataFim");
    String veterinarioId = req.getParameter("veterinarioId");
    String tipoConsulta = req.getParameter("tipoConsulta");
    String statusConsulta = req.getParameter("statusConsulta");

    // Configurar datas padrão (últimos 30 dias se não especificado)
    LocalDateTime dataFim = LocalDateTime.now();
    LocalDateTime dataInicio = dataFim.minusDays(30);

    if (dataInicioStr != null && !dataInicioStr.isEmpty()) {
      dataInicio = LocalDateTime.parse(dataInicioStr + "T00:00:00");
    }

    if (dataFimStr != null && !dataFimStr.isEmpty()) {
      dataFim = LocalDateTime.parse(dataFimStr + "T23:59:59");
    }

    // Buscar todas as consultas no período
    List<ConsultaVeterinaria> todasConsultas = consultaRepo.findByDateRange(dataInicio, dataFim);

    // Aplicar filtros adicionais
    List<ConsultaVeterinaria> consultas = todasConsultas.stream()
        .filter(c -> veterinarioId == null || veterinarioId.isEmpty() ||
            (c.getVeterinario() != null && c.getVeterinario().getId().toString().equals(veterinarioId)))
        .filter(c -> tipoConsulta == null || tipoConsulta.isEmpty() ||
            (c.getTipoConsulta() != null && c.getTipoConsulta().toString().equals(tipoConsulta)))
        .filter(c -> statusConsulta == null || statusConsulta.isEmpty() ||
            (c.getStatus() != null && c.getStatus().toString().equals(statusConsulta)))
        .collect(Collectors.toList());

    // Calcular estatísticas
    Map<TipoConsulta, Long> consultasPorTipo = new HashMap<>();
    Map<StatusConsulta, Long> consultasPorStatus = new HashMap<>();
    Map<String, Long> consultasPorVeterinario = new HashMap<>();

    for (TipoConsulta tipo : TipoConsulta.values()) {
      consultasPorTipo.put(tipo, 0L);
    }

    for (StatusConsulta status : StatusConsulta.values()) {
      consultasPorStatus.put(status, 0L);
    }

    for (ConsultaVeterinaria consulta : todasConsultas) {
      // Contagem por tipo
      if (consulta.getTipoConsulta() != null) {
        consultasPorTipo.put(consulta.getTipoConsulta(),
            consultasPorTipo.getOrDefault(consulta.getTipoConsulta(), 0L) + 1);
      }

      // Contagem por status
      if (consulta.getStatus() != null) {
        consultasPorStatus.put(consulta.getStatus(),
            consultasPorStatus.getOrDefault(consulta.getStatus(), 0L) + 1);
      }

      // Contagem por veterinário
      if (consulta.getVeterinario() != null) {
        String nomeDr = consulta.getVeterinario().getNome();
        consultasPorVeterinario.put(nomeDr,
            consultasPorVeterinario.getOrDefault(nomeDr, 0L) + 1);
      }
    }

    // Obter lista de veterinários para o filtro
    List<Funcionario> veterinarios = funcionarioRepo.findByCargo(Cargo.VETERINARIO);

    // Enviar dados para a view
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

    // Encaminhar para a página JSP
    req.getRequestDispatcher("/WEB-INF/views/reports/consultas.jsp").forward(req, resp);
  }

  private void gerarRelatorioVendas(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // Obter parâmetros de filtro
    String dataInicioStr = req.getParameter("dataInicio");
    String dataFimStr = req.getParameter("dataFim");
    String tipoIngresso = req.getParameter("tipoIngresso");

    // Configurar datas padrão (últimos 30 dias se não especificado)
    LocalDateTime dataFimTemp = LocalDateTime.now();
    LocalDateTime dataInicioTemp = dataFimTemp.minusDays(30);

    if (dataInicioStr != null && !dataInicioStr.isEmpty()) {
      dataInicioTemp = LocalDateTime.parse(dataInicioStr + "T00:00:00");
    }

    if (dataFimStr != null && !dataFimStr.isEmpty()) {
      dataFimTemp = LocalDateTime.parse(dataFimStr + "T23:59:59");
    }

    // Criar variáveis finais para uso nas lambdas
    final LocalDateTime dataInicio = dataInicioTemp;
    final LocalDateTime dataFim = dataFimTemp;

    // Buscar todos os ingressos
    List<Ingresso> todosIngressos = ingressoRepo.findAll();

    // Filtrar por período e tipo
    List<Ingresso> ingressos = todosIngressos.stream()
        .filter(i -> i.getDataCompra() != null &&
            (i.getDataCompra().isAfter(dataInicio) && i.getDataCompra().isBefore(dataFim)))
        .filter(i -> tipoIngresso == null || tipoIngresso.isEmpty() ||
            i.getTipo().toString().equals(tipoIngresso))
        .collect(Collectors.toList());

    // Calcular estatísticas
    double valorTotal = ingressos.stream()
        .mapToDouble(Ingresso::getValor)
        .sum();

    int totalIngressos = ingressos.size();

    Map<TipoIngresso, List<Ingresso>> ingressosPorTipo = ingressos.stream()
        .collect(Collectors.groupingBy(Ingresso::getTipo));

    Map<TipoIngresso, DoubleSummaryStatistics> estatisticasPorTipo = new HashMap<>();
    for (TipoIngresso tipo : TipoIngresso.values()) {
      List<Ingresso> ingressosTipo = ingressosPorTipo.getOrDefault(tipo, new ArrayList<>());
      DoubleSummaryStatistics stats = ingressosTipo.stream()
          .mapToDouble(Ingresso::getValor)
          .summaryStatistics();
      estatisticasPorTipo.put(tipo, stats);
    }

    // Calcular vendas por dia para gráfico
    Map<String, Double> vendasPorDia = new HashMap<>();
    LocalDateTime atual = dataInicio;
    while (!atual.isAfter(dataFim)) {
      String diaFormatado = atual.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      LocalDateTime finalAtual = atual;

      double valorDia = ingressos.stream()
          .filter(i -> i.getDataCompra() != null &&
              i.getDataCompra().toLocalDate().equals(finalAtual.toLocalDate()))
          .mapToDouble(Ingresso::getValor)
          .sum();

      vendasPorDia.put(diaFormatado, valorDia);
      atual = atual.plusDays(1);
    }

    // Parâmetros de paginação
    int paginaAtual = 1;
    int tamanhoPagina = 10; // Fixado em 10 itens

    try {
      String paginaParam = req.getParameter("pagina");
      if (paginaParam != null && !paginaParam.isEmpty()) {
        paginaAtual = Integer.parseInt(paginaParam);
        if (paginaAtual < 1)
          paginaAtual = 1;
      }
    } catch (NumberFormatException e) {
      // Usar valores padrão em caso de erro
    }

    // Calcular total de páginas
    int totalPaginas = (int) Math.ceil((double) ingressos.size() / tamanhoPagina);
    if (paginaAtual > totalPaginas && totalPaginas > 0) {
      paginaAtual = totalPaginas;
    }

    // Calcular intervalo de páginas a exibir (para mostrar 5 páginas centralizadas
    // na atual)
    int inicio = Math.max(1, paginaAtual - 2);
    int fim = Math.min(totalPaginas, paginaAtual + 2);

    // Ajustar para sempre mostrar 5 páginas quando possível
    if (fim - inicio < 4 && totalPaginas >= 5) {
      if (paginaAtual < 3) {
        fim = Math.min(5, totalPaginas);
      } else if (paginaAtual > totalPaginas - 2) {
        inicio = Math.max(1, totalPaginas - 4);
      }
    }

    // Adicionar aos atributos da requisição
    req.setAttribute("inicio", inicio);
    req.setAttribute("fim", fim);

    // Subconjunto de ingressos para a página atual
    List<Ingresso> ingressosPaginados;
    if (ingressos.isEmpty()) {
      ingressosPaginados = ingressos;
    } else {
      int inicioPaginacao = (paginaAtual - 1) * tamanhoPagina;
      int fimPaginacao = Math.min(inicioPaginacao + tamanhoPagina, ingressos.size());
      ingressosPaginados = ingressos.subList(inicioPaginacao, fimPaginacao);
    }

    // Adicionar atributos de paginação
    req.setAttribute("ingressosPaginados", ingressosPaginados);
    req.setAttribute("paginaAtual", paginaAtual);
    req.setAttribute("totalPaginas", totalPaginas);
    req.setAttribute("tamanhoPagina", tamanhoPagina);
    req.setAttribute("totalRegistros", ingressos.size());

    // Enviar dados para a view
    req.setAttribute("ingressos", ingressos);
    req.setAttribute("totalIngressos", totalIngressos);
    req.setAttribute("valorTotal", valorTotal);
    req.setAttribute("ingressosPorTipo", ingressosPorTipo);
    req.setAttribute("estatisticasPorTipo", estatisticasPorTipo);
    req.setAttribute("tiposIngresso", TipoIngresso.values());
    req.setAttribute("dataInicio", dataInicio.format(DATE_FORMATTER));
    req.setAttribute("dataFim", dataFim.format(DATE_FORMATTER));
    req.setAttribute("vendasPorDia", vendasPorDia);

    // Percentual de ingressos utilizados
    long ingressosUtilizados = ingressos.stream()
        .filter(Ingresso::isUtilizado)
        .count();

    double percentualUtilizados = totalIngressos > 0 ? (double) ingressosUtilizados / totalIngressos * 100 : 0;

    req.setAttribute("ingressosUtilizados", ingressosUtilizados);
    req.setAttribute("percentualUtilizados", percentualUtilizados);

    // Encaminhar para a página JSP
    req.getRequestDispatcher("/WEB-INF/views/reports/vendas.jsp").forward(req, resp);
  }
}