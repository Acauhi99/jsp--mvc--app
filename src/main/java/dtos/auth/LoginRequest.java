package dtos.auth;

public record LoginRequest(
    String email,
    String password,
    boolean isFuncionario
) {}
