<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:master title="Detalhes do Veterinário">
    <div class="container">
        <a href="${pageContext.request.contextPath}/funcionario/veterinario" class="back-link">&larr; Voltar para lista de veterinários</a>
        
        <div class="details-container">
            <div class="details-header">
                <h1 class="details-title">${funcionario.nome}</h1>
                <div class="details-actions">
                    <a href="${pageContext.request.contextPath}/funcionario/editar?id=${funcionario.id}" class="btn btn-primary">Editar</a>
                    <form action="${pageContext.request.contextPath}/funcionario/excluir" method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${funcionario.id}">
                        <input type="hidden" name="origem" value="/funcionario/veterinario">
                        <button type="button" class="btn btn-delete">Excluir</button>
                    </form>
                </div>
            </div>
            
            <div class="details-section">
                <div class="detail-item" style="text-align:center;margin-bottom:2rem;">
                    <div class="profile-avatar" style="margin:0 auto;">🩺</div>
                </div>
                
                <div class="details-section details-grid">
                    <div class="detail-item">
                        <span class="detail-label">ID</span>
                        <span class="detail-value">${funcionario.id}</span>
                    </div>
                    
                    <div class="detail-item">
                        <span class="detail-label">Nome</span>
                        <span class="detail-value">${funcionario.nome}</span>
                    </div>
                    
                    <div class="detail-item">
                        <span class="detail-label">Email</span>
                        <span class="detail-value">${funcionario.email}</span>
                    </div>
                    
                    <div class="detail-item">
                        <span class="detail-label">Cargo</span>
                        <span class="detail-value">Veterinário</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <jsp:include page="/WEB-INF/components/confirmation-modal.jsp" />
</t:master>