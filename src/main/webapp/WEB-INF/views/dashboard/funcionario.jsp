<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard ${sessionScope.user.role}">
    <div class="container main-content">
        <h1 class="section-title">Bem-vindo, ${sessionScope.user.nome}!</h1>
        
        <div class="funcionario-dashboard">
            <div class="dashboard-welcome">
                <c:choose>
                    <c:when test="${sessionScope.user.role eq 'MANUTENCAO'}">
                        <p>Gerencie as solicitações de manutenção dos habitats e instalações do zoo.</p>
                    </c:when>
                    <c:when test="${sessionScope.user.role eq 'VETERINARIO'}">
                        <p>Acompanhe as consultas e tratamentos de saúde dos animais do zoo.</p>
                    </c:when>
                    <c:otherwise>
                        <p>Acesse as informações e recursos disponíveis para funcionários do Zoo Park.</p>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="dashboard-quick-actions">
                <c:choose>
                    <c:when test="${sessionScope.user.role eq 'MANUTENCAO'}">
                        <a href="${pageContext.request.contextPath}/manutencao/novo" class="btn btn-primary btn-large">Nova Solicitação</a>
                        <a href="${pageContext.request.contextPath}/manutencao" class="btn btn-outline btn-large">Todas Solicitações</a>
                        <a href="${pageContext.request.contextPath}/habitat" class="btn btn-outline btn-large">Ver Habitats</a>
                    </c:when>
                </c:choose>
            </div>
            
            <c:if test="${not empty tarefasAtribuidas}">
                <div class="dashboard-section">
                    <h2 class="section-subtitle">Suas Tarefas Designadas</h2>
                </div>
            </c:if>
            
            <c:if test="${not empty proximas}">
                <div class="dashboard-section">
                    <h2 class="section-subtitle">Próximas Manutenções Programadas</h2>
                </div>
            </c:if>
            
            <div class="dashboard-info">
                <strong>Dica de Manutenção:</strong> 
                <c:choose>
                    <c:when test="${sessionScope.user.role eq 'MANUTENCAO'}">
                        Mantenha o registro das manutenções sempre atualizado.
                    </c:when>
                    <c:when test="${sessionScope.user.role eq 'VETERINARIO'}">
                        Sempre verifique o histórico de saúde dos animais antes de iniciar um novo tratamento.
                    </c:when>
                </c:choose>
            </div>
            
            <c:if test="${not empty alertas}">
                <div class="alert alert-error dashboard-alert">
                    <ul>
                        <c:forEach var="alerta" items="${alertas}">
                            <li>${alerta}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
        </div>
    </div>
</t:master>