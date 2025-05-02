<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard Visitante">
    <div class="container main-content">
        <h1 class="section-title">Bem-vindo à Dashboard de Visitante</h1>
        
        <div class="visitor-dashboard">
            <p>Olá, ${sessionScope.user.nome}! Bem-vindo ao Zoo Park.</p>
            
            <div class="dashboard-sections">
                <h2>Opções para Visitantes</h2>
                <ul class="dashboard-links">
                    <li><a href="${pageContext.request.contextPath}/tickets" class="btn btn-primary">Comprar Ingressos</a></li>
                    <li><a href="${pageContext.request.contextPath}/animal" class="btn btn-primary">Explorar Animais</a></li>
                    <li><a href="${pageContext.request.contextPath}/habitat" class="btn btn-primary">Conhecer Habitats</a></li>
                </ul>
            </div>
            
            <div class="user-tickets">
                <h2>Meus Ingressos</h2>
                <p>Você ainda não possui ingressos. Compre agora para visitar o zoológico!</p>
            </div>
        </div>
    </div>
</t:master>