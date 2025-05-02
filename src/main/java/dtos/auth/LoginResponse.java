package dtos.auth;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private UserDetails userDetails;
    private boolean authenticated;
    private String message;
    
    public LoginResponse() {}
    
    public LoginResponse(UserDetails userDetails, boolean authenticated, String message) {
        this.userDetails = userDetails;
        this.authenticated = authenticated;
        this.message = message;
    }
    
    // Factory methods
    public static LoginResponse success(UserDetails userDetails, String message) {
        return new LoginResponse(userDetails, true, message);
    }
    
    public static LoginResponse failure(String message) {
        return new LoginResponse(null, false, message);
    }
    
    // Getters e setters
    public UserDetails getUserDetails() { return userDetails; }
    public void setUserDetails(UserDetails userDetails) { this.userDetails = userDetails; }
    
    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getNome() {
        return userDetails != null ? userDetails.getNome() : null;
    }
    
    public String getEmail() {
        return userDetails != null ? userDetails.getEmail() : null;
    }
    
    public String getRole() {
        return userDetails != null ? userDetails.getRole() : null;
    }
}