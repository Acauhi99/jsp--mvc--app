<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:master title="Detalhes do Animal">
    <div class="container">
        <a href="${pageContext.request.contextPath}/animal/galeria" class="back-link">&larr; Voltar para a lista</a>
        <h1 class="section-title">${animal.nome}</h1>
        <div class="details-container">
            <div class="animal-image" style="height:220px; margin-bottom:2rem; display:flex;align-items:center;justify-content:center;font-size:4rem;">
                🐾
            </div>
            <div class="details-section details-grid">
                <div class="detail-item">
                    <span class="detail-label">Espécie:</span>
                    <span class="detail-value">${animal.especie}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Nome Científico:</span>
                    <span class="detail-value">${animal.nomeCientifico}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Habitat:</span>
                    <span class="detail-value">${animal.habitat.nome}</span>
                </div>
            </div>
        </div>
    </div>
</t:master>