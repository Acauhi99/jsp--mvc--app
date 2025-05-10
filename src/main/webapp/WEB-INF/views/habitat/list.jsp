<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:master title="Habitats do Zoológico">
    <div class="container">
        <h1 class="section-title">Habitats do Zoológico</h1>
        <form method="get" style="margin-bottom:2rem;display:flex;gap:1rem;flex-wrap:wrap;">
            <select name="tipoAmbiente">
                <option value="">Tipo de Ambiente</option>
                <c:forEach var="t" items="${tiposAmbiente}">
                    <option value="${t}" <c:if test="${t == tipoAmbiente}">selected</c:if>>${t}</option>
                </c:forEach>
            </select>
            <button type="submit" class="btn btn-primary">Filtrar</button>
        </form>

        <c:if test="${sessionScope.role eq 'ADMINISTRADOR'}">
            <div style="margin-bottom:1.5rem;">
                <a href="${pageContext.request.contextPath}/habitat/novo" class="btn btn-primary">
                    <i class="menu-icon"></i> Cadastrar Novo Habitat
                </a>
            </div>
        </c:if>
        
        <div class="featured-grid">
            <c:choose>
                <c:when test="${empty habitats}">
                    <div style="width:100%;text-align:center;font-size:1.2rem;padding:2rem;">
                        Nenhum habitat encontrado 😢
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="habitat" items="${habitats}">
                        <div class="animal-card habitat-card">
                            <div class="habitat-image">
                                <span class="habitat-icon">🌍</span>
                            </div>
                            <h3>${habitat.nome}</h3>
                            <p><strong>Tipo de Ambiente:</strong> ${habitat.tipoAmbiente}</p>
                            <p><strong>Tamanho:</strong> ${habitat.tamanho} m²</p>
                            <p><strong>Capacidade Máxima:</strong> ${habitat.capacidadeMaximaAnimais} animais</p>
                            <p><strong>Público Acessível:</strong>
                                <c:choose>
                                    <c:when test="${habitat.publicoAcessivel}">Sim</c:when>
                                    <c:otherwise>Não</c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</t:master>