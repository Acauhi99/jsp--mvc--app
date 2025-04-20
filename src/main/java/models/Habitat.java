package models;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "habitats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitat {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(nullable = false)
    private String nome;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ambiente", nullable = false)
    private TipoAmbiente tipoAmbiente;
    
    @Column(nullable = false)
    private double tamanho;
    
    @Column(name = "capacidade_maxima")
    private int capacidadeMaximaAnimais;
    
    @OneToMany(mappedBy = "habitat", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Animal> animais = new ArrayList<>();
    
    @Column(name = "publico_acessivel")
    private boolean publicoAcessivel;
    
    public enum TipoAmbiente {
        AQUATICO,
        SAVANA,
        FLORESTA,
        DESERTO,
        TUNDRA,
        MONTANHA,
        PANTANAL
    }
}