<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard Administrador">
    <div class="container main-content">
        <h1 class="section-title">Dashboard do Administrador</h1>
        
        <div class="dashboard-welcome">
            <div class="dashboard-welcome-icon">👑</div>
            <h2 class="dashboard-welcome-title">Bem-vindo, ${sessionScope.user.nome}!</h2>
            <p>Você tem acesso completo às funcionalidades de administração do Zoo Park.</p>
        </div>
        
        <div class="dashboard-section">
            <h2 class="section-subtitle">Cadastros</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/animal" class="btn btn-primary btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🦁</i>
                    <span>Animais</span>
                </a>
                <a href="${pageContext.request.contextPath}/habitat" class="btn btn-primary btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🏕️</i>
                    <span>Habitats</span>
                </a>
                <a href="${pageContext.request.contextPath}/funcionario/veterinario" class="btn btn-primary btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">👨‍⚕️</i>
                    <span>Veterinários</span>
                </a>
            </div>
        </div>
    
        <div class="dashboard-section">
            <h2 class="section-subtitle">Operações</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/alimentacao" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🍽️</i>
                    <span>Alimentação</span>
                </a>
                <a href="${pageContext.request.contextPath}/consulta" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🩺</i>
                    <span>Consultas</span>
                </a>
                <a href="${pageContext.request.contextPath}/consulta/historico" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">📋</i>
                    <span>Histórico Médico</span>
                </a>
                <a href="${pageContext.request.contextPath}/manutencao" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🔧</i>
                    <span>Manutenção</span>
                </a>
            </div>
        </div>
   
        <div class="dashboard-section">
            <h2 class="section-subtitle">Visitantes</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/ingresso/admin" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">🎟️</i>
                    <span>Ingressos</span>
                </a>
                <a href="${pageContext.request.contextPath}/customer" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">👥</i>
                    <span>Visitantes</span>
                </a>
            </div>
        </div>
 
        <div class="dashboard-section">
            <h2 class="section-subtitle">Relatórios</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/relatorio/consultas" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">📉</i>
                    <span>Consultas Realizadas</span>
                </a>
                <a href="${pageContext.request.contextPath}/relatorio/vendas" class="btn btn-outline btn-large dashboard-action-btn">
                    <i class="menu-icon dashboard-action-icon">💰</i>
                    <span>Vendas de Ingressos</span>
                </a>
            </div>
        </div>
        
        <div class="dashboard-info dashboard-info-default">
            <p><strong>Dica de Administrador:</strong></p>
            <p>Monitore regularmente os relatórios de venda e ocupação para otimizar a distribuição de recursos no Zoo Park.</p>
        </div>
        
        <c:if test="${not empty alertas}">
            <div class="alert alert-error dashboard-alert">
                <p><strong>Alertas do Sistema:</strong></p>
                <ul>
                    <c:forEach var="alerta" items="${alertas}">
                        <li>${alerta}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
    </div>
</t:master>