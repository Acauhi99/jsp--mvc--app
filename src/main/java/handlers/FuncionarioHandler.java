package handlers;

import models.Funcionario;
import models.Funcionario.Cargo;
import repositories.FuncionarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FuncionarioHandler {

  private final FuncionarioRepository funcionarioRepository;

  public FuncionarioHandler() {
    this.funcionarioRepository = new FuncionarioRepository();
  }

  public List<Funcionario> listarTodosFuncionarios() {
    return funcionarioRepository.findAll();
  }

  public List<Funcionario> listarPorCargo(Cargo cargo) {
    return funcionarioRepository.findByCargo(cargo);
  }

  public Optional<Funcionario> buscarPorId(String idStr) {
    if (idStr == null || idStr.isEmpty()) {
      return Optional.empty();
    }

    try {
      UUID id = UUID.fromString(idStr);
      return funcionarioRepository.findById(id);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public ValidationResult validarDadosFuncionario(String nome, String email, String password, String cargoStr,
      boolean isNovoFuncionario) {
    ValidationResult result = new ValidationResult();

    if (nome == null || nome.isEmpty()) {
      result.addError("Nome é obrigatório");
    }

    if (email == null || email.isEmpty()) {
      result.addError("Email é obrigatório");
    }

    if (cargoStr == null || cargoStr.isEmpty()) {
      result.addError("Cargo é obrigatório");
    } else {
      try {
        Cargo.valueOf(cargoStr);
      } catch (IllegalArgumentException e) {
        result.addError("Cargo inválido");
      }
    }

    if (isNovoFuncionario && (password == null || password.isEmpty())) {
      result.addError("Senha é obrigatória para novo funcionário");
    }

    if (email != null && !email.isEmpty() && isNovoFuncionario) {
      if (funcionarioRepository.findByEmail(email).isPresent()) {
        result.addError("Email já cadastrado");
      }
    }

    return result;
  }

  public Funcionario criarFuncionario(String nome, String email, String password, String cargoStr) throws Exception {
    ValidationResult validationResult = validarDadosFuncionario(nome, email, password, cargoStr, true);

    if (!validationResult.isValid()) {
      throw new Exception(validationResult.getErrorMessage());
    }

    Cargo cargo = Cargo.valueOf(cargoStr);
    Funcionario funcionario = new Funcionario(nome, email, password, cargo);
    funcionarioRepository.save(funcionario);
    return funcionario;
  }

  public Funcionario atualizarFuncionario(String idStr, String nome, String email, String password, String cargoStr)
      throws Exception {
    ValidationResult validationResult = validarDadosFuncionario(nome, email, password, cargoStr, false);

    if (!validationResult.isValid()) {
      throw new Exception(validationResult.getErrorMessage());
    }

    UUID id = UUID.fromString(idStr);
    Optional<Funcionario> optFuncionario = funcionarioRepository.findById(id);

    if (optFuncionario.isEmpty()) {
      throw new Exception("Funcionário não encontrado");
    }

    Funcionario funcionario = optFuncionario.get();
    funcionario.setNome(nome);
    funcionario.setEmail(email);
    funcionario.setCargo(Cargo.valueOf(cargoStr));

    if (password != null && !password.isEmpty()) {
      funcionario.setPassword(password);
    }

    funcionarioRepository.update(funcionario);
    return funcionario;
  }

  public void excluirFuncionario(String idStr) throws Exception {
    if (idStr == null || idStr.isEmpty()) {
      throw new Exception("ID do funcionário não fornecido");
    }

    try {
      UUID id = UUID.fromString(idStr);
      funcionarioRepository.delete(id);
    } catch (IllegalArgumentException e) {
      throw new Exception("ID do funcionário inválido");
    } catch (Exception e) {
      throw new Exception("Erro ao excluir funcionário: " + e.getMessage());
    }
  }

  public static class ValidationResult {
    private final StringBuilder errorMessage = new StringBuilder();
    private boolean isValid = true;

    public void addError(String error) {
      if (isValid) {
        isValid = false;
      } else {
        errorMessage.append("; ");
      }
      errorMessage.append(error);
    }

    public boolean isValid() {
      return isValid;
    }

    public String getErrorMessage() {
      return errorMessage.toString();
    }
  }
}
