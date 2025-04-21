package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @OneToMany(mappedBy = "comprador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingresso> ingressosAdquiridos = new ArrayList<>();
    
    // Constructors
    public Customer() {}
    
    public Customer(String nome, String email, String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
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
    
    public List<Ingresso> getIngressosAdquiridos() { return ingressosAdquiridos; }
    public void setIngressosAdquiridos(List<Ingresso> ingressos) { this.ingressosAdquiridos = ingressos; }
    
    // Factory method 
    public static Customer create(String nome, String email, String password) {
        Customer customer = new Customer();
        customer.nome = nome;
        customer.email = email;
        customer.password = password;
        return customer;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
