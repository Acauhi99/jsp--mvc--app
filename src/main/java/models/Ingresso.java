package models;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ingressos")
public class Ingresso {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIngresso tipo;
    
    @Column(nullable = false)
    private Double valor;
    
    @Column
    private boolean utilizado;
    
    @Column(name = "data_compra")
    private LocalDateTime dataCompra;
    
    @ManyToOne
    @JoinColumn(name = "comprador_id")
    private Customer comprador;
    
    // Constructors
    public Ingresso() {}
    
    public Ingresso(TipoIngresso tipo, Double valor, boolean utilizado, 
                  LocalDateTime dataCompra, Customer comprador) {
        this.tipo = tipo;
        this.valor = valor;
        this.utilizado = utilizado;
        this.dataCompra = dataCompra;
        this.comprador = comprador;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public TipoIngresso getTipo() { return tipo; }
    public void setTipo(TipoIngresso tipo) { this.tipo = tipo; }
    
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    
    public boolean isUtilizado() { return utilizado; }
    public void setUtilizado(boolean utilizado) { this.utilizado = utilizado; }
    
    public LocalDateTime getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }
    
    public Customer getComprador() { return comprador; }
    public void setComprador(Customer comprador) { this.comprador = comprador; }
    
    // Factory method 
    public static Ingresso create(TipoIngresso tipo, Double valor, Customer comprador) {
        Ingresso ingresso = new Ingresso();
        ingresso.tipo = tipo;
        ingresso.valor = valor;
        ingresso.utilizado = false;
        ingresso.dataCompra = LocalDateTime.now();
        ingresso.comprador = comprador;
        return ingresso;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingresso ingresso = (Ingresso) o;
        return Objects.equals(id, ingresso.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Ingresso{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", valor=" + valor +
                ", utilizado=" + utilizado +
                ", dataCompra=" + dataCompra +
                '}';
    }
    
    public enum TipoIngresso {
        ADULTO,
        CRIANCA,
        IDOSO,
        ESTUDANTE,
        DEFICIENTE
    }
}