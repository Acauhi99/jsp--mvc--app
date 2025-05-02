<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container main-content">
    <h1 class="section-title">Registros de Alimentação</h1>
    
    <div class="message-container">
        <c:if test="${not empty mensagem}">
            <div class="success-message">${mensagem}</div>
        </c:if>
        <c:if test="${not empty erro}">
            <div class="error-message">${erro}</div>
        </c:if>
    </div>
    
    <div class="actions-container" style="margin-bottom: 1.5rem;">
        <a href="${pageContext.request.contextPath}/alimentacao/novo" class="btn btn-primary">
            Nova Alimentação
        </a>
    </div>
    
    <c:if test="${empty alimentacoes}">
        <p class="info-text">Não há registros de alimentação cadastrados.</p>
    </c:if>
    
    <c:if test="${not empty alimentacoes}">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Animal</th>
                    <th>Tipo de Alimento</th>
                    <th>Quantidade</th>
                    <th>Data/Hora</th>
                    <th>Funcionário</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${alimentacoes}" var="alimentacao">
                    <tr>
                        <td>${alimentacao.animal.nome}</td>
                        <td>${alimentacao.tipoAlimento}</td>
                        <td>${alimentacao.quantidade} ${alimentacao.unidadeMedida}</td>
                        <td>
                            <fmt:parseDate value="${alimentacao.dataHora}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                            <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                        </td>
                        <td>${alimentacao.funcionarioResponsavel.nome}</td>
                        <td class="table-actions">
                            <a href="${pageContext.request.contextPath}/alimentacao/${alimentacao.id}" class="btn btn-sm btn-view">Ver</a>
                            <a href="${pageContext.request.contextPath}/alimentacao/editar/${alimentacao.id}" class="btn btn-sm btn-edit">Editar</a>
                            <form action="${pageContext.request.contextPath}/alimentacao" method="post" style="display: inline;">
                                <input type="hidden" name="_method" value="DELETE">
                                <input type="hidden" name="id" value="${alimentacao.id}">
                                <button type="submit" class="btn btn-sm btn-delete" onclick="return confirm('Tem certeza que deseja excluir este registro?')">Excluir</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>

<jsp:include page="../common/footer.jsp" />