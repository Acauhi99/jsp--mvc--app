package models;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "animais")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Animal {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String especie;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Classe classe;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status_saude")
    private StatusSaude statusSaude;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitat_id")
    private Habitat habitat;
    
    public enum Classe {
        MAMIFERO,
        REPTIL,
        AVE,
        PEIXE,
        ANFIBIO,
        INSETO,
        ARACNIDEO
    }
    
    public enum Genero {
        MASCULINO,
        FEMININO
    }
    
    public enum StatusSaude {
        SAUDAVEL,
        EM_TRATAMENTO
    }
}