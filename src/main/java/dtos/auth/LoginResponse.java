package dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private UUID id;
    private String nome;
    private String email;
    private String role; 
    private String token;
    private boolean authenticated;
    private String message;
}
