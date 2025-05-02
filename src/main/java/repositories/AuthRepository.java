package repositories;

import dtos.auth.LoginRequest;
import dtos.auth.LoginResponse;
import dtos.auth.RegisterRequest;
import dtos.auth.RegisterResponse;
import dtos.auth.UserDetails;
import models.Customer;
import models.Funcionario;
import utils.PasswordUtils;

import java.util.Optional;

public class AuthRepository {

    private final CustomerRepository customerRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AuthRepository() {
        this.customerRepository = new CustomerRepository();
        this.funcionarioRepository = new FuncionarioRepository();
    }

    public LoginResponse login(LoginRequest request) {
        Optional<Funcionario> optionalFuncionario = funcionarioRepository.findByEmail(request.email());
        
        if (optionalFuncionario.isPresent()) {
            Funcionario funcionario = optionalFuncionario.get();
            
            if (!PasswordUtils.verifyPassword(request.password(), funcionario.getPassword())) {
                return LoginResponse.failure("Senha inválida");
            }
            
            UserDetails userDetails = UserDetails.fromFuncionario(funcionario);
            return LoginResponse.success(userDetails, "Login realizado com sucesso");
        }
        
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(request.email());
        
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            
            if (!PasswordUtils.verifyPassword(request.password(), customer.getPassword())) {
                return LoginResponse.failure("Senha inválida");
            }
            
            UserDetails userDetails = UserDetails.fromCustomer(customer);
            return LoginResponse.success(userDetails, "Login realizado com sucesso");
        }
        
        return LoginResponse.failure("Usuário não encontrado com este email");
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.isFuncionario()) {
            return registerFuncionario(request);
        } else {
            return registerCustomer(request);
        }
    }

    private RegisterResponse registerCustomer(RegisterRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            return RegisterResponse.of(
                null, null, null, null, false,
                "Email já está em uso"
            );
        }

        String hashedPassword = PasswordUtils.hashPassword(request.getPassword());

        Customer customer = Customer.create(
            request.getNome(),
            request.getEmail(),
            hashedPassword
        );

        Customer savedCustomer = customerRepository.save(customer);

        return RegisterResponse.of(
            savedCustomer.getId(),
            savedCustomer.getNome(),
            savedCustomer.getEmail(),
            "VISITANTE",
            true,
            "Cliente registrado com sucesso"
        );
    }

    private RegisterResponse registerFuncionario(RegisterRequest request) {
        if (funcionarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return RegisterResponse.of(
                null, null, null, null, false,
                "Email já está em uso"
            );
        }

        String hashedPassword = PasswordUtils.hashPassword(request.getPassword());
        Funcionario.Cargo cargo = request.getCargo();
        
        if (cargo == null) {
            return RegisterResponse.of(
                null, null, null, null, false,
                "Cargo inválido selecionado"
            );
        }

        Funcionario funcionario = Funcionario.create(
            request.getNome(),
            request.getEmail(),
            hashedPassword,
            cargo
        );

        Funcionario savedFuncionario = funcionarioRepository.save(funcionario);

        return RegisterResponse.of(
            savedFuncionario.getId(),
            savedFuncionario.getNome(),
            savedFuncionario.getEmail(),
            savedFuncionario.getCargo().toString(),
            true,
            "Funcionário registrado com sucesso"
        );
    }
}
