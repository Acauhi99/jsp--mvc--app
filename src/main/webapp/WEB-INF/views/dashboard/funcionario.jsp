<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Dashboard Manutenção">
    <div class="container main-content">
        <h1 class="section-title">Painel de Manutenção</h1>
        <div class="manutencao-dashboard">
            <div class="dashboard-summary">
                <div class="summary-card open">
                    <span class="summary-icon">🟠</span>
                    <div>
                        <div class="summary-title">Abertas</div>
                        <div class="summary-value">${abertas}</div>
                    </div>
                </div>
                <div class="summary-card andamento">
                    <span class="summary-icon">🟡</span>
                    <div>
                        <div class="summary-title">Em Andamento</div>
                        <div class="summary-value">${emAndamento}</div>
                    </div>
                </div>
                <div class="summary-card concluida">
                    <span class="summary-icon">🟢</span>
                    <div>
                        <div class="summary-title">Concluídas</div>
                        <div class="summary-value">${concluidas}</div>
                    </div>
                </div>
            </div>
            <div class="dashboard-actions">
                <a href="${pageContext.request.contextPath}/manutencao/novo" class="btn btn-primary">Nova Solicitação</a>
            </div>
            <c:if test="${not empty alertas}">
                <div class="alert alert-error" style="margin-top:2rem;">
                    <strong>Atenção:</strong>
                    <ul>
                        <c:forEach var="alerta" items="${alertas}">
                            <li>${alerta}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
        </div>
    </div>
</t:master>