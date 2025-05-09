<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:master title="Meus Ingressos">
    <div class="container">
        <h1 class="section-title">Meus Ingressos</h1>
        <form method="get" style="margin-bottom:2rem;display:flex;gap:1rem;flex-wrap:wrap;">
            <label>
                Status:
                <select name="status">
                    <option value="">Todos</option>
                    <option value="UTILIZADO" ${status == 'UTILIZADO' ? 'selected' : ''}>Utilizado</option>
                    <option value="DISPONIVEL" ${status == 'DISPONIVEL' ? 'selected' : ''}>Disponível</option>
                </select>
            </label>
            <label>
                Preço:
                <select name="precoOrder">
                    <option value="">-</option>
                    <option value="asc" ${precoOrder == 'asc' ? 'selected' : ''}>Menor &uarr;</option>
                    <option value="desc" ${precoOrder == 'desc' ? 'selected' : ''}>Maior &darr;</option>
                </select>
            </label>
            <label>
                Data:
                <select name="dataOrder">
                    <option value="">-</option>
                    <option value="asc" ${dataOrder == 'asc' ? 'selected' : ''}>Mais antiga &uarr;</option>
                    <option value="desc" ${dataOrder == 'desc' ? 'selected' : ''}>Mais recente &darr;</option>
                </select>
            </label>
            <button type="submit" class="btn btn-primary">Filtrar</button>
        </form>
        <c:choose>
            <c:when test="${empty ingressos}">
                <div class="alert alert-error">Nenhum ingresso encontrado.</div>
            </c:when>
            <c:otherwise>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Tipo</th>
                            <c:if test="${isAdmin}"><th>Comprador</th></c:if>
                            <th>Valor</th>
                            <th>Data da Compra</th>
                            <th>Status</th>
                            <c:if test="${isAdmin}"><th>Ações</th></c:if>
                            <c:if test="${empty isAdmin}">
                                <th>Ações</th>
                            </c:if>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="ingresso" items="${ingressos}">
                            <tr>
                                <td>
                                    <c:choose>
                                        <c:when test="${ingresso.tipo eq 'ADULTO'}">Adulto</c:when>
                                        <c:when test="${ingresso.tipo eq 'CRIANCA'}">Criança</c:when>
                                        <c:when test="${ingresso.tipo eq 'IDOSO'}">Idoso</c:when>
                                        <c:when test="${ingresso.tipo eq 'ESTUDANTE'}">Estudante</c:when>
                                        <c:when test="${ingresso.tipo eq 'DEFICIENTE'}">Pessoa com Deficiência</c:when>
                                        <c:otherwise>${ingresso.tipo}</c:otherwise>
                                    </c:choose>
                                </td>
                                <c:if test="${isAdmin}">
                                    <td>${ingresso.comprador.nome}</td>
                                </c:if>
                                <td>
                                    <fmt:formatNumber value="${ingresso.valor}" type="currency" currencySymbol="R$ " minFractionDigits="2"/>
                                </td>
                                <td>
                                    <fmt:formatDate value="${ingresso.dataCompra}" pattern="dd/MM/yyyy HH:mm"/>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${ingresso.utilizado}">
                                            <span class="error-message">Utilizado</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="alert-success">Disponível</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <c:if test="${isAdmin}">
                                    <td>
                                        <div class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/ingresso/detalhes?id=${ingresso.id}" class="btn btn-sm btn-view action-btn">Ver</a>
                                        </div>
                                    </td>
                                </c:if>
                                <c:if test="${empty isAdmin}">
                                    <td>
                                        <div class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/ingresso/detalhes?id=${ingresso.id}" class="btn btn-sm btn-view action-btn">Ver</a>
                                        </div>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div style="margin:2rem 0;display:flex;gap:0.5rem;justify-content:center;align-items:center;">
                    <c:if test="${page > 1}">
                        <a href="?page=${page-1}&precoOrder=${precoOrder}&dataOrder=${dataOrder}&status=${status}" class="btn btn-outline btn-sm">&laquo; Anterior</a>
                    </c:if>
                    <span style="padding:0 1rem;">Página ${page} de ${totalPages}</span>
                    <c:if test="${page < totalPages}">
                        <a href="?page=${page+1}&precoOrder=${precoOrder}&dataOrder=${dataOrder}&status=${status}" class="btn btn-outline btn-sm">Próxima &raquo;</a>
                    </c:if>
                </div>
            </c:otherwise>
        </c:choose>
        <a href="${pageContext.request.contextPath}/ingresso/comprar" class="btn btn-primary btn-large" style="margin-top:2rem;">Comprar Novo Ingresso</a>
    </div>
</t:master>