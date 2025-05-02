package dtos.auth;

import java.io.Serializable;
import java.util.UUID;

public class UserDetails implements Serializable {
    public static final String TYPE_CUSTOMER = "VISITANTE";
    public static final String TYPE_FUNCIONARIO = "FUNCIONARIO";
    public static final String ROLE_VISITOR = "VISITANTE";

    private UUID id;
    private String nome;
    private String email;
    private String role;
    private String userType;
    
    public UserDetails() {}
    
    public UserDetails(UUID id, String nome, String email, String role, String userType) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.userType = userType;
    }
    
    public static UserDetails fromCustomer(models.Customer customer) {
        return new UserDetails(
            customer.getId(),
            customer.getNome(),
            customer.getEmail(),
            ROLE_VISITOR,
            TYPE_CUSTOMER
        );
    }
    
    public static UserDetails fromFuncionario(models.Funcionario funcionario) {
        return new UserDetails(
            funcionario.getId(),
            funcionario.getNome(),
            funcionario.getEmail(),
            funcionario.getCargo().toString(),
            TYPE_FUNCIONARIO
        );
    }
    
    // Getters e setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    // Helper methods
    public boolean isCustomer() {
        return TYPE_CUSTOMER.equals(userType);
    }
    
    public boolean isFuncionario() {
        return TYPE_FUNCIONARIO.equals(userType);
    }
    
    public boolean isAdmin() {
        return "ADMINISTRADOR".equals(role);
    }
}