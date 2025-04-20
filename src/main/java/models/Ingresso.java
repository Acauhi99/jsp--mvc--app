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
public class Ingresso {
    private UUID id;
    private TipoIngresso tipo;
    private Double valor;
    private boolean utilizado;
    private Customer comprador;
    
    public enum TipoIngresso {
        ADULTO,
        CRIANCA,
        IDOSO,
        ESTUDANTE,
        DEFICIENTE
    }
}