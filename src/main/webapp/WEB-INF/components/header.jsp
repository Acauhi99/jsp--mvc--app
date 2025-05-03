<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="main-header">
    <div class="logo-container">
        <a href="${pageContext.request.contextPath}/" class="logo">🦁 Zoo Park</a>
    </div>

    <nav class="main-nav">
        <ul>
            <li><a href="${pageContext.request.contextPath}/"><i class="menu-icon">🏠</i> Início</a></li>
            <li><a href="${pageContext.request.contextPath}/mapa"><i class="menu-icon">🗺️</i> Mapa</a></li>
            <c:if test="${not empty sessionScope.user}">
                <li>
                    <a href="${pageContext.request.contextPath}/dashboard" class="dashboard-link">
                        <i class="menu-icon">📊</i> Dashboard
                    </a>
                </li>
            </c:if>
            <li><a href="${pageContext.request.contextPath}/about"><i class="menu-icon">ℹ️</i> Sobre</a></li>
            <li><a href="${pageContext.request.contextPath}/contact"><i class="menu-icon">📞</i> Contato</a></li>
        </ul>
    </nav>

    <div class="auth-buttons">
        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-outline">
                    <i class="menu-icon">🔑</i> Login
                </a>
                <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary">
                    <i class="menu-icon">📝</i> Registrar
                </a>
            </c:when>
            <c:otherwise>
                <div class="user-menu">
                    <span><i class="menu-icon">👤</i> Bem-vindo(a), ${sessionScope.user.nome}</span>
                    <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline">
                        <i class="menu-icon">🚪</i> Sair
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</header>