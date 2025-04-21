<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Home">
    <div class="hero">
        <div class="hero-content">
            <h1>Welcome to Zoo Park</h1>
            <p>Experience the beauty of wildlife in a safe and educational environment</p>
            
            <c:choose>
                <c:when test="${empty sessionScope.user}">
                    <div class="auth-prompt">
                        <p class="auth-message">To buy tickets and explore our full content, please login or create an account</p>
                        <div class="auth-buttons-large">
                            <a href="${pageContext.request.contextPath}/api/auth/login" class="btn btn-primary btn-large">Login</a>
                            <a href="${pageContext.request.contextPath}/api/auth/register" class="btn btn-outline btn-large">Register</a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/tickets" class="btn btn-primary btn-large">Buy Tickets</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <section class="featured-animals">
        <div class="container">
            <h2 class="section-title">Featured Animals</h2>
            <div class="featured-grid">
                <div class="animal-card">
                    <div class="animal-image placeholder"></div>
                    <h3>Lions</h3>
                    <p>The king of the jungle awaits your visit</p>
                </div>
                <div class="animal-card">
                    <div class="animal-image placeholder"></div>
                    <h3>Elephants</h3>
                    <p>Meet our gentle giants</p>
                </div>
                <div class="animal-card">
                    <div class="animal-image placeholder"></div>
                    <h3>Penguins</h3>
                    <p>Watch them play and swim</p>
                </div>
            </div>
        </div>
    </section>
    
    <section class="about-preview">
        <div class="container">
            <div class="about-content">
                <div class="about-text">
                    <h2 class="section-title">About Our Zoo</h2>
                    <p>Zoo Park is dedicated to conservation, education, and providing a safe environment for both animals and visitors. Our park features over 500 animal species from around the world.</p>
                    <p>With modern facilities and expert staff, we ensure the wellbeing of our animals while offering an unforgettable experience to our visitors.</p>
                    
                    <c:choose>
                        <c:when test="${empty sessionScope.user}">
                            <p class="auth-hint">Please <a href="${pageContext.request.contextPath}/api/auth/login">login</a> to learn more about our conservation efforts.</p>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/about" class="btn btn-outline">Learn More</a>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="about-image placeholder"></div>
            </div>
        </div>
    </section>
</t:master>