package handlers;

import models.Habitat;
import models.Habitat.TipoAmbiente;
import repositories.HabitatRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HabitatHandler {

  private final HabitatRepository habitatRepository;

  public HabitatHandler() {
    this.habitatRepository = new HabitatRepository();
  }

  public List<Habitat> listarTodosHabitats() {
    return habitatRepository.findAll();
  }

  public List<Habitat> listarHabitatsPorTipoAmbiente(String tipoAmbienteStr) {
    if (tipoAmbienteStr == null || tipoAmbienteStr.isEmpty()) {
      return listarTodosHabitats();
    }

    try {
      TipoAmbiente tipoAmbiente = TipoAmbiente.valueOf(tipoAmbienteStr);
      return habitatRepository.findByTipoAmbiente(tipoAmbiente);
    } catch (IllegalArgumentException e) {
      return listarTodosHabitats();
    }
  }

  public List<Habitat> listarHabitatsPorAcessibilidade(boolean publicoAcessivel) {
    return habitatRepository.findByPublicoAcessivel(publicoAcessivel);
  }

  public Optional<Habitat> buscarHabitatPorId(String idStr) {
    if (idStr == null || idStr.isEmpty()) {
      return Optional.empty();
    }

    try {
      UUID id = UUID.fromString(idStr);
      return habitatRepository.findById(id);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public Habitat criarHabitat(String nome, String tipoAmbienteStr, String tamanhoStr,
      String capacidadeStr, String publicoAcessivelStr) throws Exception {

    validarDadosHabitat(nome, tipoAmbienteStr);

    TipoAmbiente tipoAmbiente = TipoAmbiente.valueOf(tipoAmbienteStr);
    double tamanho = parseDouble(tamanhoStr, 0.0);
    int capacidade = parseInt(capacidadeStr, 0);
    boolean publicoAcessivel = "on".equals(publicoAcessivelStr);

    Habitat habitat = Habitat.create(
        nome,
        tipoAmbiente,
        tamanho,
        capacidade,
        publicoAcessivel);

    habitatRepository.save(habitat);
    return habitat;
  }

  public Habitat atualizarHabitat(String idStr, String nome, String tipoAmbienteStr,
      String tamanhoStr, String capacidadeStr,
      String publicoAcessivelStr) throws Exception {

    validarDadosHabitat(nome, tipoAmbienteStr);

    UUID id = UUID.fromString(idStr);
    Optional<Habitat> optHabitat = habitatRepository.findById(id);

    if (optHabitat.isEmpty()) {
      throw new Exception("Habitat não encontrado");
    }

    Habitat habitat = optHabitat.get();
    habitat.setNome(nome);
    habitat.setTipoAmbiente(TipoAmbiente.valueOf(tipoAmbienteStr));
    habitat.setTamanho(parseDouble(tamanhoStr, habitat.getTamanho()));
    habitat.setCapacidadeMaximaAnimais(parseInt(capacidadeStr, habitat.getCapacidadeMaximaAnimais()));
    habitat.setPublicoAcessivel("on".equals(publicoAcessivelStr));

    habitatRepository.update(habitat);
    return habitat;
  }

  public void excluirHabitat(String idStr) throws Exception {
    if (idStr == null || idStr.isEmpty()) {
      throw new Exception("ID do habitat não fornecido");
    }

    try {
      UUID id = UUID.fromString(idStr);
      Optional<Habitat> optHabitat = habitatRepository.findById(id);

      if (optHabitat.isEmpty()) {
        throw new Exception("Habitat não encontrado");
      }

      if (!optHabitat.get().getAnimais().isEmpty()) {
        throw new Exception("Não é possível excluir um habitat que contém animais");
      }

      habitatRepository.delete(id);
    } catch (IllegalArgumentException e) {
      throw new Exception("ID de habitat inválido");
    }
  }

  private void validarDadosHabitat(String nome, String tipoAmbienteStr) throws Exception {
    if (nome == null || nome.isEmpty()) {
      throw new Exception("Nome do habitat é obrigatório");
    }

    if (tipoAmbienteStr == null || tipoAmbienteStr.isEmpty()) {
      throw new Exception("Tipo de ambiente é obrigatório");
    }

    try {
      TipoAmbiente.valueOf(tipoAmbienteStr);
    } catch (IllegalArgumentException e) {
      throw new Exception("Tipo de ambiente inválido");
    }
  }

  private double parseDouble(String value, double defaultValue) {
    if (value == null || value.isEmpty()) {
      return defaultValue;
    }
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  private int parseInt(String value, int defaultValue) {
    if (value == null || value.isEmpty()) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
