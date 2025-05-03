<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:master title="Comprar Ingresso">
    <div class="form-container">
        <div class="form-header">
            <h1>Comprar Ingresso</h1>
        </div>
        <form action="${pageContext.request.contextPath}/ingresso/comprar" method="post" class="auth-form">
            <div class="form-section">
                <div class="form-group">
                    <label for="tipo">Tipo de Ingresso</label>
                    <select name="tipo" id="tipo" required>
                        <option value="">Selecione</option>
                        <option value="ADULTO">Adulto</option>
                        <option value="CRIANCA">Criança</option>
                        <option value="IDOSO">Idoso</option>
                        <option value="ESTUDANTE">Estudante</option>
                        <option value="DEFICIENTE">Pessoa com Deficiência</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="quantidade">Quantidade</label>
                    <input type="number" id="quantidade" name="quantidade" min="1" max="10" value="1" required>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-large btn-full">Comprar</button>
            </div>
        </form>
        <div class="auth-hint">
            Após a compra, seus ingressos estarão disponíveis em <a href="${pageContext.request.contextPath}/ingresso">Meus Ingressos</a>.
        </div>
    </div>
</t:master>