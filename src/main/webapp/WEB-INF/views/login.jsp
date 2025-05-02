<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Login">
    <section class="auth-section">
        <div class="container">
            <div class="auth-container">
                <h1>Acesse sua Conta</h1>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error">
                        ${errorMessage}
                    </div>
                </c:if>

                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success">
                        ${successMessage}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/auth/login" method="post" class="auth-form">
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required>
                    </div>

                    <div class="form-group">
                        <label for="password">Senha</label>
                        <input type="password" id="password" name="password" required>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary btn-full">Entrar</button>
                    </div>

                    <div class="auth-links">
                        <p>Não tem uma conta? <a href="${pageContext.request.contextPath}/auth/register">Registre-se</a></p>
                    </div>
                </form>
            </div>
        </div>
    </section>
</t:master>