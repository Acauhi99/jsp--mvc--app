<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard ${sessionScope.role}">
    <div class="container main-content">
        <h1 class="section-title">Dashboard do ${sessionScope.role}</h1>
        
        <div class="dashboard-welcome">
            <div class="dashboard-welcome-icon">👨‍💼</div>
            <h2 class="dashboard-welcome-title">Bem-vindo, ${sessionScope.userName}!</h2>
            <c:choose>
                <c:when test="${sessionScope.role eq 'MANUTENCAO'}">
                    <p>Gerencie as solicitações de manutenção dos habitats e instalações do zoo.</p>
                </c:when>
                <c:when test="${sessionScope.role eq 'VETERINARIO'}">
                    <p>Acompanhe as consultas e tratamentos de saúde dos animais do zoo.</p>
                </c:when>
                <c:otherwise>
                    <p>Acesse as informações e recursos disponíveis para funcionários do Zoo Park.</p>
                </c:otherwise>
            </c:choose>
        </div>
        
        <div class="dashboard-section">
            <h2 class="section-subtitle">Acesso Rápido</h2>
            <div class="dashboard-quick-actions">
                <c:choose>
                    <c:when test="${sessionScope.role eq 'MANUTENCAO'}">
                        <a href="${pageContext.request.contextPath}/manutencao/novo" class="btn btn-primary btn-large dashboard-action-btn">
                            <i class="menu-icon dashboard-action-icon">📝</i>
                            <span>Nova Solicitação</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/manutencao" class="btn btn-outline btn-large dashboard-action-btn">
                            <i class="menu-icon dashboard-action-icon">🔧</i>
                            <span>Todas Solicitações</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/habitat" class="btn btn-outline btn-large dashboard-action-btn">
                            <i class="menu-icon dashboard-action-icon">🏕️</i>
                            <span>Ver Habitats</span>
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.role eq 'VETERINARIO'}">
                        <a href="${pageContext.request.contextPath}/consulta/nova" class="btn btn-primary btn-large dashboard-action-btn">
                            <i class="menu-icon dashboard-action-icon">🩺</i>
                            <span>Nova Consulta</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/consulta" class="btn btn-outline btn-large dashboard-action-btn">
                            <i class="menu-icon dashboard-action-icon">📋</i>
                            <span>Consultas</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/consulta/historico" class="btn btn-outline btn-large dashboard-action-btn">
                            <i class="menu-icon dashboard-action-icon">📊</i>
                            <span>Histórico Médico</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/alimentacao/novo" class="btn btn-outline btn-large dashboard-action-btn">
                            <i class="menu-icon dashboard-action-icon">🍽️</i>
                            <span>Registrar Alimentação</span>
                        </a>
                    </c:when>
                </c:choose>
            </div>
        </div>
        
        <c:if test="${sessionScope.role eq 'VETERINARIO' && not empty consultasHoje}">
            <div class="dashboard-section">
                <h2 class="section-subtitle">Consultas de Hoje</h2>
                <div class="dashboard-cards-container">
                    <c:forEach var="consulta" items="${consultasHoje}">
                        <div class="dashboard-card">
                            <div class="dashboard-card-header">
                                <h3 class="dashboard-card-title">${consulta.animal.nome}</h3>
                                <span class="badge badge-${consulta.tipoConsulta}">${consulta.tipoConsulta}</span>
                            </div>
                            <div class="dashboard-card-item">
                                <strong>Horário:</strong> ${fn:substring(consulta.dataHora, 11, 16)}
                            </div>
                            <div class="dashboard-card-item">
                                <strong>Tipo:</strong> ${consulta.tipoConsulta}
                            </div>
                            <a href="${pageContext.request.contextPath}/consulta/detalhes?id=${consulta.id}" 
                                class="btn btn-primary btn-sm" style="width:100%;">Ver Detalhes</a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>
        
        <c:if test="${sessionScope.role eq 'VETERINARIO' && not empty animaisEmTratamento}">
            <div class="dashboard-section">
                <h2 class="section-subtitle">Animais em Tratamento</h2>
                <div class="dashboard-cards-container">
                    <c:forEach var="animal" items="${animaisEmTratamento}" varStatus="status" begin="0" end="3">
                        <div class="dashboard-card">
                            <span class="dashboard-card-label dashboard-card-label-treatment">
                                Em Tratamento
                            </span>
                            <h3 class="dashboard-card-title">${animal.nome}</h3>
                            <div class="dashboard-card-item">
                                <strong>Espécie:</strong> ${animal.especie}
                            </div>
                            <div class="dashboard-card-item-description">
                                ${fn:substring(animal.detalhesSaude, 0, 80)}${fn:length(animal.detalhesSaude) > 80 ? '...' : ''}
                            </div>
                            <div class="dashboard-card-actions">
                                <a href="${pageContext.request.contextPath}/consulta/historico?animal=${animal.id}" 
                                   class="btn btn-primary btn-sm" style="flex:1;">Histórico</a>
                                <a href="${pageContext.request.contextPath}/consulta/nova?animal=${animal.id}" 
                                   class="btn btn-outline btn-sm" style="flex:1;">Nova Consulta</a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <c:if test="${fn:length(animaisEmTratamento) > 4}">
                    <div class="view-all-link">
                        <a href="${pageContext.request.contextPath}/animal?status=EM_TRATAMENTO" class="btn btn-outline">
                            Ver todos os ${fn:length(animaisEmTratamento)} animais em tratamento
                        </a>
                    </div>
                </c:if>
            </div>
        </c:if>
        
        <div class="dashboard-info dashboard-info-default">
            <p><strong>Dica de ${sessionScope.role}:</strong></p>
            <p>
                <c:choose>
                    <c:when test="${sessionScope.role eq 'MANUTENCAO'}">
                        Mantenha o registro das manutenções sempre atualizado para garantir a segurança dos habitats.
                    </c:when>
                    <c:when test="${sessionScope.role eq 'VETERINARIO'}">
                        Sempre verifique o histórico de saúde dos animais antes de iniciar um novo tratamento.
                    </c:when>
                </c:choose>
            </p>
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