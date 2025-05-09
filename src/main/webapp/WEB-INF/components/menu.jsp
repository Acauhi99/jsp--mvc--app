<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty sessionScope.user}">
    <nav class="dashboard-menu">
        <div class="container">
            <div class="menu-header">
                <h3 class="menu-title
                    <c:choose>
                        <c:when test="${sessionScope.user.role eq 'ADMINISTRADOR'}"> admin</c:when>
                        <c:when test="${sessionScope.user.role eq 'FUNCIONARIO' || sessionScope.user.role eq 'VETERINARIO' || sessionScope.user.role eq 'MANUTENCAO'}"> funcionario</c:when>
                        <c:when test="${sessionScope.user.role eq 'VISITANTE'}"> visitante</c:when>
                        <c:otherwise> outro</c:otherwise>
                    </c:choose>
                ">
                    Painel de Controle: ${sessionScope.user.role}
                </h3>
            </div>
            
            <div class="menu-items">
                <c:choose>
                    <%-- Menu para Administrador --%>
                    <c:when test="${sessionScope.user.role eq 'ADMINISTRADOR'}">
                        <a href="${pageContext.request.contextPath}/dashboard/admin"><i class="menu-icon">📊</i> Dashboard</a>
                        
                        <span class="separator"></span>
                        <span class="menu-section">Cadastros</span>
                        
                        <a href="${pageContext.request.contextPath}/animal"><i class="menu-icon">🦁</i> Animais</a>
                        <a href="${pageContext.request.contextPath}/habitat"><i class="menu-icon">🏕️</i> Habitats</a>
                        <a href="${pageContext.request.contextPath}/funcionario/veterinario"><i class="menu-icon">👨‍💼</i> Veterinários</a>
                        
                        <span class="separator"></span>
                        <span class="menu-section">Operações</span>
                        
                        <a href="${pageContext.request.contextPath}/alimentacao"><i class="menu-icon">🍽️</i> Alimentação</a>
                        <a href="${pageContext.request.contextPath}/consulta"><i class="menu-icon">🩺</i> Consultas</a>
                        <a href="${pageContext.request.contextPath}/consulta/historico"><i class="menu-icon">📋</i> Histórico Médico</a>
                        <a href="${pageContext.request.contextPath}/manutencao"><i class="menu-icon">🔧</i> Manutenção</a>
                        
                        <span class="separator"></span>
                        <span class="menu-section">Visitantes</span>
                        
                        <a href="${pageContext.request.contextPath}/ingresso/admin"><i class="menu-icon">🎟️</i> Ingressos</a>
                        <a href="${pageContext.request.contextPath}/customer"><i class="menu-icon">👥</i> Visitantes</a>
                        
                        <span class="separator"></span>
                        <span class="menu-section">Relatórios</span>
                        
                        <a href="${pageContext.request.contextPath}/relatorio/consultas"><i class="menu-icon">📉</i> Consultas Realizadas</a>
                        <a href="${pageContext.request.contextPath}/relatorio/vendas"><i class="menu-icon">💰</i> Vendas de Ingressos</a>
                    </c:when>
                    
                    <%-- Menu para Veterinários --%>
                    <c:when test="${sessionScope.user.role eq 'VETERINARIO'}">
                        <a href="${pageContext.request.contextPath}/dashboard/funcionario"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal"><i class="menu-icon">🦁</i> Animais</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/consulta"><i class="menu-icon">🩺</i> Consultas</a>
                        <a href="${pageContext.request.contextPath}/consulta/historico"><i class="menu-icon">📋</i> Histórico</a>
                        <a href="${pageContext.request.contextPath}/alimentacao"><i class="menu-icon">🍽️</i> Alimentação</a>
                    </c:when>
                    
                    <%-- Menu para Equipe de Manutenção --%>
                    <c:when test="${sessionScope.user.role eq 'MANUTENCAO'}">
                        <a href="${pageContext.request.contextPath}/dashboard/funcionario"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/habitat"><i class="menu-icon">🏕️</i> Habitats</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/manutencao"><i class="menu-icon">🔧</i> Solicitações</a>
                    </c:when>
                    
                    <%-- Menu para Visitantes --%>
                    <c:when test="${sessionScope.user.role eq 'VISITANTE'}">
                        <a href="${pageContext.request.contextPath}/dashboard/visitor"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal/galeria"><i class="menu-icon">🦁</i> Animais</a>
                        <a href="${pageContext.request.contextPath}/habitat"><i class="menu-icon">🏕️</i> Habitats</a>
                        <span class="separator"></span>
                        <a href="${pageContext.request.contextPath}/ingresso"><i class="menu-icon">🎟️</i> Meus Ingressos</a>
                        <a href="${pageContext.request.contextPath}/ingresso/comprar"><i class="menu-icon">🛒</i> Comprar</a>
                    </c:when>
                </c:choose>
                
                <a href="${pageContext.request.contextPath}/perfil" class="profile-link"><i class="menu-icon">👤</i> Perfil</a>
                <a href="${pageContext.request.contextPath}/auth/logout" class="logout-link"><i class="menu-icon">🚪</i> Sair</a>
            </div>
        </div>
    </nav>
</c:if>