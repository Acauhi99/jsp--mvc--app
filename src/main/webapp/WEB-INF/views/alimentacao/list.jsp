<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Registro de Alimentação">
    <div class="container">
        <h1 class="section-title">Registro de Alimentação</h1>
        
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
            <form method="get" style="display: flex; gap: 1rem; flex-wrap: wrap; max-width: 800px;">
                <select name="animalId" class="form-control">
                    <option value="">Todos os Animais</option>
                    <c:forEach var="animal" items="${animais}">
                        <option value="${animal.id}" ${param.animalId == animal.id ? 'selected' : ''}>${animal.nome}</option>
                    </c:forEach>
                </select>
                <button type="submit" class="btn btn-primary">Filtrar</button>
            </form>
            
            <a href="${pageContext.request.contextPath}/alimentacao/novo" class="btn btn-primary">
                <i class="menu-icon"></i> Agendar Alimentação
            </a>
        </div>
        
        <c:if test="${not empty mensagem}">
            <div class="success-message">${mensagem}</div>
        </c:if>
        <c:if test="${not empty erro}">
            <div class="error-message">${erro}</div>
        </c:if>
        
        <table class="data-table">
            <thead>
                <tr>
                    <th>Animal</th>
                    <th>Tipo de Alimento</th>
                    <th>Quantidade</th>
                    <th>Data/Hora</th>
                    <th>Responsável</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="alimentacao" items="${alimentacoes}">
                    <tr>
                        <td>${alimentacao.animal.nome}</td>
                        <td>${alimentacao.tipoAlimento}</td>
                        <td>${alimentacao.quantidade} ${alimentacao.unidadeMedida}</td>
                        <td>
                            ${fn:substring(alimentacao.dataHora, 8, 10)}/${fn:substring(alimentacao.dataHora, 5, 7)}/${fn:substring(alimentacao.dataHora, 0, 4)}
                            ${fn:substring(alimentacao.dataHora, 11, 16)}
                        </td>
                        <td>${alimentacao.funcionarioResponsavel.nome}</td>
                        <td>
                            <div class="action-buttons">
                                <a href="${pageContext.request.contextPath}/alimentacao/${alimentacao.id}" class="btn btn-sm btn-view action-btn">Ver</a>
                                <a href="${pageContext.request.contextPath}/alimentacao/editar/${alimentacao.id}" class="btn btn-sm btn-edit action-btn">Editar</a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                
                <c:if test="${empty alimentacoes}">
                    <tr>
                        <td colspan="6" style="text-align: center;">Nenhum registro de alimentação encontrado.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</t:master>