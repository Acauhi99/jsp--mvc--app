package repositories;

import dtos.auth.LoginRequest;
import dtos.auth.LoginResponse;
import dtos.auth.RegisterRequest;
import dtos.auth.RegisterResponse;
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
        if (request.isFuncionario()) {
            return authenticateFuncionario(request);
        } else {
            return authenticateCustomer(request);
        }
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.isFuncionario()) {
            return registerFuncionario(request);
        } else {
            return registerCustomer(request);
        }
    }

    private LoginResponse authenticateCustomer(LoginRequest request) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(request.email());

        if (optionalCustomer.isEmpty()) {
            return LoginResponse.of(
                null, null, null, null, false,
                "Customer not found with this email"
            );
        }

        Customer customer = optionalCustomer.get();

        if (!PasswordUtils.verifyPassword(request.password(), customer.getPassword())) {
            return LoginResponse.of(
                null, null, null, null, false,
                "Invalid password"
            );
        }

        return LoginResponse.of(
            customer.getId(),
            customer.getNome(),
            customer.getEmail(),
            "CUSTOMER",
            true,
            "Login successful"
        );
    }

    private LoginResponse authenticateFuncionario(LoginRequest request) {
        Optional<Funcionario> optionalFuncionario = funcionarioRepository.findByEmail(request.email());

        if (optionalFuncionario.isEmpty()) {
            return LoginResponse.of(
                null, null, null, null, false,
                "Funcionário not found with this email"
            );
        }

        Funcionario funcionario = optionalFuncionario.get();

        if (!PasswordUtils.verifyPassword(request.password(), funcionario.getPassword())) {
            return LoginResponse.of(
                null, null, null, null, false,
                "Invalid password"
            );
        }

        String role = funcionario.getCargo().toString();

        return LoginResponse.of(
            funcionario.getId(),
            funcionario.getNome(),
            funcionario.getEmail(),
            role,
            true,
            "Login successful"
        );
    }

    private RegisterResponse registerCustomer(RegisterRequest request) {
        if (customerRepository.findByEmail(request.email()).isPresent()) {
            return RegisterResponse.of(
                null, null, null, null, false,
                "Email already in use"
            );
        }

        String hashedPassword = PasswordUtils.hashPassword(request.password());

        Customer customer = Customer.create(
            request.nome(),
            request.email(),
            hashedPassword
        );

        Customer savedCustomer = customerRepository.save(customer);

        return RegisterResponse.of(
            savedCustomer.getId(),
            savedCustomer.getNome(),
            savedCustomer.getEmail(),
            "CUSTOMER",
            true,
            "Customer registered successfully"
        );
    }

    private RegisterResponse registerFuncionario(RegisterRequest request) {
        if (funcionarioRepository.findByEmail(request.email()).isPresent()) {
            return RegisterResponse.of(
                null, null, null, null, false,
                "Email already in use"
            );
        }

        String hashedPassword = PasswordUtils.hashPassword(request.password());

        Funcionario funcionario = Funcionario.create(
            request.nome(),
            request.email(),
            hashedPassword,
            request.cargo()
        );

        Funcionario savedFuncionario = funcionarioRepository.save(funcionario);

        return RegisterResponse.of(
            savedFuncionario.getId(),
            savedFuncionario.getNome(),
            savedFuncionario.getEmail(),
            savedFuncionario.getCargo().toString(),
            true,
            "Funcionário registered successfully"
        );
    }
}
