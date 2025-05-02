package dtos.auth;

import models.Funcionario.Cargo;

public class RegisterRequest {
    private String nome;
    private String email;
    private String password;
    private boolean isFuncionario;
    private String cargoStr;
    
    public RegisterRequest() {}
    
    public RegisterRequest(String nome, String email, String password, boolean isFuncionario, String cargoStr) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.isFuncionario = isFuncionario;
        this.cargoStr = cargoStr;
    }
    
    // Getters & Setters
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean isFuncionario() { return isFuncionario; }
    public String getCargoStr() { return cargoStr; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFuncionario(boolean isFuncionario) { this.isFuncionario = isFuncionario; }
    public void setCargoStr(String cargoStr) { this.cargoStr = cargoStr; }
    
    public Cargo getCargo() {
        if (cargoStr == null || cargoStr.isEmpty()) {
            return null;
        }
        try {
            return Cargo.valueOf(cargoStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
