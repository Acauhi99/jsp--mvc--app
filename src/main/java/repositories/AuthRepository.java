package repositories;

import dtos.auth.LoginRequest;
import dtos.auth.LoginResponse;
import dtos.auth.RegisterRequest;
import dtos.auth.RegisterResponse;
import models.Customer;
import models.Funcionario;
import utils.JwtUtils;
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
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(request.getEmail());
        
        if (optionalCustomer.isEmpty()) {
            return LoginResponse.builder()
                    .authenticated(false)
                    .message("Customer not found with this email")
                    .build();
        }
        
        Customer customer = optionalCustomer.get();
        
        if (!PasswordUtils.verifyPassword(request.getPassword(), customer.getPassword())) {
            return LoginResponse.builder()
                    .authenticated(false)
                    .message("Invalid password")
                    .build();
        }
        
        String token = JwtUtils.generateToken(customer.getId(), customer.getEmail(), "CUSTOMER");
        
        return LoginResponse.builder()
                .id(customer.getId())
                .nome(customer.getNome())
                .email(customer.getEmail())
                .role("CUSTOMER")
                .token(token)
                .authenticated(true)
                .message("Login successful")
                .build();
    }
    
    private LoginResponse authenticateFuncionario(LoginRequest request) {
        Optional<Funcionario> optionalFuncionario = funcionarioRepository.findByEmail(request.getEmail());
        
        if (optionalFuncionario.isEmpty()) {
            return LoginResponse.builder()
                    .authenticated(false)
                    .message("Funcionário not found with this email")
                    .build();
        }
        
        Funcionario funcionario = optionalFuncionario.get();
        
        if (!PasswordUtils.verifyPassword(request.getPassword(), funcionario.getPassword())) {
            return LoginResponse.builder()
                    .authenticated(false)
                    .message("Invalid password")
                    .build();
        }
        
        String role = funcionario.getCargo().toString();
        String token = JwtUtils.generateToken(funcionario.getId(), funcionario.getEmail(), role);
        
        return LoginResponse.builder()
                .id(funcionario.getId())
                .nome(funcionario.getNome())
                .email(funcionario.getEmail())
                .role(role)
                .token(token)
                .authenticated(true)
                .message("Login successful")
                .build();
    }
    
    private RegisterResponse registerCustomer(RegisterRequest request) {
        // Check if email already exists
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("Email already in use")
                    .build();
        }
        
        // Hash password
        String hashedPassword = PasswordUtils.hashPassword(request.getPassword());
        
        // Create new customer
        Customer customer = Customer.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .password(hashedPassword)
                .build();
        
        // Save customer
        Customer savedCustomer = customerRepository.save(customer);
        
        return RegisterResponse.builder()
                .id(savedCustomer.getId())
                .nome(savedCustomer.getNome())
                .email(savedCustomer.getEmail())
                .role("CUSTOMER")
                .success(true)
                .message("Customer registered successfully")
                .build();
    }
    
    private RegisterResponse registerFuncionario(RegisterRequest request) {
        // Check if email already exists
        if (funcionarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("Email already in use")
                    .build();
        }
        
        // Hash password
        String hashedPassword = PasswordUtils.hashPassword(request.getPassword());
        
        // Create new funcionario
        Funcionario funcionario = Funcionario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .password(hashedPassword)
                .cargo(request.getCargo())
                .build();
        
        // Save funcionario
        Funcionario savedFuncionario = funcionarioRepository.save(funcionario);
        
        return RegisterResponse.builder()
                .id(savedFuncionario.getId())
                .nome(savedFuncionario.getNome())
                .email(savedFuncionario.getEmail())
                .role(savedFuncionario.getCargo().toString())
                .success(true)
                .message("Funcionário registered successfully")
                .build();
    }
}
