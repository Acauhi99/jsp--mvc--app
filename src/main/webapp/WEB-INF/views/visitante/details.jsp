<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<% pageContext.setAttribute("formatter", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")); %>
<t:master title="Detalhes do Visitante">
    <div class="container">
        <a href="${pageContext.request.contextPath}/customer" class="back-link">&larr; Voltar para lista de visitantes</a>
        
        <div class="details-container">
            <div class="details-header">
                <h1 class="details-title">${visitante.nome}</h1>
            </div>
            
            <div class="details-section">
                <div class="detail-item" style="text-align:center;margin-bottom:2rem;">
                    <div class="profile-avatar" style="margin:0 auto;">👤</div>
                </div>
                
                <div class="details-section details-grid">
                    <div class="detail-item">
                        <span class="detail-label">ID</span>
                        <span class="detail-value">${visitante.id}</span>
                    </div>
                    
                    <div class="detail-item">
                        <span class="detail-label">Nome</span>
                        <span class="detail-value">${visitante.nome}</span>
                    </div>
                    
                    <div class="detail-item">
                        <span class="detail-label">Email</span>
                        <span class="detail-value">${visitante.email}</span>
                    </div>
                    
                    <div class="detail-item">
                        <span class="detail-label">Total de Ingressos</span>
                        <span class="detail-value">${visitante.ingressosAdquiridos.size()}</span>
                    </div>
                </div>
            </div>
            
            <div class="details-section">
                <h2>Ingressos Adquiridos</h2>
                <c:choose>
                    <c:when test="${empty visitante.ingressosAdquiridos}">
                        <p>Este visitante ainda não adquiriu nenhum ingresso.</p>
                    </c:when>
                    <c:otherwise>
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Tipo</th>
                                    <th>Data de Compra</th>
                                    <th>Valor</th>
                                    <th>Status</th>
                                    <th>Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="ingresso" items="${visitante.ingressosAdquiridos}">
                                    <tr>
                                        <td>${ingresso.tipo}</td>
                                        <td>
                                            <c:if test="${not empty ingresso.dataCompra}">
                                                ${ingresso.dataCompra.format(formatter)}
                                            </c:if>
                                        </td>
                                        <td>R$ ${ingresso.valor}</td>
                                        <td>
                                            <span class="badge badge-${ingresso.utilizado ? 'CANCELADA' : 'CONCLUIDA'}">
                                                ${ingresso.utilizado ? 'Utilizado' : 'Disponível'}
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/ingresso/detalhes?id=${ingresso.id}" class="btn btn-sm btn-view">Ver</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</t:master>