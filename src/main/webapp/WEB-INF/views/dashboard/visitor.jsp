<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard Visitante">
    <div class="container main-content">
        <h1 class="section-title">Bem-vindo, ${sessionScope.user.nome}!</h1>
        
        <div class="visitor-dashboard">
            <div class="dashboard-welcome" style="margin-bottom:2rem;">
                <p>Que bom ter você conosco! Explore as atrações do Zoo Park e aproveite sua visita.</p>
            </div>
            
            <div class="dashboard-quick-actions" style="display:flex;gap:1.5rem;flex-wrap:wrap;margin-bottom:2.5rem;">
                <a href="${pageContext.request.contextPath}/ingresso/comprar" class="btn btn-primary btn-large">Comprar Ingresso</a>
                <a href="${pageContext.request.contextPath}/ingresso" class="btn btn-outline btn-large">Meus Ingressos</a>
                <a href="${pageContext.request.contextPath}/animal" class="btn btn-outline btn-large">Explorar Animais</a>
                <a href="${pageContext.request.contextPath}/habitat" class="btn btn-outline btn-large">Conhecer Habitats</a>
                <a href="${pageContext.request.contextPath}/mapa" class="btn btn-outline btn-large">Ver Mapa do Zoo</a>
            </div>

            <div class="visitor-tip" style="background:#e3f4eb;padding:1.2rem;border-radius:8px;">
                <strong>Dica do dia:</strong> Você sabia que o Zoo Park participa de projetos de conservação de espécies ameaçadas? Informe-se e ajude a preservar a natureza!
            </div>
        </div>
    </div>
</t:master>