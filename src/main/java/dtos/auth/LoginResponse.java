package dtos.auth;

import java.util.UUID;

public record LoginResponse(
    UUID id,
    String nome,
    String email,
    String role,
    boolean authenticated,
    String message
) {
    public static LoginResponse of(UUID id, String nome, String email, String role,
                                  boolean authenticated, String message) {
        return new LoginResponse(id, nome, email, role, authenticated, message);
    }
}
