<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Solicitações de Manutenção">
    <div class="container">
        <h1 class="section-title">Solicitações de Manutenção</h1>
        <form method="get" style="display:flex;gap:1rem;flex-wrap:wrap;margin-bottom:1.5rem;">
            <select name="status">
                <option value="">Status</option>
                <option value="PENDENTE">Pendente</option>
                <option value="EM_ANDAMENTO">Em Andamento</option>
                <option value="CONCLUIDA">Concluída</option>
                <option value="CANCELADA">Cancelada</option>
            </select>
            <select name="prioridade">
                <option value="">Prioridade</option>
                <option value="BAIXA">Baixa</option>
                <option value="MEDIA">Média</option>
                <option value="ALTA">Alta</option>
                <option value="URGENTE">Urgente</option>
            </select>
            <select name="habitat">
                <option value="">Habitat</option>
                <c:forEach var="h" items="${habitats}">
                    <option value="${h.id}" <c:if test="${param.habitat == h.id}">selected</c:if>>${h.nome}</option>
                </c:forEach>
            </select>
            <select name="responsavel">
                <option value="">Responsável</option>
                <c:forEach var="f" items="${manutentores}">
                    <option value="${f.id}" <c:if test="${param.responsavel == f.id}">selected</c:if>>${f.nome}</option>
                </c:forEach>
            </select>
            <button type="submit" class="btn btn-primary">Filtrar</button>
        </form>
        <div class="table-actions">
            <a href="${pageContext.request.contextPath}/manutencao/novo" class="btn btn-primary">Nova Solicitação</a>
        </div>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Habitat</th>
                    <th>Tipo de Serviço</th>
                    <th>Status</th>
                    <th>Prioridade</th>
                    <th>Data de Solicitação</th>
                    <th>Responsável</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="m" items="${manutencoes}">
                    <tr>
                        <td>${m.habitat.nome}</td>
                        <td>${m.tipoManutencao}</td>
                        <td>
                            <span class="badge badge-${m.status}">${m.status}</span>
                        </td>
                        <td>
                            <span class="badge badge-prio-${m.prioridade}">${m.prioridade}</span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty m.dataSolicitacao}">
                                    ${fn:substring(m.dataSolicitacao, 8, 10)}/${fn:substring(m.dataSolicitacao, 5, 7)}/${fn:substring(m.dataSolicitacao, 0, 4)}
                                    ${fn:substring(m.dataSolicitacao, 11, 16)}
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty m.responsavel}">${m.responsavel.nome}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <a href="${pageContext.request.contextPath}/manutencao/detalhes?id=${m.id}" 
                                   class="btn btn-sm btn-view action-btn">Ver</a>
                                <c:if test="${m.status != 'CONCLUIDA'}">
                                    <a href="${pageContext.request.contextPath}/manutencao/editar?id=${m.id}" 
                                       class="btn btn-sm btn-edit action-btn">Editar</a>
                                </c:if>
                                <c:if test="${m.status == 'CONCLUIDA'}">
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
                <a href="?page=${i}&status=${param.status}&prioridade=${param.prioridade}&habitat=${param.habitat}"
                   class="btn <c:if test='${i == page}'>btn-primary</c:if> btn-sm">${i}</a>
            </c:forEach>
        </div>
    </div>
</t:master>