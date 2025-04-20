package dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Funcionario.Cargo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String nome;
    private String email;
    private String password;
    private boolean isFuncionario;
    private Cargo cargo;
}
