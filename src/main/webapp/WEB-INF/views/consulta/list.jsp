<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Consultas Veterinárias">
    <div class="container">
        <h1 class="section-title">Consultas Veterinárias</h1>
        <form method="get" style="display:flex;gap:1rem;flex-wrap:wrap;margin-bottom:1.5rem;">
            <select name="status">
                <option value="">Status</option>
                <option value="AGENDADA" <c:if test="${param.status == 'AGENDADA'}">selected</c:if>>Agendada</option>
                <option value="EM_ANDAMENTO" <c:if test="${param.status == 'EM_ANDAMENTO'}">selected</c:if>>Em Andamento</option>
                <option value="CONCLUIDA" <c:if test="${param.status == 'CONCLUIDA'}">selected</c:if>>Concluída</option>
                <option value="CANCELADA" <c:if test="${param.status == 'CANCELADA'}">selected</c:if>>Cancelada</option>
            </select>
            <select name="animal">
                <option value="">Animal</option>
                <c:forEach var="a" items="${animais}">
                    <option value="${a.id}" <c:if test="${param.animal == a.id}">selected</c:if>>${a.nome}</option>
                </c:forEach>
            </select>
            <select name="tipo">
                <option value="">Tipo</option>
                <option value="ROTINA" <c:if test="${param.tipo == 'ROTINA'}">selected</c:if>>Rotina</option>
                <option value="EMERGENCIA" <c:if test="${param.tipo == 'EMERGENCIA'}">selected</c:if>>Emergência</option>
                <option value="TRATAMENTO" <c:if test="${param.tipo == 'TRATAMENTO'}">selected</c:if>>Tratamento</option>
                <option value="CIRURGIA" <c:if test="${param.tipo == 'CIRURGIA'}">selected</c:if>>Cirurgia</option>
                <option value="EXAME" <c:if test="${param.tipo == 'EXAME'}">selected</c:if>>Exame</option>
            </select>
            <select name="veterinario">
                <option value="">Veterinário</option>
                <c:forEach var="v" items="${veterinarios}">
                    <option value="${v.id}" <c:if test="${param.veterinario == v.id}">selected</c:if>>${v.nome}</option>
                </c:forEach>
            </select>
            <button type="submit" class="btn btn-primary">Filtrar</button>
        </form>
        <div class="table-actions">
            <a href="${pageContext.request.contextPath}/consulta/nova" class="btn btn-primary">Nova Consulta</a>
        </div>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Animal</th>
                    <th>Tipo</th>
                    <th>Status</th>
                    <th>Data/Hora</th>
                    <th>Veterinário</th>
                    <th>Acompanhamento</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${consultas}">
                    <tr>
                        <td>${c.animal.nome}</td>
                        <td>${c.tipoConsulta}</td>
                        <td>
                            <span class="badge badge-${c.status}">${c.status}</span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty c.dataHora}">
                                    ${fn:substring(c.dataHora, 8, 10)}/${fn:substring(c.dataHora, 5, 7)}/${fn:substring(c.dataHora, 0, 4)}
                                    ${fn:substring(c.dataHora, 11, 16)}
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty c.veterinario}">${c.veterinario.nome}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${c.acompanhamentoNecessario}">
                                    <span class="badge badge-PENDENTE">Necessário</span>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <a href="${pageContext.request.contextPath}/consulta/detalhes?id=${c.id}" 
                                   class="btn btn-sm btn-view action-btn">Ver</a>
                                <c:if test="${c.status != 'CONCLUIDA'}">
                                    <a href="${pageContext.request.contextPath}/consulta/editar?id=${c.id}" 
                                       class="btn btn-sm btn-edit action-btn">Editar</a>
                                </c:if>
                                <c:if test="${c.status == 'CONCLUIDA'}">
                                    <span class="action-placeholder"></span>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div style="margin-top:2rem;text-align:center;">
            <c:forEach begin="1" end="${totalPages}" var="i">
                <a href="?page=${i}&status=${param.status}&animal=${param.animal}&tipo=${param.tipo}&veterinario=${param.veterinario}"
                   class="btn <c:if test='${i == page}'>btn-primary</c:if> btn-sm">${i}</a>
            </c:forEach>
        </div>
    </div>
</t:master>