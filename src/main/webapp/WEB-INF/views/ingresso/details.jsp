<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:master title="Detalhes do Ingresso">
    <div class="container">
        <a href="${pageContext.request.contextPath}${isAdmin ? '/ingresso/admin' : '/ingresso'}" class="back-link">&larr; Voltar para lista de ingressos</a>
        
        <div class="details-container">
            <div class="details-header">
                <h1 class="details-title">Ingresso - ${ingresso.tipo}</h1>
                <div class="details-actions">
                    <c:if test="${!ingresso.utilizado && !isAdmin}">
                        <form action="${pageContext.request.contextPath}/ingresso/utilizar" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${ingresso.id}">
                            <button type="submit" class="btn btn-primary">Utilizar Ingresso</button>
                        </form>
                    </c:if>
                   
                </div>
            </div>
            
            <div class="details-section details-grid">
                <div class="detail-item">
                    <span class="detail-label">Código</span>
                    <span class="detail-value">${ingresso.id}</span>
                </div>
                
                <div class="detail-item">
                    <span class="detail-label">Tipo</span>
                    <span class="detail-value">
                        <c:choose>
                            <c:when test="${ingresso.tipo eq 'ADULTO'}">Adulto</c:when>
                            <c:when test="${ingresso.tipo eq 'CRIANCA'}">Criança</c:when>
                            <c:when test="${ingresso.tipo eq 'IDOSO'}">Idoso</c:when>
                            <c:when test="${ingresso.tipo eq 'ESTUDANTE'}">Estudante</c:when>
                            <c:when test="${ingresso.tipo eq 'DEFICIENTE'}">Pessoa com Deficiência</c:when>
                            <c:otherwise>${ingresso.tipo}</c:otherwise>
                        </c:choose>
                    </span>
                </div>
                
                <div class="detail-item">
                    <span class="detail-label">Valor</span>
                    <span class="detail-value">
                        <fmt:formatNumber value="${ingresso.valor}" type="currency" currencySymbol="R$ " minFractionDigits="2"/>
                    </span>
                </div>
                
                <div class="detail-item">
                    <span class="detail-label">Status</span>
                    <span class="detail-value">
                        <span class="badge badge-${ingresso.utilizado ? 'CANCELADA' : 'CONCLUIDA'}">
                            ${ingresso.utilizado ? 'Utilizado' : 'Disponível'}
                        </span>
                    </span>
                </div>
                
                <div class="detail-item">
                    <span class="detail-label">Data de Compra</span>
                    <span class="detail-value">
                        <fmt:formatDate value="${ingresso.dataCompra}" pattern="dd/MM/yyyy HH:mm:ss"/>
                    </span>
                </div>
                
                <c:if test="${isAdmin}">
                    <div class="detail-item">
                        <span class="detail-label">Comprador</span>
                        <span class="detail-value">${ingresso.comprador.nome}</span>
                    </div>
                    
                    <div class="detail-item">
                        <span class="detail-label">Email do Comprador</span>
                        <span class="detail-value">${ingresso.comprador.email}</span>
                    </div>
                </c:if>
            </div>
            
            <div class="details-section">
                <h2>Instruções de Uso</h2>
                <p>Este ingresso deve ser apresentado na entrada do zoológico, junto com um documento com foto. O ingresso é válido apenas para a data de hoje e não pode ser transferido para outra pessoa.</p>
                
                <c:if test="${ingresso.utilizado}">
                    <div class="alert alert-error" style="margin-top:1rem;">
                        <strong>Atenção:</strong> Este ingresso já foi utilizado.
                    </div>
                </c:if>
                
                <c:if test="${!ingresso.utilizado}">
                    <div class="alert alert-success" style="margin-top:1rem;">
                        <strong>Atenção:</strong> Este ingresso está disponível para uso.
                    </div>
                </c:if>
            </div>
            
            <c:if test="${!ingresso.utilizado && !isAdmin}">
                <div class="details-section" style="margin-top:2rem;text-align:center;">
                    <div style="margin-bottom:1rem;">
                        <div style="font-size:8rem;line-height:1;margin-bottom:1rem;">🎫</div>
                        <p>Apresente este ingresso na entrada do zoológico.</p>
                    </div>
                    
                    <form action="${pageContext.request.contextPath}/ingresso/utilizar" method="post">
                        <input type="hidden" name="id" value="${ingresso.id}">
                        <button type="submit" class="btn btn-primary btn-large" style="min-width:200px;">
                            Utilizar Agora
                        </button>
                    </form>
                </div>
            </c:if>
        </div>
    </div>
</t:master>