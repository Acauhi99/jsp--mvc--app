<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty sessionScope.user}">
    <nav class="dashboard-menu">
        <div class="container">
            <div class="menu-header">
                <h3>Painel de Controle: ${sessionScope.user.role}</h3>
            </div>
            
            <div class="menu-items">
                <c:choose>
                    <%-- Menu para Administrador --%>
                    <c:when test="${sessionScope.user.role eq 'ADMINISTRADOR'}">
                        <a href="${pageContext.request.contextPath}/dashboard/admin"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal"><i class="menu-icon">🦊</i> Animais</a>
                        <a href="${pageContext.request.contextPath}/habitat"><i class="menu-icon">🏞️</i> Habitats</a>
                        <a href="${pageContext.request.contextPath}/funcionario"><i class="menu-icon">👨‍💼</i> Funcionários</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/alimentacao"><i class="menu-icon">🍽️</i> Alimentação</a>
                        <a href="${pageContext.request.contextPath}/consulta"><i class="menu-icon">🩺</i> Consultas</a>
                        <a href="${pageContext.request.contextPath}/manutencao"><i class="menu-icon">🔧</i> Manutenção</a>
                    </c:when>
                    
                    <%-- Menu para Veterinários --%>
                    <c:when test="${sessionScope.user.role eq 'VETERINARIO'}">
                        <a href="${pageContext.request.contextPath}/dashboard/funcionario"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal"><i class="menu-icon">🦁</i> Animais</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/consulta"><i class="menu-icon">🩺</i> Consultas</a>
                        <a href="${pageContext.request.contextPath}/consulta/historico"><i class="menu-icon">📋</i> Histórico</a>
                    </c:when>
                    
                    <%-- Menu para Tratadores --%>
                    <c:when test="${sessionScope.user.role eq 'TRATADOR'}">
                        <a href="${pageContext.request.contextPath}/dashboard/funcionario"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal"><i class="menu-icon">🦁</i> Animais</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/alimentacao"><i class="menu-icon">🍽️</i> Alimentação</a>
                        <a href="${pageContext.request.contextPath}/alimentacao/historico"><i class="menu-icon">📋</i> Histórico</a>
                    </c:when>
                    
                    <%-- Menu para Equipe de Manutenção --%>
                    <c:when test="${sessionScope.user.role eq 'MANUTENCAO'}">
                        <a href="${pageContext.request.contextPath}/dashboard/funcionario"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/habitat"><i class="menu-icon">🏞️</i> Habitats</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/manutencao"><i class="menu-icon">🔧</i> Solicitações</a>
                        <a href="${pageContext.request.contextPath}/manutencao/programada"><i class="menu-icon">📅</i> Programadas</a>
                    </c:when>
                    
                    <%-- Menu para Visitantes --%>
                    <c:when test="${sessionScope.user.role eq 'VISITANTE'}">
                        <a href="${pageContext.request.contextPath}/dashboard/visitor"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal/galeria"><i class="menu-icon">🦁</i> Animais</a>
                        <a href="${pageContext.request.contextPath}/habitat/mapa"><i class="menu-icon">🗺️</i> Mapa</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/ingresso"><i class="menu-icon">🎟️</i> Meus Ingressos</a>
                        <a href="${pageContext.request.contextPath}/ingresso/comprar"><i class="menu-icon">🛒</i> Comprar</a>
                    </c:when>
                    
                    <%-- Menu padrão para outros funcionários --%>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/dashboard"><i class="menu-icon">🏠</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal"><i class="menu-icon">🦁</i> Animais</a>
                    </c:otherwise>
                </c:choose>
                
                <a href="${pageContext.request.contextPath}/perfil" class="profile-link"><i class="menu-icon">👤</i> Perfil</a>
                <a href="${pageContext.request.contextPath}/auth/logout" class="logout-link"><i class="menu-icon">🚪</i> Sair</a>
            </div>
        </div>
    </nav>
</c:if>