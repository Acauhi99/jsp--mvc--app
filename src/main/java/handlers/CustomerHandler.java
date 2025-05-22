package handlers;

import models.Customer;
import repositories.CustomerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerHandler {

  private final CustomerRepository customerRepository;

  public CustomerHandler(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public List<Customer> listarTodosVisitantes() {
    return customerRepository.findAllWithIngressos();
  }

  public Optional<Customer> buscarVisitantePorId(String idStr) {
    if (idStr == null || idStr.isEmpty()) {
      return Optional.empty();
    }

    try {
      UUID id = UUID.fromString(idStr);
      return customerRepository.findById(id);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public Optional<Customer> buscarVisitantePorIdComIngressos(String idStr) {
    if (idStr == null || idStr.isEmpty()) {
      return Optional.empty();
    }

    try {
      UUID id = UUID.fromString(idStr);
      return customerRepository.findByIdWithIngressos(id);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public ValidationResult validarDadosVisitante(String nome, String email, String password, boolean isNovoVisitante) {
    ValidationResult result = new ValidationResult();

    if (nome == null || nome.isEmpty()) {
      result.addError("Nome é obrigatório");
    }

    if (email == null || email.isEmpty()) {
      result.addError("Email é obrigatório");
    }

    if (isNovoVisitante && (password == null || password.isEmpty())) {
      result.addError("Senha é obrigatória para novo visitante");
    }

    if (email != null && !email.isEmpty() && isNovoVisitante) {
      if (customerRepository.findByEmail(email).isPresent()) {
        result.addError("Email já cadastrado");
      }
    }

    return result;
  }

  public Customer criarVisitante(String nome, String email, String password) throws Exception {
    ValidationResult validationResult = validarDadosVisitante(nome, email, password, true);

    if (!validationResult.isValid()) {
      throw new Exception(validationResult.getErrorMessage());
    }

    Customer visitante = new Customer(nome, email, password);
    customerRepository.save(visitante);
    return visitante;
  }

  public Customer atualizarVisitante(String idStr, String nome, String email, String password) throws Exception {
    ValidationResult validationResult = validarDadosVisitante(nome, email, password, false);

    if (!validationResult.isValid()) {
      throw new Exception(validationResult.getErrorMessage());
    }

    UUID id = UUID.fromString(idStr);
    Optional<Customer> optVisitante = customerRepository.findById(id);

    if (optVisitante.isEmpty()) {
      throw new Exception("Visitante não encontrado");
    }

    Customer visitante = optVisitante.get();
    visitante.setNome(nome);
    visitante.setEmail(email);

    if (password != null && !password.isEmpty()) {
      visitante.setPassword(password);
    }

    customerRepository.update(visitante);
    return visitante;
  }

  public void excluirVisitante(String idStr) throws Exception {
    if (idStr == null || idStr.isEmpty()) {
      throw new Exception("ID do visitante não fornecido");
    }

    try {
      UUID id = UUID.fromString(idStr);
      customerRepository.delete(id);
    } catch (IllegalArgumentException e) {
      throw new Exception("ID do visitante inválido");
    } catch (Exception e) {
      throw new Exception("Erro ao excluir visitante: " + e.getMessage());
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
