package handlers;

import models.Alimentacao;
import models.Animal;
import models.Funcionario;
import repositories.AlimentacaoRepository;
import repositories.AnimalRepository;
import repositories.FuncionarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AlimentacaoHandler {

  private final AlimentacaoRepository alimentacaoRepository;
  private final AnimalRepository animalRepository;
  private final FuncionarioRepository funcionarioRepository;

  public AlimentacaoHandler(AlimentacaoRepository alimentacaoRepository,
      AnimalRepository animalRepository,
      FuncionarioRepository funcionarioRepository) {
    this.alimentacaoRepository = alimentacaoRepository;
    this.animalRepository = animalRepository;
    this.funcionarioRepository = funcionarioRepository;
  }

  public List<Animal> getAllAnimais() {
    return animalRepository.findAll();
  }

  public List<Funcionario> getAllFuncionarios() {
    return funcionarioRepository.findAll();
  }

  public List<Alimentacao> getAllAlimentacoes() {
    return alimentacaoRepository.findAll();
  }

  public List<Alimentacao> getAlimentacoesByAnimalId(String animalIdStr) {
    if (animalIdStr == null || animalIdStr.isEmpty()) {
      return getAllAlimentacoes();
    }

    UUID animalId = parseUUID(animalIdStr);
    return alimentacaoRepository.findByAnimalId(animalId);
  }

  public Optional<Alimentacao> getAlimentacaoById(String idStr) {
    UUID alimentacaoId = parseUUID(idStr);
    return alimentacaoRepository.findById(alimentacaoId);
  }

  public void createAlimentacao(String animalIdStr, String tipoAlimento, String quantidadeStr,
      String unidadeMedida, String funcionarioIdStr, String observacoes)
      throws Exception {
    validateRequiredFields(animalIdStr, tipoAlimento, quantidadeStr, funcionarioIdStr);

    UUID animalId = parseUUID(animalIdStr);
    Double quantidade = parseDouble(quantidadeStr);
    UUID funcionarioId = parseUUID(funcionarioIdStr);

    Animal animal = findAnimal(animalId);
    Funcionario funcionario = findFuncionario(funcionarioId);

    Alimentacao alimentacao = Alimentacao.create(
        animal,
        tipoAlimento,
        quantidade,
        unidadeMedida,
        LocalDateTime.now(),
        funcionario,
        observacoes);

    alimentacaoRepository.save(alimentacao);
  }

  public void updateAlimentacao(String idStr, String animalIdStr, String tipoAlimento,
      String quantidadeStr, String unidadeMedida,
      String funcionarioIdStr, String observacoes)
      throws Exception {
    validateRequiredFields(animalIdStr, tipoAlimento, quantidadeStr, funcionarioIdStr);

    UUID id = parseUUID(idStr);
    UUID animalId = parseUUID(animalIdStr);
    Double quantidade = parseDouble(quantidadeStr);
    UUID funcionarioId = parseUUID(funcionarioIdStr);

    Animal animal = findAnimal(animalId);
    Funcionario funcionario = findFuncionario(funcionarioId);

    Optional<Alimentacao> optAlimentacao = alimentacaoRepository.findById(id);
    if (optAlimentacao.isEmpty()) {
      throw new Exception("Alimentação não encontrada");
    }

    Alimentacao alimentacao = optAlimentacao.get();
    alimentacao.setAnimal(animal);
    alimentacao.setTipoAlimento(tipoAlimento);
    alimentacao.setQuantidade(quantidade);
    alimentacao.setUnidadeMedida(unidadeMedida);
    alimentacao.setFuncionarioResponsavel(funcionario);
    alimentacao.setObservacoes(observacoes);

    alimentacaoRepository.update(alimentacao);
  }

  public void deleteAlimentacao(String idStr) throws Exception {
    UUID alimentacaoId = parseUUID(idStr);
    Optional<Alimentacao> alimentacao = alimentacaoRepository.findById(alimentacaoId);

    if (alimentacao.isEmpty()) {
      throw new Exception("Alimentação não encontrada");
    }

    alimentacaoRepository.delete(alimentacaoId);
  }

  private void validateRequiredFields(String animalIdStr, String tipoAlimento,
      String quantidadeStr, String funcionarioIdStr)
      throws Exception {
    if (animalIdStr == null || animalIdStr.trim().isEmpty()) {
      throw new Exception("Animal é obrigatório");
    }
    if (tipoAlimento == null || tipoAlimento.trim().isEmpty()) {
      throw new Exception("Tipo de alimento é obrigatório");
    }
    if (quantidadeStr == null || quantidadeStr.trim().isEmpty()) {
      throw new Exception("Quantidade é obrigatória");
    }
    if (funcionarioIdStr == null || funcionarioIdStr.trim().isEmpty()) {
      throw new Exception("Funcionário responsável é obrigatório");
    }
  }

  private UUID parseUUID(String uuidStr) {
    if (uuidStr == null || uuidStr.trim().isEmpty()) {
      throw new IllegalArgumentException("UUID não pode ser nulo ou vazio");
    }
    try {
      return UUID.fromString(uuidStr);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("UUID inválido: " + uuidStr);
    }
  }

  private Double parseDouble(String doubleStr) {
    if (doubleStr == null || doubleStr.trim().isEmpty()) {
      throw new IllegalArgumentException("Valor numérico não pode ser nulo ou vazio");
    }
    try {
      return Double.parseDouble(doubleStr);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Valor numérico inválido: " + doubleStr);
    }
  }

  private Animal findAnimal(UUID animalId) throws Exception {
    Optional<Animal> optAnimal = animalRepository.findById(animalId);
    if (optAnimal.isEmpty()) {
      throw new Exception("Animal não encontrado");
    }
    return optAnimal.get();
  }

  private Funcionario findFuncionario(UUID funcionarioId) throws Exception {
    Optional<Funcionario> optFuncionario = funcionarioRepository.findById(funcionarioId);
    if (optFuncionario.isEmpty()) {
      throw new Exception("Funcionário não encontrado");
    }
    return optFuncionario.get();
  }
}
