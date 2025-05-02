<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard Administrador">
    <div class="container main-content">
        <h1 class="section-title">Bem-vindo à Dashboard de Administrador</h1>
        
        <div class="admin-dashboard">
            <p>Olá, ${sessionScope.user.nome}! Você está logado como Administrador.</p>
            
            <div class="dashboard-sections">
                <h2>Acesso rápido</h2>
                <ul class="dashboard-links">
                    <li><a href="${pageContext.request.contextPath}/animal" class="btn btn-primary">Gerenciar Animais</a></li>
                    <li><a href="${pageContext.request.contextPath}/habitat" class="btn btn-primary">Gerenciar Habitats</a></li>
                    <li><a href="${pageContext.request.contextPath}/alimentacao" class="btn btn-primary">Registros de Alimentação</a></li>
                    <li><a href="${pageContext.request.contextPath}/consulta" class="btn btn-primary">Consultas Veterinárias</a></li>
                    <li><a href="${pageContext.request.contextPath}/manutencao" class="btn btn-primary">Manutenção de Habitats</a></li>
                </ul>
            </div>
        </div>
    </div>
</t:master>