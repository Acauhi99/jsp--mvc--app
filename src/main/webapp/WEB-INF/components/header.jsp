<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="main-header">
    <div class="logo-container">
        <a href="${pageContext.request.contextPath}/" class="logo">Zoo Park</a>
    </div>
    
    <nav class="main-nav">
        <ul>
            <li><a href="${pageContext.request.contextPath}/">Home</a></li>
            <li><a href="${pageContext.request.contextPath}/animals">Animals</a></li>
            <li><a href="${pageContext.request.contextPath}/tickets">Tickets</a></li>
            <li><a href="${pageContext.request.contextPath}/about">About</a></li>
            <li><a href="${pageContext.request.contextPath}/contact">Contact</a></li>
        </ul>
    </nav>
    
    <div class="auth-buttons">
        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/api/auth/login" class="btn btn-outline">Login</a>
                <a href="${pageContext.request.contextPath}/api/auth/register" class="btn btn-primary">Register</a>
            </c:when>
            <c:otherwise>
                <div class="user-menu">
                    <span>Welcome, ${sessionScope.user.nome}</span>
                    <a href="${pageContext.request.contextPath}/api/auth/logout" class="btn btn-outline">Logout</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</header>