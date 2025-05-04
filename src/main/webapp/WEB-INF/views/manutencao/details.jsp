<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Detalhes da Manutenção">
    <div class="details-container">
        <a href="javascript:history.back()" class="back-link">&larr; Voltar</a>
        <div class="details-header" style="flex-direction:column;align-items:center;">
            <div class="profile-avatar" style="font-size:2.5rem;">🔧</div>
            <h2 class="details-title">${manutencao.habitat.nome}</h2>
            <span class="badge badge-${manutencao.status}" style="margin-top:0.5rem;">${manutencao.status}</span>
        </div>
        <div class="details-section details-grid">
            <div class="detail-item">
                <span class="detail-label">Tipo de Serviço:</span>
                <span class="detail-value">${manutencao.tipoManutencao}</span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Prioridade:</span>
                <span class="detail-value">
                    <span class="badge badge-prio-${manutencao.prioridade}">${manutencao.prioridade}</span>
                </span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Status:</span>
                <span class="detail-value">
                    <span class="badge badge-${manutencao.status}">${manutencao.status}</span>
                </span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Solicitante:</span>
                <span class="detail-value">${manutencao.solicitante.nome}</span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Responsável:</span>
                <span class="detail-value">
                    <c:choose>
                        <c:when test="${not empty manutencao.responsavel}">${manutencao.responsavel.nome}</c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Data de Solicitação:</span>
                <span class="detail-value">
                    <c:choose>
                        <c:when test="${not empty manutencao.dataSolicitacao}">
                            ${fn:substring(manutencao.dataSolicitacao, 8, 10)}/${fn:substring(manutencao.dataSolicitacao, 5, 7)}/${fn:substring(manutencao.dataSolicitacao, 0, 4)}
                            ${fn:substring(manutencao.dataSolicitacao, 11, 16)}
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Data Programada:</span>
                <span class="detail-value">
                    <c:choose>
                        <c:when test="${not empty manutencao.dataProgramada}">
                            ${fn:substring(manutencao.dataProgramada, 8, 10)}/${fn:substring(manutencao.dataProgramada, 5, 7)}/${fn:substring(manutencao.dataProgramada, 0, 4)}
                            ${fn:substring(manutencao.dataProgramada, 11, 16)}
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Descrição:</span>
                <span class="detail-value">${manutencao.descricao}</span>
            </div>
            <c:if test="${not empty manutencao.dataConclusao}">
                <div class="detail-item">
                    <span class="detail-label">Data de Conclusão:</span>
                    <span class="detail-value">
                        ${fn:substring(manutencao.dataConclusao, 8, 10)}/${fn:substring(manutencao.dataConclusao, 5, 7)}/${fn:substring(manutencao.dataConclusao, 0, 4)}
                        ${fn:substring(manutencao.dataConclusao, 11, 16)}
                    </span>
                </div>
            </c:if>
            <c:if test="${not empty manutencao.observacaoConclusao}">
                <div class="detail-item">
                    <span class="detail-label">Observação da Conclusão:</span>
                    <span class="detail-value">${manutencao.observacaoConclusao}</span>
                </div>
            </c:if>
        </div>
    </div>
</t:master>