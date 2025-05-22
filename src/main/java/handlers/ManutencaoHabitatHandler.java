package handlers;

import models.Funcionario;
import models.Funcionario.Cargo;
import models.Habitat;
import models.ManutencaoHabitat;
import models.ManutencaoHabitat.StatusManutencao;
import models.ManutencaoHabitat.PrioridadeManutencao;
import models.ManutencaoHabitat.TipoManutencao;
import repositories.HabitatRepository;
import repositories.ManutencaoHabitatRepository;
import repositories.FuncionarioRepository;

import java.time.LocalDateTime;
import java.util.*;

public class ManutencaoHabitatHandler {

  private final ManutencaoHabitatRepository manutencaoRepository;
  private final HabitatRepository habitatRepository;
  private final FuncionarioRepository funcionarioRepository;

  public ManutencaoHabitatHandler(
      ManutencaoHabitatRepository manutencaoRepository,
      HabitatRepository habitatRepository,
      FuncionarioRepository funcionarioRepository) {
    this.manutencaoRepository = manutencaoRepository;
    this.habitatRepository = habitatRepository;
    this.funcionarioRepository = funcionarioRepository;
  }

  public List<ManutencaoHabitat> listarManutencoes(
      String statusParam, String prioridadeParam,
      String habitatIdParam, String responsavelIdParam) {

    StatusManutencao status = null;
    if (statusParam != null && !statusParam.isEmpty()) {
      try {
        status = StatusManutencao.valueOf(statusParam);
      } catch (IllegalArgumentException e) {
        // Ignora valor inválido
      }
    }

    PrioridadeManutencao prioridade = null;
    if (prioridadeParam != null && !prioridadeParam.isEmpty()) {
      try {
        prioridade = PrioridadeManutencao.valueOf(prioridadeParam);
      } catch (IllegalArgumentException e) {
        // Ignora valor inválido
      }
    }

    UUID habitatId = null;
    if (habitatIdParam != null && !habitatIdParam.isEmpty()) {
      try {
        habitatId = UUID.fromString(habitatIdParam);
      } catch (IllegalArgumentException e) {
        // Ignora valor inválido
      }
    }

    UUID responsavelId = null;
    if (responsavelIdParam != null && !responsavelIdParam.isEmpty()) {
      try {
        responsavelId = UUID.fromString(responsavelIdParam);
      } catch (IllegalArgumentException e) {
        // Ignora valor inválido
      }
    }

    return manutencaoRepository.findWithFilters(status, prioridade, habitatId, responsavelId);
  }

  public List<Habitat> listarHabitats() {
    return habitatRepository.findAll();
  }

  public List<Funcionario> listarManutentores() {
    return funcionarioRepository.findByCargo(Cargo.MANUTENCAO);
  }

  public Map<String, Object> carregarDadosSelecao() {
    Map<String, Object> dados = new HashMap<>();
    dados.put("habitats", listarHabitats());
    dados.put("manutentores", listarManutentores());
    dados.put("tiposManutencao", TipoManutencao.values());
    dados.put("prioridades", PrioridadeManutencao.values());
    dados.put("statusOpcoes", StatusManutencao.values());
    return dados;
  }

  public Optional<ManutencaoHabitat> buscarPorId(String idStr) {
    if (idStr == null || idStr.isEmpty()) {
      return Optional.empty();
    }

    try {
      UUID id = UUID.fromString(idStr);
      return manutencaoRepository.findById(id);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public PageResult<ManutencaoHabitat> aplicarPaginacao(List<ManutencaoHabitat> lista, int page, int pageSize) {
    int totalItems = lista.size();
    int totalPages = (int) Math.ceil((double) totalItems / pageSize);
    int fromIndex = (page - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, totalItems);

    List<ManutencaoHabitat> pageItems = new ArrayList<>();
    if (totalItems > 0) {
      pageItems = lista.subList(Math.min(fromIndex, totalItems), Math.min(toIndex, totalItems));
    }

    return new PageResult<>(pageItems, page, totalPages);
  }

  public Habitat obterHabitat(String habitatIdStr) throws Exception {
    if (habitatIdStr == null || habitatIdStr.isEmpty()) {
      throw new Exception("Habitat é obrigatório");
    }

    try {
      UUID habitatId = UUID.fromString(habitatIdStr);
      return habitatRepository.findById(habitatId)
          .orElseThrow(() -> new Exception("Habitat não encontrado"));
    } catch (IllegalArgumentException e) {
      throw new Exception("ID de habitat inválido");
    }
  }

  public Funcionario obterResponsavel(String responsavelIdStr) {
    if (responsavelIdStr == null || responsavelIdStr.isEmpty()) {
      return null;
    }

    try {
      UUID responsavelId = UUID.fromString(responsavelIdStr);
      return funcionarioRepository.findById(responsavelId).orElse(null);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  public Funcionario obterFuncionarioPorId(UUID funcionarioId) {
    if (funcionarioId == null) {
      return null;
    }

    return funcionarioRepository.findById(funcionarioId).orElse(null);
  }

  public LocalDateTime parseDataProgramada(String dataStr) {
    if (dataStr == null || dataStr.isEmpty()) {
      return null;
    }

    try {
      return LocalDateTime.parse(dataStr);
    } catch (Exception e) {
      return null;
    }
  }

  public ManutencaoHabitat criarOuAtualizarManutencao(
      String idStr, String habitatIdStr, String tipoStr, String prioridadeStr,
      String descricao, String dataProgramadaStr, UUID solicitanteId,
      String responsavelIdStr, String statusStr) throws Exception {

    ManutencaoHabitat manutencao;

    validarCamposObrigatorios(habitatIdStr, tipoStr, prioridadeStr, descricao);

    Habitat habitat = obterHabitat(habitatIdStr);
    TipoManutencao tipo = TipoManutencao.valueOf(tipoStr);
    PrioridadeManutencao prioridade = PrioridadeManutencao.valueOf(prioridadeStr);
    LocalDateTime dataProgramada = parseDataProgramada(dataProgramadaStr);
    Funcionario solicitante = obterFuncionarioPorId(solicitanteId);
    Funcionario responsavel = obterResponsavel(responsavelIdStr);

    if (solicitante == null) {
      throw new Exception("Solicitante não encontrado");
    }

    if (idStr != null && !idStr.isEmpty()) {
      try {
        UUID id = UUID.fromString(idStr);
        manutencao = manutencaoRepository.findById(id).orElse(new ManutencaoHabitat());
      } catch (IllegalArgumentException e) {
        throw new Exception("ID de manutenção inválido");
      }
    } else {
      manutencao = new ManutencaoHabitat();
      manutencao.setStatus(StatusManutencao.PENDENTE);
      manutencao.setDataSolicitacao(LocalDateTime.now());
    }

    manutencao.setHabitat(habitat);
    manutencao.setTipoManutencao(tipo);
    manutencao.setPrioridade(prioridade);
    manutencao.setDescricao(descricao);
    manutencao.setDataProgramada(dataProgramada);
    manutencao.setSolicitante(solicitante);
    manutencao.setResponsavel(responsavel);

    if (statusStr != null && !statusStr.isEmpty()) {
      try {
        StatusManutencao status = StatusManutencao.valueOf(statusStr);
        manutencao.setStatus(status);

        if (status == StatusManutencao.CONCLUIDA && manutencao.getDataConclusao() == null) {
          manutencao.setDataConclusao(LocalDateTime.now());
        }
      } catch (IllegalArgumentException e) {
        // Mantém o status atual
      }
    }

    if (idStr != null && !idStr.isEmpty()) {
      manutencaoRepository.update(manutencao);
    } else {
      manutencaoRepository.save(manutencao);
    }

    return manutencao;
  }

  public void concluirManutencao(String idStr, UUID responsavelId, String observacaoConclusao) throws Exception {
    if (idStr == null || idStr.isEmpty()) {
      throw new Exception("ID de manutenção não fornecido");
    }

    try {
      UUID id = UUID.fromString(idStr);
      Optional<ManutencaoHabitat> optManutencao = manutencaoRepository.findById(id);

      if (optManutencao.isEmpty()) {
        throw new Exception("Manutenção não encontrada");
      }

      Funcionario responsavel = obterFuncionarioPorId(responsavelId);
      if (responsavel == null) {
        throw new Exception("Responsável não encontrado");
      }

      ManutencaoHabitat manutencao = optManutencao.get();
      manutencao.concluir(responsavel, LocalDateTime.now(), observacaoConclusao);
      manutencaoRepository.update(manutencao);
    } catch (IllegalArgumentException e) {
      throw new Exception("ID de manutenção inválido");
    }
  }

  private void validarCamposObrigatorios(String habitatIdStr, String tipoStr,
      String prioridadeStr, String descricao) throws Exception {
    if (habitatIdStr == null || habitatIdStr.isEmpty()) {
      throw new Exception("Habitat é obrigatório");
    }

    if (tipoStr == null || tipoStr.isEmpty()) {
      throw new Exception("Tipo de manutenção é obrigatório");
    }

    if (prioridadeStr == null || prioridadeStr.isEmpty()) {
      throw new Exception("Prioridade é obrigatória");
    }

    if (descricao == null || descricao.trim().isEmpty()) {
      throw new Exception("Descrição é obrigatória");
    }
  }

  public static class PageResult<T> {
    private final List<T> items;
    private final int currentPage;
    private final int totalPages;

    public PageResult(List<T> items, int currentPage, int totalPages) {
      this.items = items;
      this.currentPage = currentPage;
      this.totalPages = totalPages;
    }

    public List<T> getItems() {
      return items;
    }

    public int getCurrentPage() {
      return currentPage;
    }

    public int getTotalPages() {
      return totalPages;
    }
  }
}
