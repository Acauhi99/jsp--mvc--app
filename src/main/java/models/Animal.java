package models;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Animal {
    private UUID id;
    private String nome;
    private String especie;
    private Classe classe;
    private Genero genero;
    private StatusSaude statusSaude;
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