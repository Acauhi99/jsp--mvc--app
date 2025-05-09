<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:master title="Cadastrar Novo Habitat">
    <div class="form-container">
        <div class="form-header" style="text-align:center;">
            <span class="ticket-emoji">🌍</span>
            <h1>Cadastrar Novo Habitat</h1>
        </div>
        
        <c:if test="${not empty erro}">
            <div class="error-message">${erro}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/habitat/novo" method="post" class="auth-form">
            <div class="form-section">
                <div class="form-grid">
                    <div class="form-group">
                        <label for="nome">Nome do Habitat *</label>
                        <input type="text" id="nome" name="nome" required maxlength="100" />
                    </div>
                    
                    <div class="form-group">
                        <label for="tipoAmbiente">Tipo de Ambiente *</label>
                        <select id="tipoAmbiente" name="tipoAmbiente" required>
                            <c:forEach var="tipo" items="${tiposAmbiente}">
                                <option value="${tipo}">${tipo}</option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="tamanho">Tamanho (m²)</label>
                        <input type="number" id="tamanho" name="tamanho" min="1" step="0.01" />
                    </div>
                    
                    <div class="form-group">
                        <label for="capacidadeMaximaAnimais">Capacidade Máxima de Animais</label>
                        <input type="number" id="capacidadeMaximaAnimais" name="capacidadeMaximaAnimais" min="1" />
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" id="publicoAcessivel" name="publicoAcessivel" />
                            Acessível ao Público
                        </label>
                    </div>
                </div>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-large btn-full">Cadastrar Habitat</button>
                <a href="${pageContext.request.contextPath}/habitat" class="btn btn-outline btn-full" style="margin-top:1rem;">Cancelar</a>
            </div>
        </form>
    </div>
</t:master>