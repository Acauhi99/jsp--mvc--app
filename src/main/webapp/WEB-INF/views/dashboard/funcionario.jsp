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
                    <c:when test="${sessionScope.user.role eq 'VETERINARIO'}">
                        <a href="${pageContext.request.contextPath}/consulta/nova" class="btn btn-primary btn-large">Nova Consulta</a>
                        <a href="${pageContext.request.contextPath}/consulta" class="btn btn-outline btn-large">Consultas</a>
                        <a href="${pageContext.request.contextPath}/consulta/historico" class="btn btn-outline btn-large">Histórico Médico</a>
                        <a href="${pageContext.request.contextPath}/alimentacao/novo" class="btn btn-outline btn-large">Registrar Alimentação</a>
                        <a href="${pageContext.request.contextPath}/animal" class="btn btn-outline btn-large">Animais</a>
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
            
            <c:if test="${sessionScope.user.role eq 'VETERINARIO' && not empty consultasHoje}">
                <div class="dashboard-section">
                    <h2 class="section-subtitle">Consultas de Hoje</h2>
                    <div class="cards-container" style="display:grid;grid-template-columns:repeat(auto-fill, minmax(300px, 1fr));gap:1rem;">
                        <c:forEach var="consulta" items="${consultasHoje}">
                            <div class="card" style="background:white;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1);padding:1.2rem;">
                                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1rem;">
                                    <h3 style="margin:0;color:var(--primary-color);font-size:1.25rem;">${consulta.animal.nome}</h3>
                                    <span class="badge badge-${consulta.tipoConsulta}">${consulta.tipoConsulta}</span>
                                </div>
                                <div style="margin-bottom:0.8rem;font-size:0.95rem;">
                                    <strong>Horário:</strong> ${fn:substring(consulta.dataHora, 11, 16)}
                                </div>
                                <div style="margin-bottom:1.2rem;font-size:0.95rem;">
                                    <strong>Tipo:</strong> ${consulta.tipoConsulta}
                                </div>
                                <a href="${pageContext.request.contextPath}/consulta/detalhes?id=${consulta.id}" 
                                   class="btn btn-primary btn-sm" style="width:100%;">Ver Detalhes</a>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
            
            <c:if test="${sessionScope.user.role eq 'VETERINARIO' && not empty animaisEmTratamento}">
                <div class="dashboard-section">
                    <h2 class="section-subtitle">Animais em Tratamento</h2>
                    <div style="display:grid;grid-template-columns:repeat(auto-fill, minmax(300px, 1fr));gap:1rem;">
                        <c:forEach var="animal" items="${animaisEmTratamento}" varStatus="status" begin="0" end="3">
                            <div style="background:white;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1);padding:1.2rem;position:relative;overflow:hidden;">
                                <span style="position:absolute;top:0;right:0;padding:0.4rem 1rem;background:#fae2e2;color:#e74c3c;font-weight:600;font-size:0.85rem;border-bottom-left-radius:8px;">
                                    Em Tratamento
                                </span>
                                <h3 style="margin-top:0.5rem;margin-bottom:1rem;color:var(--primary-color);font-size:1.25rem;">${animal.nome}</h3>
                                <div style="margin-bottom:0.8rem;font-size:0.95rem;">
                                    <strong>Espécie:</strong> ${animal.especie}
                                </div>
                                <div style="margin-bottom:1.2rem;font-size:0.95rem;color:#666;">
                                    ${fn:substring(animal.detalhesSaude, 0, 80)}${fn:length(animal.detalhesSaude) > 80 ? '...' : ''}
                                </div>
                                <div style="display:flex;gap:0.5rem;">
                                    <a href="${pageContext.request.contextPath}/consulta/historico?animal=${animal.id}" 
                                       class="btn btn-primary btn-sm" style="flex:1;">Histórico</a>
                                    <a href="${pageContext.request.contextPath}/consulta/nova?animal=${animal.id}" 
                                       class="btn btn-outline btn-sm" style="flex:1;">Nova Consulta</a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <c:if test="${fn:length(animaisEmTratamento) > 4}">
                        <div style="text-align:center;margin-top:1rem;">
                            <a href="${pageContext.request.contextPath}/animal?status=EM_TRATAMENTO" class="btn btn-outline">
                                Ver todos os ${fn:length(animaisEmTratamento)} animais em tratamento
                            </a>
                        </div>
                    </c:if>
                </div>
            </c:if>
            
            <c:if test="${sessionScope.user.role eq 'VETERINARIO' && not empty acompanhamentos}">
                <div class="dashboard-section">
                    <h2 class="section-subtitle">Acompanhamentos Programados</h2>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Animal</th>
                                <th>Tipo</th>
                                <th>Data Retorno</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="consulta" items="${acompanhamentos}">
                                <tr>
                                    <td>${consulta.animal.nome}</td>
                                    <td>${consulta.tipoConsulta}</td>
                                    <td>
                                        ${fn:substring(consulta.dataRetorno, 8, 10)}/${fn:substring(consulta.dataRetorno, 5, 7)}/${fn:substring(consulta.dataRetorno, 0, 4)}
                                        ${fn:substring(consulta.dataRetorno, 11, 16)}
                                    </td>
                                    <td>
                                        <div class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/consulta/detalhes?id=${consulta.id}" 
                                               class="btn btn-sm btn-view action-btn">Ver</a>
                                            <a href="${pageContext.request.contextPath}/consulta/nova?acompanhamento=${consulta.id}" 
                                               class="btn btn-sm btn-primary action-btn">Agendar</a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
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