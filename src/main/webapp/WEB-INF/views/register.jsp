<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Register">
    <section class="auth-section">
        <div class="container">
            <div class="auth-container">
                <h1>Create an Account</h1>
                
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error">
                        ${errorMessage}
                    </div>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/api/auth/register" method="post" class="auth-form">
                    <div class="form-group">
                        <label for="nome">Full Name</label>
                        <input type="text" id="nome" name="nome" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required>
                    </div>
                    
                    <div class="form-group">
                        <div class="checkbox-group">
                            <input type="checkbox" id="isFuncionario" name="isFuncionario" value="true">
                            <label for="isFuncionario">Register as Staff Member</label>
                        </div>
                    </div>
                    
                    <div class="staff-fields" style="display: none;">
                        <div class="form-group">
                            <label for="cargo">Position</label>
                            <select id="cargo" name="cargo">
                                <option value="VETERINARIO">Veterinarian</option>
                                <option value="TRATADOR">Animal Keeper</option>
                                <option value="GUIA">Guide</option>
                                <option value="ADMINISTRADOR">Administrator</option>
                                <option value="SEGURANCA">Security</option>
                                <option value="LIMPEZA">Cleaning Staff</option>
                                <option value="BILHETEIRO">Ticket Seller</option>
                                <option value="BIÓLOGO">Biologist</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary btn-full">Register</button>
                    </div>
                    
                    <div class="auth-links">
                        <p>Already have an account? <a href="${pageContext.request.contextPath}/api/auth/login">Login</a></p>
                    </div>
                </form>
            </div>
        </div>
    </section>
</t:master>