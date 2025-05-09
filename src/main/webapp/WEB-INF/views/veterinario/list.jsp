<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:master title="Gerenciamento de Veterinários">
    <div class="container">
        <h1 class="section-title">Gerenciamento de Veterinários</h1>
        
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
            <a href="${pageContext.request.contextPath}/funcionario/novo?tipo=VETERINARIO" class="btn btn-primary">
                <i class="menu-icon">🩺</i> Adicionar Novo Veterinário
            </a>
        </div>
        
        <c:if test="${not empty param.mensagem}">
            <div class="success-message">${param.mensagem}</div>
        </c:if>
        <c:if test="${not empty param.erro}">
            <div class="error-message">${param.erro}</div>
        </c:if>
        
        <c:choose>
            <c:when test="${empty veterinarios}">
                <div class="alert alert-info">
                    Nenhum veterinário cadastrado no sistema.
                </div>
            </c:when>
            <c:otherwise>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="veterinario" items="${veterinarios}">
                            <tr>
                                <td>${veterinario.nome}</td>
                                <td>${veterinario.email}</td>
                                <td>
                                    <div class="action-buttons">
                                        <a href="${pageContext.request.contextPath}/funcionario/detalhes?id=${veterinario.id}" class="btn btn-sm btn-view action-btn">Ver</a>
                                        <a href="${pageContext.request.contextPath}/funcionario/editar?id=${veterinario.id}" class="btn btn-sm btn-edit action-btn">Editar</a>
                                        <form action="${pageContext.request.contextPath}/funcionario/excluir" method="post" style="display:inline;">
                                            <input type="hidden" name="id" value="${veterinario.id}">
                                            <input type="hidden" name="origem" value="/funcionario/veterinario">
                                            <button type="button" class="btn btn-sm btn-delete action-btn">Excluir</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
    <jsp:include page="/WEB-INF/components/confirmation-modal.jsp" />
</t:master>