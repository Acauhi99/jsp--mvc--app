<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Detalhes da Consulta">
    <div class="details-container">
        <a href="javascript:history.back()" class="back-link">&larr; Voltar</a>
        <div class="details-header" style="flex-direction:column;align-items:center;">
            <div class="profile-avatar" style="font-size:2.5rem;">🩺</div>
            <h2 class="details-title">${consulta.animal.nome}</h2>
            <span class="badge badge-${consulta.status}" style="margin-top:0.5rem;">${consulta.status}</span>
        </div>
        <div class="details-section details-grid">
            <div class="detail-item">
                <span class="detail-label">Tipo de Consulta:</span>
                <span class="detail-value">${consulta.tipoConsulta}</span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Animal:</span>
                <span class="detail-value">${consulta.animal.nome} (${consulta.animal.especie})</span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Veterinário:</span>
                <span class="detail-value">
                    <c:choose>
                        <c:when test="${not empty consulta.veterinario}">${consulta.veterinario.nome}</c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Data/Hora:</span>
                <span class="detail-value">
                    <c:choose>
                        <c:when test="${not empty consulta.dataHora}">
                            ${fn:substring(consulta.dataHora, 8, 10)}/${fn:substring(consulta.dataHora, 5, 7)}/${fn:substring(consulta.dataHora, 0, 4)}
                            ${fn:substring(consulta.dataHora, 11, 16)}
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Status:</span>
                <span class="detail-value">
                    <span class="badge badge-${consulta.status}">${consulta.status}</span>
                </span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Acompanhamento:</span>
                <span class="detail-value">
                    <c:choose>
                        <c:when test="${consulta.acompanhamentoNecessario}">
                            <span class="badge badge-PENDENTE">Necessário</span>
                            <c:if test="${not empty consulta.dataRetorno}">
                                <br>
                                <small>Data: ${fn:substring(consulta.dataRetorno, 8, 10)}/${fn:substring(consulta.dataRetorno, 5, 7)}/${fn:substring(consulta.dataRetorno, 0, 4)}
                                ${fn:substring(consulta.dataRetorno, 11, 16)}</small>
                            </c:if>
                        </c:when>
                        <c:otherwise>Não necessário</c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>
        
        <div class="details-section">
            <h2 style="font-size:1.3rem;color:var(--primary-color);margin-bottom:1rem;padding-bottom:0.5rem;border-bottom:1px solid #eee;">Diagnóstico</h2>
            <p style="margin-bottom:1.5rem;">${consulta.diagnostico}</p>
        </div>
        
        <div class="details-section">
            <h2 style="font-size:1.3rem;color:var(--primary-color);margin-bottom:1rem;padding-bottom:0.5rem;border-bottom:1px solid #eee;">Tratamento Recomendado</h2>
            <p style="margin-bottom:1.5rem;">${consulta.tratamento}</p>
        </div>
        
        <div class="details-section">
            <h2 style="font-size:1.3rem;color:var(--primary-color);margin-bottom:1rem;padding-bottom:0.5rem;border-bottom:1px solid #eee;">Medicamentos</h2>
            <p style="margin-bottom:1.5rem;">${consulta.medicamentos}</p>
        </div>
        
        <c:if test="${not empty consulta.observacoes}">
            <div class="details-section">
                <h2 style="font-size:1.3rem;color:var(--primary-color);margin-bottom:1rem;padding-bottom:0.5rem;border-bottom:1px solid #eee;">Observações Adicionais</h2>
                <p>${consulta.observacoes}</p>
            </div>
        </c:if>
        
        <div class="details-actions" style="margin-top:2rem;">
            <c:if test="${consulta.status != 'CONCLUIDA' && consulta.status != 'CANCELADA'}">
                <a href="${pageContext.request.contextPath}/consulta/editar?id=${consulta.id}" class="btn btn-primary">Editar Consulta</a>
            </c:if>
            <a href="${pageContext.request.contextPath}/animal/detalhes?id=${consulta.animal.id}" class="btn btn-outline">Ver Animal</a>
        </div>
    </div>
</t:master>