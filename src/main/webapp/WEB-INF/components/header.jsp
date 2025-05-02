<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="main-header">
    <div class="logo-container">
        <a href="${pageContext.request.contextPath}/" class="logo">Zoo Park</a>
    </div>

    <nav class="main-nav">
        <ul>
            <li><a href="${pageContext.request.contextPath}/">Início</a></li>
            <li><a href="${pageContext.request.contextPath}/animals">Animais</a></li>
            <li><a href="${pageContext.request.contextPath}/tickets">Ingressos</a></li>
            <li><a href="${pageContext.request.contextPath}/about">Sobre</a></li>
            <li><a href="${pageContext.request.contextPath}/contact">Contato</a></li>
        </ul>
    </nav>

    <div class="auth-buttons">
        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-outline">Login</a>
                <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary">Registrar</a>
            </c:when>
            <c:otherwise>
                <div class="user-menu">
                    <span>Bem-vindo(a), ${sessionScope.user.nome}</span>
                    <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline">Sair</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</header>