<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="${empty alimentacao ? 'Registrar Nova Alimentação' : 'Editar Registro de Alimentação'}">
    <div class="form-container">
        <div class="form-header" style="text-align:center;">
            <span class="ticket-emoji">🍽️</span>
            <h1>${empty alimentacao ? 'Registrar Nova Alimentação' : 'Editar Registro de Alimentação'}</h1>
        </div>
        
        <c:if test="${not empty erro}">
            <div class="error-message">${erro}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/alimentacao" method="post" class="auth-form">
            <c:if test="${not empty alimentacao}">
                <input type="hidden" name="id" value="${alimentacao.id}">
            </c:if>
            
            <div class="form-section">
                <h2>Informações Básicas</h2>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="animalId">Animal</label>
                        <select id="animalId" name="animalId" required>
                            <option value="">Selecione o animal</option>
                            <c:forEach var="animal" items="${animais}">
                                <option value="${animal.id}" ${not empty alimentacao && alimentacao.animal.id == animal.id ? 'selected' : ''}>
                                    ${animal.nome} (${animal.especie})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="tipoAlimento">Tipo de Alimento</label>
                        <input type="text" id="tipoAlimento" name="tipoAlimento" required 
                               value="${not empty alimentacao ? alimentacao.tipoAlimento : ''}">
                    </div>
                </div>
                
                <div class="form-grid">
                    <div class="form-group">
                        <label for="quantidade">Quantidade</label>
                        <input type="number" id="quantidade" name="quantidade" step="0.01" required 
                               value="${not empty alimentacao ? alimentacao.quantidade : ''}">
                    </div>
                    
                    <div class="form-group">
                        <label for="unidadeMedida">Unidade de Medida</label>
                        <select id="unidadeMedida" name="unidadeMedida" required>
                            <option value="">Selecione</option>
                            <option value="kg" ${not empty alimentacao && alimentacao.unidadeMedida == 'kg' ? 'selected' : ''}>Kilograma (kg)</option>
                            <option value="g" ${not empty alimentacao && alimentacao.unidadeMedida == 'g' ? 'selected' : ''}>Grama (g)</option>
                            <option value="L" ${not empty alimentacao && alimentacao.unidadeMedida == 'L' ? 'selected' : ''}>Litro (L)</option>
                            <option value="ml" ${not empty alimentacao && alimentacao.unidadeMedida == 'ml' ? 'selected' : ''}>Mililitro (ml)</option>
                            <option value="unidade" ${not empty alimentacao && alimentacao.unidadeMedida == 'unidade' ? 'selected' : ''}>Unidade</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="funcionarioId">Funcionário Responsável</label>
                    <select id="funcionarioId" name="funcionarioId" required>
                        <option value="">Selecione o responsável</option>
                        <c:forEach var="funcionario" items="${funcionarios}">
                            <c:if test="${funcionario.cargo == 'VETERINARIO'}">
                                <option value="${funcionario.id}" 
                                    ${not empty alimentacao && alimentacao.funcionarioResponsavel.id == funcionario.id ? 'selected' : ''}
                                    ${empty alimentacao && sessionScope.userId == funcionario.id ? 'selected' : ''}>
                                    ${funcionario.nome}
                                </option>
                            </c:if>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="observacoes">Observações</label>
                    <textarea id="observacoes" name="observacoes" rows="4">${not empty alimentacao ? alimentacao.observacoes : ''}</textarea>
                </div>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-large btn-full">
                    ${empty alimentacao ? 'Registrar Alimentação' : 'Atualizar Registro'}
                </button>
                
                <a href="${pageContext.request.contextPath}/alimentacao" class="btn btn-outline btn-full" style="margin-top: 1rem;">
                    Cancelar
                </a>
            </div>
        </form>
    </div>
</t:master>