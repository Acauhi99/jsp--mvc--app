<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container main-content">
    <a href="${pageContext.request.contextPath}/alimentacao" class="back-link">
        ← Voltar para a lista
    </a>

    <div class="details-container">
        <div class="details-header">
            <h1 class="details-title">Detalhes da Alimentação</h1>
            
            <div class="details-actions">
                <a href="${pageContext.request.contextPath}/alimentacao/editar/${alimentacao.id}" class="btn btn-primary">Editar</a>
                <form action="${pageContext.request.contextPath}/alimentacao" method="post" style="display: inline;">
                    <input type="hidden" name="_method" value="DELETE">
                    <input type="hidden" name="id" value="${alimentacao.id}">
                    <button type="submit" class="btn btn-outline" onclick="return confirm('Tem certeza que deseja excluir este registro?')">Excluir</button>
                </form>
            </div>
        </div>

        <div class="details-section">
            <h2>Informações Gerais</h2>
            <div class="details-grid">
                <div class="detail-item">
                    <span class="detail-label">Animal:</span>
                    <span class="detail-value">${alimentacao.animal.nome}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Espécie:</span>
                    <span class="detail-value">${alimentacao.animal.especie}</span>
                </div>
            </div>
            
            <div class="details-grid">
                <div class="detail-item">
                    <span class="detail-label">Tipo de Alimento:</span>
                    <span class="detail-value">${alimentacao.tipoAlimento}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Quantidade:</span>
                    <span class="detail-value">${alimentacao.quantidade} ${alimentacao.unidadeMedida}</span>
                </div>
            </div>
            
            <div class="details-grid">
                <div class="detail-item">
                    <span class="detail-label">Data e Hora:</span>
                    <span class="detail-value">
                        <fmt:parseDate value="${alimentacao.dataHora}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                        <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                    </span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Funcionário Responsável:</span>
                    <span class="detail-value">${alimentacao.funcionarioResponsavel.nome}</span>
                </div>
            </div>
            
            <div class="detail-item">
                <span class="detail-label">Observações:</span>
                <p class="detail-value">${not empty alimentacao.observacoes ? alimentacao.observacoes : 'Nenhuma observação registrada.'}</p>
            </div>
        </div>

        <div class="details-section">
            <h2>Informações do Recinto</h2>
            <div class="details-grid">
                <div class="detail-item">
                    <span class="detail-label">Habitat:</span>
                    <span class="detail-value">${alimentacao.animal.habitat.nome}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Tipo:</span>
                    <span class="detail-value">${alimentacao.animal.habitat.tipo}</span>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />