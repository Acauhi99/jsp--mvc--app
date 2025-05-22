package handlers;

import models.Animal;
import models.Animal.Classe;
import models.Animal.Genero;
import models.Animal.StatusSaude;
import models.Habitat;
import models.Habitat.TipoAmbiente;
import repositories.AnimalRepository;
import repositories.HabitatRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;

public class AnimalHandler {

  private final AnimalRepository animalRepository;
  private final HabitatRepository habitatRepository;

  public AnimalHandler() {
    this.animalRepository = new AnimalRepository();
    this.habitatRepository = new HabitatRepository();
  }

  public List<Animal> listarAnimaisFiltrados(String especie, String classe, String genero,
      String tipoAmbiente, int page, int pageSize) {
    int offset = (page - 1) * pageSize;
    return animalRepository.findFiltered(especie, classe, genero, tipoAmbiente, offset, pageSize);
  }

  public long contarAnimaisFiltrados(String especie, String classe, String genero, String tipoAmbiente) {
    return animalRepository.countFiltered(especie, classe, genero, tipoAmbiente);
  }

  public Optional<Animal> buscarPorId(String idStr) {
    if (idStr == null || idStr.trim().isEmpty()) {
      return Optional.empty();
    }

    try {
      UUID id = UUID.fromString(idStr);
      return animalRepository.findById(id);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public List<Habitat> listarTodosHabitats() {
    return habitatRepository.findAll();
  }

  public Map<String, Object> obterDadosFormulario() {
    Map<String, Object> dados = new HashMap<>();
    dados.put("classes", Classe.values());
    dados.put("generos", Genero.values());
    dados.put("statusSaude", StatusSaude.values());
    dados.put("habitats", habitatRepository.findAll());
    dados.put("tiposAmbiente", TipoAmbiente.values());
    return dados;
  }

  public Animal criarAnimal(String nome, String especie, String nomeCientifico, String classe,
      String genero, String habitatIdStr, String statusSaude,
      String dataChegadaStr, String detalhesSaude) throws Exception {

    validarCamposObrigatorios(nome, especie);

    Animal animal = new Animal();
    animal.setNome(nome);
    animal.setEspecie(especie);
    animal.setNomeCientifico(nomeCientifico);

    if (classe != null && !classe.isEmpty()) {
      animal.setClasse(Classe.valueOf(classe));
    }

    if (genero != null && !genero.isEmpty()) {
      animal.setGenero(Genero.valueOf(genero));
    }

    if (statusSaude != null && !statusSaude.isEmpty()) {
      animal.setStatusSaude(StatusSaude.valueOf(statusSaude));
    }

    animal.setDetalhesSaude(detalhesSaude);

    if (dataChegadaStr != null && !dataChegadaStr.isEmpty()) {
      try {
        animal.setDataChegada(LocalDate.parse(dataChegadaStr));
      } catch (Exception e) {
        throw new Exception("Formato de data inválido. Use o formato YYYY-MM-DD.");
      }
    }

    if (habitatIdStr != null && !habitatIdStr.isEmpty()) {
      try {
        UUID habitatId = UUID.fromString(habitatIdStr);
        Optional<Habitat> optHabitat = habitatRepository.findById(habitatId);

        if (optHabitat.isEmpty()) {
          throw new Exception("Habitat não encontrado.");
        }

        animal.setHabitat(optHabitat.get());
      } catch (IllegalArgumentException e) {
        throw new Exception("ID de habitat inválido.");
      }
    }

    try {
      animalRepository.save(animal);
      return animal;
    } catch (Exception e) {
      throw new Exception("Erro ao salvar animal: " + e.getMessage());
    }
  }

  public Animal atualizarAnimal(String idStr, String nome, String especie, String nomeCientifico,
      String classeStr, String generoStr, String habitatIdStr,
      String statusSaudeStr, String dataChegadaStr, String detalhesSaude) throws Exception {

    if (idStr == null || idStr.isEmpty()) {
      throw new Exception("ID do animal é obrigatório para atualização");
    }

    validarCamposObrigatorios(nome, especie);

    UUID id;
    try {
      id = UUID.fromString(idStr);
    } catch (IllegalArgumentException e) {
      throw new Exception("ID do animal inválido");
    }

    Optional<Animal> optAnimal = animalRepository.findById(id);
    if (optAnimal.isEmpty()) {
      throw new Exception("Animal não encontrado");
    }

    Animal animal = optAnimal.get();
    animal.setNome(nome);
    animal.setEspecie(especie);
    animal.setNomeCientifico(nomeCientifico);

    if (classeStr != null && !classeStr.isEmpty()) {
      try {
        animal.setClasse(Classe.valueOf(classeStr));
      } catch (IllegalArgumentException e) {
        throw new Exception("Classe de animal inválida");
      }
    }

    if (generoStr != null && !generoStr.isEmpty()) {
      try {
        animal.setGenero(Genero.valueOf(generoStr));
      } catch (IllegalArgumentException e) {
        throw new Exception("Gênero de animal inválido");
      }
    }

    if (statusSaudeStr != null && !statusSaudeStr.isEmpty()) {
      try {
        animal.setStatusSaude(StatusSaude.valueOf(statusSaudeStr));
      } catch (IllegalArgumentException e) {
        throw new Exception("Status de saúde inválido");
      }
    }

    animal.setDetalhesSaude(detalhesSaude);

    if (dataChegadaStr != null && !dataChegadaStr.isEmpty()) {
      try {
        animal.setDataChegada(LocalDate.parse(dataChegadaStr));
      } catch (Exception e) {
        throw new Exception("Formato de data inválido. Use o formato YYYY-MM-DD.");
      }
    }

    if (habitatIdStr != null && !habitatIdStr.isEmpty()) {
      try {
        UUID habitatId = UUID.fromString(habitatIdStr);
        Optional<Habitat> optHabitat = habitatRepository.findById(habitatId);

        if (optHabitat.isEmpty()) {
          throw new Exception("Habitat não encontrado.");
        }

        animal.setHabitat(optHabitat.get());
      } catch (IllegalArgumentException e) {
        throw new Exception("ID de habitat inválido.");
      }
    } else {
      animal.setHabitat(null);
    }

    try {
      animalRepository.update(animal);
      return animal;
    } catch (Exception e) {
      throw new Exception("Erro ao atualizar animal: " + e.getMessage());
    }
  }

  public void excluirAnimal(String idStr) throws Exception {
    if (idStr == null || idStr.isEmpty()) {
      throw new Exception("ID do animal é obrigatório para exclusão");
    }

    UUID id;
    try {
      id = UUID.fromString(idStr);
    } catch (IllegalArgumentException e) {
      throw new Exception("ID do animal inválido");
    }

    Optional<Animal> optAnimal = animalRepository.findById(id);
    if (optAnimal.isEmpty()) {
      throw new Exception("Animal não encontrado");
    }

    try {
      animalRepository.delete(id);
    } catch (Exception e) {
      throw new Exception("Erro ao excluir animal: " + e.getMessage());
    }
  }

  private void validarCamposObrigatorios(String nome, String especie) throws Exception {
    if (nome == null || nome.trim().isEmpty()) {
      throw new Exception("Nome do animal é obrigatório");
    }
    if (especie == null || especie.trim().isEmpty()) {
      throw new Exception("Espécie do animal é obrigatória");
    }
  }
}
