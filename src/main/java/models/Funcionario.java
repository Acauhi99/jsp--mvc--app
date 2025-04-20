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
public class Funcionario {
    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private Cargo cargo;
    
    public enum Cargo {
        VETERINARIO,
        TRATADOR,
        GUIA,
        ADMINISTRADOR,
        SEGURANCA,
        LIMPEZA,
        BILHETEIRO,
        BIÓLOGO
    }
}