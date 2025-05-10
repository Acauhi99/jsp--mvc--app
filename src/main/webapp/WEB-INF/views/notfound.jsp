<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Página não encontrada">
    <div class="container main-content">
        <div class="notfound-container">
            <div class="notfound-icon">🔍</div>
            <h1 class="notfound-title">Página não encontrada</h1>
            <p class="notfound-message">Desculpe, não conseguimos encontrar a página que você está procurando.</p>
            <p class="notfound-code">Erro 404</p>
            
            <div class="notfound-actions">
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Voltar para página inicial</a>
            </div>
            
            <div class="notfound-suggestions">
                <h3>Você pode tentar:</h3>
                <ul>
                    <li>Verificar se a URL foi digitada corretamente</li>
                    <li>Voltar para a página anterior</li>
                    <li>Explorar as seções do Zoo Park abaixo</li>
                </ul>
            </div>
            
            <div class="notfound-quick-links">
                <a href="${pageContext.request.contextPath}/contact" class="quick-link">
                    <i class="menu-icon">📞</i>
                    <span>Contato</span>
                </a>
            </div>
        </div>
    </div>
</t:master>