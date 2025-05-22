package handlers;

import models.ConsultaVeterinaria;
import models.ConsultaVeterinaria.StatusConsulta;
import models.ConsultaVeterinaria.TipoConsulta;
import models.Funcionario;
import models.Funcionario.Cargo;
import models.Ingresso;
import models.Ingresso.TipoIngresso;
import repositories.ConsultaVeterinariaRepository;
import repositories.FuncionarioRepository;
import repositories.IngressoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportHandler {

  private final ConsultaVeterinariaRepository consultaRepository;
  private final IngressoRepository ingressoRepository;
  private final FuncionarioRepository funcionarioRepository;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public ReportHandler(
      ConsultaVeterinariaRepository consultaRepository,
      IngressoRepository ingressoRepository,
      FuncionarioRepository funcionarioRepository) {
    this.consultaRepository = consultaRepository;
    this.ingressoRepository = ingressoRepository;
    this.funcionarioRepository = funcionarioRepository;
  }

  public Map<String, Object> gerarRelatorioConsultas(
      String dataInicioStr, String dataFimStr,
      String veterinarioId, String tipoConsulta, String statusConsulta) {

    LocalDateTime dataInicio = LocalDateTime.now().minusDays(30);
    LocalDateTime dataFim = LocalDateTime.now();

    if (dataInicioStr != null && !dataInicioStr.isEmpty()) {
      dataInicio = LocalDateTime.parse(dataInicioStr + "T00:00:00");
    }

    if (dataFimStr != null && !dataFimStr.isEmpty()) {
      dataFim = LocalDateTime.parse(dataFimStr + "T23:59:59");
    }

    List<ConsultaVeterinaria> todasConsultas = consultaRepository.findByDateRange(dataInicio, dataFim);
    List<ConsultaVeterinaria> consultas = filtrarConsultas(
        todasConsultas, veterinarioId, tipoConsulta, statusConsulta);

    Map<TipoConsulta, Long> consultasPorTipo = inicializarMapaTipoConsulta();
    Map<StatusConsulta, Long> consultasPorStatus = inicializarMapaStatusConsulta();
    Map<String, Long> consultasPorVeterinario = new HashMap<>();

    calcularEstatisticasConsulta(
        todasConsultas, consultasPorTipo, consultasPorStatus, consultasPorVeterinario);
    List<Funcionario> veterinarios = funcionarioRepository.findByCargo(Cargo.VETERINARIO);

    Map<String, Object> resultado = new HashMap<>();
    resultado.put("consultas", consultas);
    resultado.put("totalConsultas", todasConsultas.size());
    resultado.put("consultasPorTipo", consultasPorTipo);
    resultado.put("consultasPorStatus", consultasPorStatus);
    resultado.put("consultasPorVeterinario", consultasPorVeterinario);
    resultado.put("veterinarios", veterinarios);
    resultado.put("tiposConsulta", TipoConsulta.values());
    resultado.put("statusConsulta", StatusConsulta.values());
    resultado.put("dataInicio", dataInicio.format(DATE_FORMATTER));
    resultado.put("dataFim", dataFim.format(DATE_FORMATTER));

    return resultado;
  }

  public Map<String, Object> gerarRelatorioVendas(
      String dataInicioStr, String dataFimStr,
      String tipoIngresso, int paginaAtual, int tamanhoPagina) {

    LocalDateTime dataInicio = LocalDateTime.now().minusDays(30);
    LocalDateTime dataFim = LocalDateTime.now();

    if (dataInicioStr != null && !dataInicioStr.isEmpty()) {
      dataInicio = LocalDateTime.parse(dataInicioStr + "T00:00:00");
    }

    if (dataFimStr != null && !dataFimStr.isEmpty()) {
      dataFim = LocalDateTime.parse(dataFimStr + "T23:59:59");
    }

    List<Ingresso> todosIngressos = ingressoRepository.findAll();
    List<Ingresso> ingressos = filtrarIngressos(todosIngressos, dataInicio, dataFim, tipoIngresso);

    double valorTotal = ingressos.stream().mapToDouble(Ingresso::getValor).sum();
    int totalIngressos = ingressos.size();

    Map<TipoIngresso, List<Ingresso>> ingressosPorTipo = ingressos.stream()
        .collect(Collectors.groupingBy(Ingresso::getTipo));

    Map<TipoIngresso, DoubleSummaryStatistics> estatisticasPorTipo = calcularEstatisticasPorTipo(ingressosPorTipo);
    Map<String, Double> vendasPorDia = calcularVendasPorDia(ingressos, dataInicio, dataFim);

    int totalRegistros = ingressos.size();
    int totalPaginas = (int) Math.ceil((double) totalRegistros / tamanhoPagina);

    if (paginaAtual < 1)
      paginaAtual = 1;
    if (paginaAtual > totalPaginas && totalPaginas > 0)
      paginaAtual = totalPaginas;

    int inicio = Math.max(1, paginaAtual - 2);
    int fim = Math.min(totalPaginas, paginaAtual + 2);

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

    Map<String, Object> resultado = new HashMap<>();
    resultado.put("ingressos", ingressos);
    resultado.put("ingressosPaginados", ingressosPaginados);
    resultado.put("paginaAtual", paginaAtual);
    resultado.put("totalPaginas", totalPaginas);
    resultado.put("tamanhoPagina", tamanhoPagina);
    resultado.put("totalRegistros", totalRegistros);
    resultado.put("inicio", inicio);
    resultado.put("fim", fim);
    resultado.put("totalIngressos", totalIngressos);
    resultado.put("valorTotal", valorTotal);
    resultado.put("ingressosPorTipo", ingressosPorTipo);
    resultado.put("estatisticasPorTipo", estatisticasPorTipo);
    resultado.put("tiposIngresso", TipoIngresso.values());
    resultado.put("dataInicio", dataInicio.format(DATE_FORMATTER));
    resultado.put("dataFim", dataFim.format(DATE_FORMATTER));
    resultado.put("vendasPorDia", vendasPorDia);
    resultado.put("ingressosUtilizados", ingressosUtilizados);
    resultado.put("percentualUtilizados", percentualUtilizados);

    return resultado;
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

  private List<ConsultaVeterinaria> filtrarConsultas(
      List<ConsultaVeterinaria> consultas,
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

  private void calcularEstatisticasConsulta(
      List<ConsultaVeterinaria> consultas,
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

  private List<Ingresso> filtrarIngressos(
      List<Ingresso> ingressos,
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

  private Map<String, Double> calcularVendasPorDia(
      List<Ingresso> ingressos,
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
