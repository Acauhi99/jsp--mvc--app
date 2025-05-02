<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard Funcionário">
    <div class="container main-content">
        <h1 class="section-title">Bem-vindo à Dashboard de Funcionário</h1>
        
        <div class="funcionario-dashboard">
            <p>Olá, ${sessionScope.user.nome}! Você está logado como ${sessionScope.user.role}.</p>
            
            <div class="dashboard-sections">
                <h2>Acesso rápido</h2>
                <ul class="dashboard-links">
                    <c:if test="${sessionScope.user.role eq 'VETERINARIO'}">
                        <li><a href="${pageContext.request.contextPath}/consulta" class="btn btn-primary">Consultas Veterinárias</a></li>
                    </c:if>
                    
                    <c:if test="${sessionScope.user.role eq 'TRATADOR'}">
                        <li><a href="${pageContext.request.contextPath}/alimentacao" class="btn btn-primary">Registrar Alimentação</a></li>
                    </c:if>
                    
                    <c:if test="${sessionScope.user.role eq 'MANUTENCAO'}">
                        <li><a href="${pageContext.request.contextPath}/manutencao" class="btn btn-primary">Manutenção de Habitats</a></li>
                    </c:if>
                    
                    <li><a href="${pageContext.request.contextPath}/animal" class="btn btn-primary">Visualizar Animais</a></li>
                </ul>
            </div>
        </div>
    </div>
</t:master>