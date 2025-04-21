package dtos.auth;

import java.util.UUID;

public record RegisterResponse(
    UUID id,
    String nome,
    String email,
    String role,
    boolean success,
    String message
) {
    public static RegisterResponse of(UUID id, String nome, String email, 
                                     String role, boolean success, String message) {
        return new RegisterResponse(id, nome, email, role, success, message);
    }
}
