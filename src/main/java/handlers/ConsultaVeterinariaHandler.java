package handlers;

import models.Animal;
import models.ConsultaVeterinaria;
import models.ConsultaVeterinaria.StatusConsulta;
import models.ConsultaVeterinaria.TipoConsulta;
import models.Funcionario;
import models.Funcionario.Cargo;
import repositories.AnimalRepository;
import repositories.ConsultaVeterinariaRepository;
import repositories.FuncionarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.*;

public class ConsultaVeterinariaHandler {

  private final ConsultaVeterinariaRepository consultaRepository;
  private final AnimalRepository animalRepository;
  private final FuncionarioRepository funcionarioRepository;

  public ConsultaVeterinariaHandler(
      ConsultaVeterinariaRepository consultaRepository,
      AnimalRepository animalRepository,
      FuncionarioRepository funcionarioRepository) {
    this.consultaRepository = consultaRepository;
    this.animalRepository = animalRepository;
    this.funcionarioRepository = funcionarioRepository;
  }

  public List<Animal> listarAnimais() {
    return animalRepository.findAll();
  }

  public List<Funcionario> listarVeterinarios() {
    return funcionarioRepository.findByCargo(Cargo.VETERINARIO);
  }

  public Map<String, Object> carregarDadosParaFormularioEFiltros() {
    Map<String, Object> dados = new HashMap<>();
    dados.put("animais", listarAnimais());
    dados.put("veterinarios", listarVeterinarios());
    dados.put("tiposConsulta", TipoConsulta.values());
    dados.put("statusConsulta", StatusConsulta.values());
    return dados;
  }

  public List<ConsultaVeterinaria> obterConsultasFiltradas(
      String statusParam, String animalParam, String tipoParam, String veterinarioParam) {

    List<ConsultaVeterinaria> consultas = consultaRepository.findAll();

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
      consultas.removeIf(c -> c.getVeterinario() == null ||
          !c.getVeterinario().getId().toString().equals(veterinarioParam));
    }

    return consultas;
  }

  public Optional<ConsultaVeterinaria> buscarConsultaPorId(String idStr) {
    if (idStr == null || idStr.isEmpty()) {
      return Optional.empty();
    }

    try {
      UUID id = UUID.fromString(idStr);
      return consultaRepository.findById(id);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public List<ConsultaVeterinaria> obterHistoricoConsultas(String animalIdStr) {
    if (animalIdStr == null || animalIdStr.isEmpty()) {
      return new ArrayList<>();
    }

    try {
      UUID animalId = UUID.fromString(animalIdStr);
      return consultaRepository.findByAnimalId(animalId);
    } catch (IllegalArgumentException e) {
      return new ArrayList<>();
    }
  }

  public Optional<Animal> buscarAnimalPorId(String animalIdStr) {
    if (animalIdStr == null || animalIdStr.isEmpty()) {
      return Optional.empty();
    }

    try {
      UUID animalId = UUID.fromString(animalIdStr);
      return animalRepository.findById(animalId);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public ConsultaVeterinaria salvarConsulta(
      String id, String animalIdStr, String tipoConsultaStr, String statusConsultaStr,
      String dataHoraStr, String veterinarioIdStr, String diagnostico,
      String tratamento, String medicamentos, String observacoes,
      boolean acompanhamentoNecessario, String dataRetornoStr) throws Exception {

    Animal animal = obterAnimalObrigatorio(animalIdStr);
    TipoConsulta tipoConsulta = obterTipoConsultaObrigatorio(tipoConsultaStr);
    StatusConsulta statusConsulta = obterStatusConsultaObrigatorio(statusConsultaStr);
    LocalDateTime dataHora = obterDataHora(dataHoraStr);
    Funcionario veterinario = obterVeterinario(veterinarioIdStr);
    LocalDateTime dataRetorno = acompanhamentoNecessario ? obterDataHora(dataRetornoStr) : null;

    ConsultaVeterinaria consulta;

    if (id != null && !id.isEmpty()) {
      consulta = consultaRepository.findById(UUID.fromString(id))
          .orElse(new ConsultaVeterinaria());
    } else {
      consulta = new ConsultaVeterinaria();
    }

    consulta.setAnimal(animal);
    consulta.setTipoConsulta(tipoConsulta);
    consulta.setStatus(statusConsulta);
    consulta.setDataHora(dataHora);
    consulta.setVeterinario(veterinario);
    consulta.setDiagnostico(diagnostico);
    consulta.setTratamento(tratamento);
    consulta.setMedicamentos(medicamentos);
    consulta.setObservacoes(observacoes);
    consulta.setAcompanhamentoNecessario(acompanhamentoNecessario);
    consulta.setDataRetorno(dataRetorno);

    if (id != null && !id.isEmpty()) {
      consultaRepository.update(consulta);
    } else {
      consultaRepository.save(consulta);
    }

    return consulta;
  }

  public Funcionario obterVeterinarioDaSessao(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      return null;
    }

    Object userIdObj = session.getAttribute("userId");

    if (userIdObj instanceof UUID) {
      UUID userId = (UUID) userIdObj;
      Funcionario currentUser = funcionarioRepository.findById(userId).orElse(null);

      if (currentUser != null && currentUser.getCargo() == Cargo.VETERINARIO) {
        return currentUser;
      }
    } else {
      Object userObj = session.getAttribute("user");
      if (userObj instanceof Funcionario) {
        Funcionario currentUser = (Funcionario) userObj;

        if (currentUser.getCargo() == Cargo.VETERINARIO) {
          return currentUser;
        }
      }
    }

    return null;
  }

  public <T> PageResult<T> paginarResultados(List<T> items, int page, int pageSize) {
    int totalItems = items.size();
    int totalPages = (int) Math.ceil((double) totalItems / pageSize);

    int fromIndex = (page - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, totalItems);

    List<T> pageItems = totalItems > 0
        ? items.subList(Math.min(fromIndex, totalItems), Math.min(toIndex, totalItems))
        : new ArrayList<>();

    return new PageResult<>(pageItems, page, totalPages);
  }

  private Animal obterAnimalObrigatorio(String animalIdStr) throws Exception {
    if (animalIdStr == null || animalIdStr.isEmpty()) {
      throw new Exception("Animal é obrigatório");
    }

    try {
      UUID animalId = UUID.fromString(animalIdStr);
      return animalRepository.findById(animalId)
          .orElseThrow(() -> new Exception("Animal não encontrado"));
    } catch (IllegalArgumentException e) {
      throw new Exception("ID de animal inválido");
    }
  }

  private TipoConsulta obterTipoConsultaObrigatorio(String tipoConsultaStr) throws Exception {
    if (tipoConsultaStr == null || tipoConsultaStr.isEmpty()) {
      throw new Exception("Tipo de consulta é obrigatório");
    }

    try {
      return TipoConsulta.valueOf(tipoConsultaStr);
    } catch (IllegalArgumentException e) {
      throw new Exception("Tipo de consulta inválido");
    }
  }

  private StatusConsulta obterStatusConsultaObrigatorio(String statusConsultaStr) throws Exception {
    if (statusConsultaStr == null || statusConsultaStr.isEmpty()) {
      throw new Exception("Status da consulta é obrigatório");
    }

    try {
      return StatusConsulta.valueOf(statusConsultaStr);
    } catch (IllegalArgumentException e) {
      throw new Exception("Status de consulta inválido");
    }
  }

  private LocalDateTime obterDataHora(String dataHoraStr) {
    if (dataHoraStr != null && !dataHoraStr.isEmpty()) {
      return LocalDateTime.parse(dataHoraStr);
    }
    return null;
  }

  private Funcionario obterVeterinario(String veterinarioIdStr) {
    if (veterinarioIdStr != null && !veterinarioIdStr.isEmpty()) {
      try {
        UUID veterinarioId = UUID.fromString(veterinarioIdStr);
        return funcionarioRepository.findById(veterinarioId).orElse(null);
      } catch (IllegalArgumentException e) {
        return null;
      }
    }
    return null;
  }

  public static class PageResult<T> {
    public final List<T> items;
    public final int currentPage;
    public final int totalPages;

    public PageResult(List<T> items, int currentPage, int totalPages) {
      this.items = items;
      this.currentPage = currentPage;
      this.totalPages = totalPages;
    }
  }
}
