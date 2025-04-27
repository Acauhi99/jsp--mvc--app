package models;

import java.util.UUID;
import java.util.Objects;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "funcionarios")
public class Funcionario {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cargo cargo;
    
    // Constructors
    public Funcionario() {}
    
    public Funcionario(String nome, String email, String password, Cargo cargo) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.cargo = cargo;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }
    
    // Factory method 
    public static Funcionario create(String nome, String email, String password, Cargo cargo) {
        Funcionario funcionario = new Funcionario();
        funcionario.nome = nome;
        funcionario.email = email;
        funcionario.password = password;
        funcionario.cargo = cargo;
        return funcionario;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Funcionario that = (Funcionario) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Funcionario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", cargo=" + cargo +
                '}';
    }
    
    public enum Cargo {
        ADMINISTRADOR,  
        VETERINARIO,    
        TRATADOR,       
        MANUTENCAO,    
    }
}