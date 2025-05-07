<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Detalhes da Alimentação">
    <div class="details-container">
        <a href="${pageContext.request.contextPath}/alimentacao" class="back-link">&larr; Voltar para Lista</a>
        
        <div class="details-header">
            <h1 class="details-title">Registro de Alimentação</h1>
            
            <div class="details-actions">
                
            </div>
        </div>
        
        <div class="details-section details-grid">
            <div class="detail-item">
                <span class="detail-label">Animal</span>
                <span class="detail-value">${alimentacao.animal.nome}</span>
            </div>
            
            <div class="detail-item">
                <span class="detail-label">Espécie</span>
                <span class="detail-value">${alimentacao.animal.especie}</span>
            </div>
            
            <div class="detail-item">
                <span class="detail-label">Tipo de Alimento</span>
                <span class="detail-value">${alimentacao.tipoAlimento}</span>
            </div>
            
            <div class="detail-item">
                <span class="detail-label">Quantidade</span>
                <span class="detail-value">${alimentacao.quantidade} ${alimentacao.unidadeMedida}</span>
            </div>
            
            <div class="detail-item">
                <span class="detail-label">Data e Hora</span>
                <span class="detail-value">
                    ${fn:substring(alimentacao.dataHora, 8, 10)}/${fn:substring(alimentacao.dataHora, 5, 7)}/${fn:substring(alimentacao.dataHora, 0, 4)}
                    ${fn:substring(alimentacao.dataHora, 11, 16)}
                </span>
            </div>
            
            <div class="detail-item">
                <span class="detail-label">Responsável</span>
                <span class="detail-value">${alimentacao.funcionarioResponsavel.nome}</span>
            </div>
        </div>
        
        <c:if test="${not empty alimentacao.observacoes}">
            <div class="details-section">
                <h2>Observações</h2>
                <p>${alimentacao.observacoes}</p>
            </div>
        </c:if>
        
        <div class="details-section">
            <h2>Outras informações</h2>
            <p>
                <strong>Habitat do animal:</strong> ${alimentacao.animal.habitat.nome}
            </p>
            <p>
                <strong>Status de saúde:</strong> 
                <span class="badge badge-${alimentacao.animal.statusSaude == 'EM_TRATAMENTO' ? 'EMERGENCIA' : 'CONCLUIDA'}">
                    ${alimentacao.animal.statusSaude == 'EM_TRATAMENTO' ? 'Em Tratamento' : 'Saudável'}
                </span>
            </p>
        </div>
        
        <div class="details-actions" style="margin-top: 2rem;">
            <a href="${pageContext.request.contextPath}/alimentacao?animalId=${alimentacao.animal.id}" class="btn btn-outline">Ver Histórico de Alimentação</a>
        </div>
    </div>
</t:master>