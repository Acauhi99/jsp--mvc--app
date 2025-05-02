<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Home">
    <div class="hero">
        <div class="hero-content">
            <h1>Bem-vindo ao Zoo Park</h1>
            <p>Experimente a beleza da vida selvagem em um ambiente seguro e educativo</p>

            <c:choose>
                <c:when test="${empty sessionScope.user}">
                    <div class="auth-prompt">
                        <p class="auth-message">Para comprar ingressos e explorar nosso conteúdo completo, por favor, faça login ou crie uma conta</p>
                        <div class="auth-buttons-large">
                            <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-primary btn-large">Login</a>
                            <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-outline btn-large">Registrar</a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/tickets" class="btn btn-primary btn-large">Comprar Ingressos</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <section class="featured-animals">
        <div class="container">
            <h2 class="section-title">Animais em Destaque</h2>
            <div class="featured-grid">
                <div class="animal-card">
                    <img src="${pageContext.request.contextPath}/images/lion-4366887_1920.jpg" alt="Leão" class="animal-image">
                    <h3>Leões</h3>
                    <p>O rei da selva espera por sua visita</p>
                </div>
                <div class="animal-card">
                    <img src="${pageContext.request.contextPath}/images/elephants-2923917_1920.jpg" alt="Elefantes" class="animal-image">
                    <h3>Elefantes</h3>
                    <p>Conheça nossos gigantes gentis</p>
                </div>
                <div class="animal-card">
                    <img src="${pageContext.request.contextPath}/images/king-penguins-384252_1920.jpg" alt="Pinguins" class="animal-image">
                    <h3>Pinguins</h3>
                    <p>Observe-os brincar e nadar</p>
                </div>
            </div>
        </div>
    </section>

    <section class="about-preview">
        <div class="container">
            <div class="about-content">
                <div class="about-text">
                    <h2 class="section-title">Sobre Nosso Zoológico</h2>
                    <p>O Zoo Park é dedicado à conservação, educação e a fornecer um ambiente seguro para animais e visitantes. Nosso parque abriga mais de 500 espécies de animais de todo o mundo.</p>
                    <p>Com instalações modernas e equipe especializada, garantimos o bem-estar de nossos animais enquanto oferecemos uma experiência inesquecível aos nossos visitantes.</p>

                    <c:choose>
                        <c:when test="${empty sessionScope.user}">
                            <p class="auth-hint">Por favor, <a href="${pageContext.request.contextPath}/auth/login">faça login</a> para saber mais sobre nossos esforços de conservação.</p>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/about" class="btn btn-outline">Saiba Mais</a>
                        </c:otherwise>
                    </c:choose>
                </div>
                <img src="${pageContext.request.contextPath}/images/zoologico_982374.jpg" alt="Vista panorâmica do Zoo Park" class="about-image">
            </div>
        </div>
    </section>
</t:master>