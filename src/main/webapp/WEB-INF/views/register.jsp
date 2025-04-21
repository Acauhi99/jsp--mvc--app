<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Registro">
    <section class="auth-section">
        <div class="container">
            <div class="auth-container">
                <h1>Crie sua Conta</h1>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error">
                        ${errorMessage}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/api/auth/register" method="post" class="auth-form">
                    <div class="form-group">
                        <label for="nome">Nome Completo</label>
                        <input type="text" id="nome" name="nome" required>
                    </div>

                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required>
                    </div>

                    <div class="form-group">
                        <label for="password">Senha</label>
                        <input type="password" id="password" name="password" required>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirmar Senha</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required>
                    </div>

                    <div class="form-group">
                        <div class="checkbox-group">
                            <input type="checkbox" id="isFuncionario" name="isFuncionario" value="true">
                            <label for="isFuncionario">Registrar como Funcionário</label>
                        </div>
                    </div>

                    <div class="staff-fields" style="display: none;">
                        <div class="form-group">
                            <label for="cargo">Cargo</label>
                            <select id="cargo" name="cargo">
                                <option value="VETERINARIO">Veterinário</option>
                                <option value="TRATADOR">Tratador</option>
                                <option value="GUIA">Guia</option>
                                <option value="ADMINISTRADOR">Administrador</option>
                                <option value="SEGURANCA">Segurança</option>
                                <option value="LIMPEZA">Limpeza</option>
                                <option value="BILHETEIRO">Bilheteiro</option>
                                <option value="BIÓLOGO">Biólogo</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary btn-full">Registrar</button>
                    </div>

                    <div class="auth-links">
                        <p>Já tem uma conta? <a href="${pageContext.request.contextPath}/api/auth/login">Login</a></p>
                    </div>
                </form>
            </div>
        </div>
    </section>
</t:master>