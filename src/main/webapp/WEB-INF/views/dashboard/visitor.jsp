<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard Visitante">
    <div class="container main-content">
        <h1 class="section-title">Dashboard do Visitante</h1>
        
        <div class="dashboard-welcome" style="text-align:center; margin-bottom:2rem; background:#f9f9f9; padding:1.5rem; border-radius:8px; box-shadow:var(--box-shadow);">
            <div style="font-size:3rem; margin-bottom:0.5rem;">🦒</div>
            <h2 style="color:var(--primary-color); margin-bottom:0.5rem;">Bem-vindo, ${sessionScope.user.nome}!</h2>
            <p>Que bom ter você conosco! Explore as atrações do Zoo Park e aproveite sua visita.</p>
        </div>
        
        <div class="dashboard-section">
            <h2 class="section-subtitle">Atividades</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/ingresso/comprar" class="btn btn-primary btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🎟️</i>
                    <span>Comprar Ingresso</span>
                </a>
                <a href="${pageContext.request.contextPath}/ingresso" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🎫</i>
                    <span>Meus Ingressos</span>
                </a>
                <a href="${pageContext.request.contextPath}/animal/galeria" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🦁</i>
                    <span>Explorar Animais</span>
                </a>
                <a href="${pageContext.request.contextPath}/habitat" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🏕️</i>
                    <span>Conhecer Habitats</span>
                </a>
            </div>
        </div>
        
        <div class="dashboard-section">
            <h2 class="section-subtitle">Mapa e Informações</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/mapa" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🗺️</i>
                    <span>Ver Mapa do Zoo</span>
                </a>
                <a href="${pageContext.request.contextPath}/contact" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">⏰</i>
                    <span>Horários</span>
                </a>
            </div>
        </div>
    
        <div class="dashboard-info" style="margin:2rem auto; max-width:800px; text-align:center; background:#e3f4eb; padding:1.2rem; border-radius:8px;">
            <p style="margin-bottom:0.5rem;"><strong>Dica do dia:</strong></p>
            <p>Você sabia que o Zoo Park participa de projetos de conservação de espécies ameaçadas? Informe-se e ajude a preservar a natureza!</p>
        </div>
        
        <c:if test="${not empty promoAtiva}">
            <div class="alert alert-success" style="margin:1rem auto; max-width:800px; text-align:center;">
                <p><strong>Promoção Ativa:</strong> ${promoAtiva.descricao}</p>
                <p>Válida até: ${promoAtiva.dataFim}</p>
                <a href="${pageContext.request.contextPath}/promocao/${promoAtiva.id}" class="btn btn-primary" style="margin-top:0.5rem;">Ver Detalhes</a>
            </div>
        </c:if>
    </div>
</t:master>