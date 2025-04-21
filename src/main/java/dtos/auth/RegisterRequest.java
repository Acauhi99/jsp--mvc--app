package dtos.auth;

import models.Funcionario.Cargo;

public record RegisterRequest(
    String nome,
    String email,
    String password,
    boolean isFuncionario,
    Cargo cargo
) {}
