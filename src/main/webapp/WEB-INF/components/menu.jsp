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
                        <a href="${pageContext.request.contextPath}/dashboard/admin" title="Visualizar Dashboard"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal" title="Gerenciar Animais"><i class="menu-icon">🦊</i> Animais</a>
                        <a href="${pageContext.request.contextPath}/habitat" title="Gerenciar Habitats"><i class="menu-icon">🏞️</i> Habitats</a>
                        <a href="${pageContext.request.contextPath}/funcionario" title="Gerenciar Funcionários"><i class="menu-icon">👨‍💼</i> Funcionários</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/alimentacao" title="Gerenciar Alimentação"><i class="menu-icon">🍽️</i> Alimentação</a>
                        <a href="${pageContext.request.contextPath}/consulta" title="Gerenciar Consultas"><i class="menu-icon">🩺</i> Consultas</a>
                        <a href="${pageContext.request.contextPath}/manutencao" title="Gerenciar Manutenção"><i class="menu-icon">🔧</i> Manutenção</a>
                    </c:when>
                    
                    <%-- Menu para Veterinários --%>
                    <c:when test="${sessionScope.user.role eq 'VETERINARIO'}">
                        <a href="${pageContext.request.contextPath}/dashboard/funcionario" title="Visualizar Dashboard"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal" title="Visualizar Animais"><i class="menu-icon">🦁</i> Animais</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/consulta" title="Gerenciar Consultas"><i class="menu-icon">🩺</i> Consultas</a>
                        <a href="${pageContext.request.contextPath}/consulta/historico" title="Ver Histórico de Consultas"><i class="menu-icon">📋</i> Histórico</a>
                    </c:when>
                    
                    <%-- Menu para Tratadores --%>
                    <c:when test="${sessionScope.user.role eq 'TRATADOR'}">
                        <a href="${pageContext.request.contextPath}/dashboard/funcionario" title="Visualizar Dashboard"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal" title="Visualizar Animais"><i class="menu-icon">🦁</i> Animais</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/alimentacao" title="Registrar Alimentação"><i class="menu-icon">🍽️</i> Alimentação</a>
                        <a href="${pageContext.request.contextPath}/alimentacao/historico" title="Ver Histórico de Alimentação"><i class="menu-icon">📋</i> Histórico</a>
                    </c:when>
                    
                    <%-- Menu para Equipe de Manutenção --%>
                    <c:when test="${sessionScope.user.role eq 'MANUTENCAO'}">
                        <a href="${pageContext.request.contextPath}/dashboard/funcionario" title="Visualizar Dashboard"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/habitat" title="Visualizar Habitats"><i class="menu-icon">🏞️</i> Habitats</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/manutencao" title="Ver Solicitações de Manutenção"><i class="menu-icon">🔧</i> Solicitações</a>
                        <a href="${pageContext.request.contextPath}/manutencao/programada" title="Ver Manutenções Programadas"><i class="menu-icon">📅</i> Programadas</a>
                    </c:when>
                    
                    <%-- Menu para Visitantes --%>
                    <c:when test="${sessionScope.user.role eq 'VISITANTE'}">
                        <a href="${pageContext.request.contextPath}/dashboard/visitor" title="Visualizar Dashboard"><i class="menu-icon">📊</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal/galeria" title="Ver Galeria de Animais"><i class="menu-icon">🦁</i> Animais</a>
                        <a href="${pageContext.request.contextPath}/habitat/mapa" title="Ver Mapa do Zoológico"><i class="menu-icon">🗺️</i> Mapa</a>
                        
                        <span class="separator"></span>
                        
                        <a href="${pageContext.request.contextPath}/ingresso" title="Ver Meus Ingressos"><i class="menu-icon">🎟️</i> Meus Ingressos</a>
                        <a href="${pageContext.request.contextPath}/ingresso/comprar" title="Comprar Novos Ingressos"><i class="menu-icon">🛒</i> Comprar</a>
                    </c:when>
                    
                    <%-- Menu padrão para outros funcionários --%>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/dashboard" title="Visualizar Dashboard"><i class="menu-icon">🏠</i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/animal" title="Visualizar Animais"><i class="menu-icon">🦁</i> Animais</a>
                    </c:otherwise>
                </c:choose>
                
                <a href="${pageContext.request.contextPath}/perfil" class="profile-link" title="Editar Meu Perfil"><i class="menu-icon">👤</i> Perfil</a>
                <a href="${pageContext.request.contextPath}/auth/logout" class="logout-link" title="Sair do Sistema"><i class="menu-icon">🚪</i> Sair</a>
            </div>
        </div>
    </nav>
</c:if>