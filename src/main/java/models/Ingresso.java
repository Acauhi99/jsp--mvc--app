package models;

import java.util.UUID;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ingressos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    
    public enum TipoIngresso {
        ADULTO,
        CRIANCA,
        IDOSO,
        ESTUDANTE,
        DEFICIENTE
    }
}