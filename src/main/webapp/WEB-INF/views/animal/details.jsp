<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:master title="Detalhes do Animal">
    <div class="container">
        <a href="${pageContext.request.contextPath}/animal/" class="back-link">&larr; Voltar para a lista</a>
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
                    <span class="detail-label">Classe:</span>
                    <span class="detail-value">${animal.classe}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Gênero:</span>
                    <span class="detail-value">${animal.genero}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Habitat:</span>
                    <span class="detail-value">${animal.habitat.nome}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Status de Saúde:</span>
                    <span class="detail-value">
                        <span class="badge badge-${animal.statusSaude == 'EM_TRATAMENTO' ? 'EMERGENCIA' : 'CONCLUIDA'}">
                            ${animal.statusSaude == 'EM_TRATAMENTO' ? 'Em Tratamento' : 'Saudável'}
                        </span>
                    </span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Data de Chegada:</span>
                    <span class="detail-value">
                        <c:if test="${not empty animal.dataChegada}">
                            ${animal.dataChegada.dayOfMonth}/${animal.dataChegada.monthValue}/${animal.dataChegada.year}
                        </c:if>
                    </span>
                </div>
            </div>
            <div class="details-section" style="margin-top:2rem;">
                <h2>Detalhes de Saúde</h2>
                <p style="font-size:1.1rem;">
                    <c:choose>
                        <c:when test="${not empty animal.detalhesSaude}">
                            ${animal.detalhesSaude}
                        </c:when>
                        <c:otherwise>
                            Nenhuma observação registrada.
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
        </div>
    </div>
</t:master>