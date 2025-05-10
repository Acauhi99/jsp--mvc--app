<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard Visitante">
    <div class="container main-content">
        <h1 class="section-title">Dashboard do Visitante</h1>
        
        <div class="dashboard-welcome">
            <div class="dashboard-welcome-icon">🦒</div>
            <h2 class="dashboard-welcome-title">Bem-vindo, ${sessionScope.user.nome}!</h2>
            <p>Que bom ter você conosco! Explore as atrações do Zoo Park e aproveite sua visita.</p>
        </div>
        
        <div class="dashboard-section">
            <h2 class="section-subtitle">Atividades</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/ingresso/comprar" class="btn btn-primary btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🎟️</i>
                    <span>Comprar Ingresso</span>
                </a>
                <a href="${pageContext.request.contextPath}/ingresso" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🎫</i>
                    <span>Meus Ingressos</span>
                </a>
                <a href="${pageContext.request.contextPath}/animal/galeria" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🦁</i>
                    <span>Explorar Animais</span>
                </a>
                <a href="${pageContext.request.contextPath}/habitat" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🏕️</i>
                    <span>Conhecer Habitats</span>
                </a>
            </div>
        </div>
        
        <div class="dashboard-section">
            <h2 class="section-subtitle">Mapa e Informações</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/mapa" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🗺️</i>
                    <span>Ver Mapa do Zoo</span>
                </a>
                <a href="${pageContext.request.contextPath}/contact" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">⏰</i>
                    <span>Horários</span>
                </a>
            </div>
        </div>
    
        <div class="dashboard-info dashboard-info-success">
            <p><strong>Dica do dia:</strong></p>
            <p>Você sabia que o Zoo Park participa de projetos de conservação de espécies ameaçadas? Informe-se e ajude a preservar a natureza!</p>
        </div>
        
        <c:if test="${not empty promoAtiva}">
            <div class="alert alert-success dashboard-alert">
                <p><strong>Promoção Ativa:</strong> ${promoAtiva.descricao}</p>
                <p>Válida até: ${promoAtiva.dataFim}</p>
                <a href="${pageContext.request.contextPath}/promocao/${promoAtiva.id}" class="btn btn-primary" style="margin-top:0.5rem;">Ver Detalhes</a>
            </div>
        </c:if>
    </div>
</t:master>