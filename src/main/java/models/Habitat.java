package models;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitat {
    private UUID id;
    private String nome;
    private TipoAmbiente tipoAmbiente;
    private double tamanho;
    private int capacidadeMaximaAnimais;
    @Builder.Default
    private List<Animal> animais = new ArrayList<>();
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