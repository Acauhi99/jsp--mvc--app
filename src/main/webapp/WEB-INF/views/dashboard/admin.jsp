<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard Administrador">
    <div class="container main-content">
        <h1 class="section-title">Dashboard do Administrador</h1>
        
        <div class="dashboard-welcome" style="text-align:center; margin-bottom:2rem; background:#f9f9f9; padding:1.5rem; border-radius:8px; box-shadow:var(--box-shadow);">
            <div style="font-size:3rem; margin-bottom:0.5rem;">👑</div>
            <h2 style="color:var(--primary-color); margin-bottom:0.5rem;">Bem-vindo, ${sessionScope.user.nome}!</h2>
            <p>Você tem acesso completo às funcionalidades de administração do Zoo Park.</p>
        </div>
        
        <!-- Cadastros -->
        <div class="dashboard-section">
            <h2 class="section-subtitle">Cadastros</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/animal" class="btn btn-primary btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🦁</i>
                    <span>Animais</span>
                </a>
                <a href="${pageContext.request.contextPath}/habitat" class="btn btn-primary btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🏕️</i>
                    <span>Habitats</span>
                </a>
                <a href="${pageContext.request.contextPath}/funcionario/veterinario" class="btn btn-primary btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">👨‍⚕️</i>
                    <span>Veterinários</span>
                </a>
            </div>
        </div>
        
        <!-- Operações -->
        <div class="dashboard-section">
            <h2 class="section-subtitle">Operações</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/alimentacao" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🍽️</i>
                    <span>Alimentação</span>
                </a>
                <a href="${pageContext.request.contextPath}/consulta" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🩺</i>
                    <span>Consultas</span>
                </a>
                <a href="${pageContext.request.contextPath}/consulta/historico" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">📋</i>
                    <span>Histórico Médico</span>
                </a>
                <a href="${pageContext.request.contextPath}/manutencao" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🔧</i>
                    <span>Manutenção</span>
                </a>
            </div>
        </div>
        
        <!-- Visitantes -->
        <div class="dashboard-section">
            <h2 class="section-subtitle">Visitantes</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/ingresso/admin" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">🎟️</i>
                    <span>Ingressos</span>
                </a>
                <a href="${pageContext.request.contextPath}/customer" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">👥</i>
                    <span>Visitantes</span>
                </a>
            </div>
        </div>
        
        <!-- Relatórios -->
        <div class="dashboard-section">
            <h2 class="section-subtitle">Relatórios</h2>
            <div class="dashboard-quick-actions">
                <a href="${pageContext.request.contextPath}/relatorio/consultas" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">📊</i>
                    <span>Consultas Realizadas</span>
                </a>
                <a href="${pageContext.request.contextPath}/relatorio/vendas" class="btn btn-outline btn-large" style="flex:1; min-width:180px; max-width:250px; display:flex; flex-direction:column; align-items:center; padding:1.2rem;">
                    <i class="menu-icon" style="font-size:2rem; margin-bottom:0.5rem;">💰</i>
                    <span>Vendas de Ingressos</span>
                </a>
            </div>
        </div>
        
        <!-- Dica/Informação -->
        <div class="dashboard-info" style="margin:2rem auto; max-width:800px; text-align:center;">
            <p style="margin-bottom:0.5rem;"><strong>Dica de Administrador:</strong></p>
            <p>Monitore regularmente os relatórios de venda e ocupação para otimizar a distribuição de recursos no Zoo Park.</p>
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