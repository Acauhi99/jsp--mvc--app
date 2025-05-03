<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:master title="Meu Perfil">
    <div class="container">
        <h1 class="section-title">Meu Perfil</h1>
        <div class="details-container">
            <div class="details-header">
                <h2 class="details-title">${sessionScope.user.nome}</h2>
            </div>
            <div class="details-section">
                <div class="detail-item">
                    <span class="detail-label">E-mail:</span>
                    <span class="detail-value">${sessionScope.user.email}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Cargo:</span>
                    <span class="detail-value">${sessionScope.user.role}</span>
                </div>
            </div>
        </div>
    </div>
</t:master>