<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard ${sessionScope.role}">
    <div class="container main-content">
        <h1 class="section-title">Dashboard do ${sessionScope.role}</h1>
        
        <div class="dashboard-welcome" style="text-align:center; margin-bottom:2rem; background:#f9f9f9; padding:1.5rem; border-radius:8px; box-shadow:var(--box-shadow);">
            <div style="font-size:3rem; margin-bottom:0.5rem;">👨‍💼</div>
            <h2 style="color:var(--primary-color); margin-bottom:0.5rem;">Bem-vindo, ${sessionScope.userName}!</h2>
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
                        <a href="${pageContext.request.contextPath}/manutencao/novo" class="btn btn-primary btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                            <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">📝</i>
                            <span>Nova Solicitação</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/manutencao" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                            <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🔧</i>
                            <span>Todas Solicitações</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/habitat" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                            <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🏕️</i>
                            <span>Ver Habitats</span>
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.role eq 'VETERINARIO'}">
                        <a href="${pageContext.request.contextPath}/consulta/nova" class="btn btn-primary btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                            <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🩺</i>
                            <span>Nova Consulta</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/consulta" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                            <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">📋</i>
                            <span>Consultas</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/consulta/historico" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                            <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">📊</i>
                            <span>Histórico Médico</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/alimentacao/novo" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                            <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🍽️</i>
                            <span>Registrar Alimentação</span>
                        </a>
                    </c:when>
                </c:choose>
            </div>
        </div>
        
        <c:if test="${sessionScope.role eq 'VETERINARIO' && not empty consultasHoje}">
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
        
        <c:if test="${sessionScope.role eq 'VETERINARIO' && not empty animaisEmTratamento}">
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
        
        <div class="dashboard-info" style="margin:2rem auto; max-width:800px; text-align:center;">
            <p style="margin-bottom:0.5rem;"><strong>Dica de ${sessionScope.role}:</strong></p>
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
            <div class="alert alert-error" style="margin:1rem auto; max-width:800px;">
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