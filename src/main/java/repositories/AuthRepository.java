package repositories;

import models.Customer;
import models.Funcionario;
import utils.PasswordUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AuthRepository {

    private final CustomerRepository customerRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AuthRepository() {
        this.customerRepository = new CustomerRepository();
        this.funcionarioRepository = new FuncionarioRepository();
    }

    public Map<String, Object> login(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", false);

        if (email == null || password == null) {
            result.put("message", "Email e senha são obrigatórios");
            return result;
        }

        Optional<Funcionario> optionalFuncionario = funcionarioRepository.findByEmail(email);

        if (optionalFuncionario.isPresent()) {
            Funcionario funcionario = optionalFuncionario.get();

            if (!PasswordUtils.verifyPassword(password, funcionario.getPassword())) {
                result.put("message", "Senha inválida");
                return result;
            }

            result.put("authenticated", true);
            result.put("user", funcionario);
            result.put("userId", funcionario.getId());
            result.put("nome", funcionario.getNome());
            result.put("email", funcionario.getEmail());
            result.put("role", funcionario.getCargo().toString());
            result.put("message", "Login realizado com sucesso");

            return result;
        }

        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();

            if (!PasswordUtils.verifyPassword(password, customer.getPassword())) {
                result.put("message", "Senha inválida");
                return result;
            }

            result.put("authenticated", true);
            result.put("user", customer);
            result.put("userId", customer.getId());
            result.put("nome", customer.getNome());
            result.put("email", customer.getEmail());
            result.put("role", "VISITANTE");
            result.put("message", "Login realizado com sucesso");

            return result;
        }

        result.put("message", "Usuário não encontrado com este email");
        return result;
    }

    public Map<String, Object> registerCustomer(String nome, String email, String password) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);

        if (customerRepository.findByEmail(email).isPresent()) {
            result.put("message", "Email já está em uso");
            return result;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);

        Customer customer = Customer.create(nome, email, hashedPassword);
        Customer savedCustomer = customerRepository.save(customer);

        result.put("success", true);
        result.put("user", savedCustomer);
        result.put("userId", savedCustomer.getId());
        result.put("nome", savedCustomer.getNome());
        result.put("email", savedCustomer.getEmail());
        result.put("role", "VISITANTE");
        result.put("message", "Cliente registrado com sucesso");

        return result;
    }

    public Map<String, Object> registerFuncionario(String nome, String email, String password, String cargoStr) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);

        if (funcionarioRepository.findByEmail(email).isPresent()) {
            result.put("message", "Email já está em uso");
            return result;
        }

        Funcionario.Cargo cargo;
        try {
            cargo = Funcionario.Cargo.valueOf(cargoStr);
        } catch (IllegalArgumentException e) {
            result.put("message", "Cargo inválido selecionado");
            return result;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        Funcionario funcionario = Funcionario.create(nome, email, hashedPassword, cargo);
        Funcionario savedFuncionario = funcionarioRepository.save(funcionario);

        result.put("success", true);
        result.put("user", savedFuncionario);
        result.put("userId", savedFuncionario.getId());
        result.put("nome", savedFuncionario.getNome());
        result.put("email", savedFuncionario.getEmail());
        result.put("role", savedFuncionario.getCargo().toString());
        result.put("message", "Funcionário registrado com sucesso");

        return result;
    }

    public Customer getCustomerById(UUID id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Funcionario getFuncionarioById(UUID id) {
        return funcionarioRepository.findById(id).orElse(null);
    }
}
