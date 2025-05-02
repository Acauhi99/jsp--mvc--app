<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container main-content">
    <a href="${pageContext.request.contextPath}/alimentacao" class="back-link">
        ← Voltar para a lista
    </a>

    <div class="form-container">
        <div class="form-header">
            <h1>${alimentacao != null ? 'Editar' : 'Novo'} Registro de Alimentação</h1>
        </div>
        
        <c:if test="${not empty erro}">
            <div class="error-message">${erro}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/alimentacao" method="post">
            <c:if test="${alimentacao != null}">
                <input type="hidden" name="id" value="${alimentacao.id}">
            </c:if>
            
            <div class="form-section">
                <h2>Informações da Alimentação</h2>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="animalId">Animal *</label>
                        <select id="animalId" name="animalId" required>
                            <option value="">Selecione um animal</option>
                            <c:forEach items="${animais}" var="animal">
                                <option value="${animal.id}" ${alimentacao != null && alimentacao.animal.id eq animal.id ? 'selected' : ''}>
                                    ${animal.nome} (${animal.especie})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="tipoAlimento">Tipo de Alimento *</label>
                        <input type="text" id="tipoAlimento" name="tipoAlimento" 
                               value="${alimentacao != null ? alimentacao.tipoAlimento : ''}" required>
                    </div>
                </div>
                
                <div class="form-grid">
                    <div class="form-group">
                        <label for="quantidade">Quantidade *</label>
                        <input type="number" id="quantidade" name="quantidade" 
                               value="${alimentacao != null ? alimentacao.quantidade : ''}" 
                               step="0.01" min="0" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="unidadeMedida">Unidade de Medida</label>
                        <input type="text" id="unidadeMedida" name="unidadeMedida" 
                               value="${alimentacao != null ? alimentacao.unidadeMedida : 'kg'}" 
                               placeholder="Ex: kg, g, unidade, porção">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="funcionarioId">Funcionário Responsável *</label>
                    <select id="funcionarioId" name="funcionarioId" required>
                        <option value="">Selecione um funcionário</option>
                        <c:forEach items="${funcionarios}" var="funcionario">
                            <option value="${funcionario.id}" ${alimentacao != null && alimentacao.funcionarioResponsavel.id eq funcionario.id ? 'selected' : ''}>
                                ${funcionario.nome} (${funcionario.cargo})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="observacoes">Observações</label>
                    <textarea id="observacoes" name="observacoes" rows="4">${alimentacao != null ? alimentacao.observacoes : ''}</textarea>
                </div>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Salvar</button>
                <a href="${pageContext.request.contextPath}/alimentacao" class="btn btn-outline">Cancelar</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />