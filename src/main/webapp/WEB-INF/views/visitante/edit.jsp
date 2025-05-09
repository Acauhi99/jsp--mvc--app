<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:master title="${empty visitante ? 'Cadastrar Novo Visitante' : 'Editar Visitante'}">
    <div class="form-container">
        <div class="form-header" style="text-align:center;">
            <span class="ticket-emoji">👤</span>
            <h1>${empty visitante ? 'Cadastrar Novo Visitante' : 'Editar Visitante'}</h1>
        </div>
        
        <c:if test="${not empty erro}">
            <div class="error-message">${erro}</div>
        </c:if>
        <c:if test="${not empty mensagem}">
            <div class="success-message">${mensagem}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}${empty visitante ? '/customer/novo' : '/customer/editar'}" method="post" class="auth-form">
            <c:if test="${not empty visitante}">
                <input type="hidden" name="id" value="${visitante.id}">
            </c:if>
            
            <div class="form-section">
                <div class="form-group">
                    <label for="nome">Nome Completo</label>
                    <input type="text" id="nome" name="nome" value="${visitante.nome}" required autofocus>
                </div>
                
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" value="${visitante.email}" required>
                </div>
                
                <div class="form-group">
                    <label for="password">Senha ${empty visitante ? '' : '(deixe em branco para manter a atual)'}</label>
                    <input type="password" id="password" name="password" ${empty visitante ? 'required' : ''}>
                </div>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-large btn-full">Salvar</button>
                <a href="${pageContext.request.contextPath}/customer" class="btn btn-outline btn-full" style="margin-top:1rem;">Cancelar</a>
            </div>
        </form>
    </div>
</t:master>