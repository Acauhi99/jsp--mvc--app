<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:master title="Animais do Zoológico">
    <div class="container">
        <h1 class="section-title">Animais do Zoológico</h1>
        <form method="get" style="margin-bottom:2rem;display:flex;gap:1rem;flex-wrap:wrap;">
            <input type="text" name="especie" placeholder="Espécie" value="${especie != null ? especie : ''}" />
            <select name="classe">
                <option value="">Classe</option>
                <c:forEach var="c" items="${classes}">
                    <option value="${c}" <c:if test="${c == classe}">selected</c:if>>${c}</option>
                </c:forEach>
            </select>
            <select name="genero">
                <option value="">Gênero</option>
                <c:forEach var="g" items="${generos}">
                    <option value="${g}" <c:if test="${g == genero}">selected</c:if>>${g}</option>
                </c:forEach>
            </select>
            <select name="tipoAmbiente">
                <option value="">Tipo de Ambiente</option>
                <c:forEach var="t" items="${tiposAmbiente}">
                    <option value="${t}" <c:if test="${t == tipoAmbiente}">selected</c:if>>${t}</option>
                </c:forEach>
            </select>
            <button type="submit" class="btn btn-primary">Filtrar</button>
        </form>

        <c:if test="${sessionScope.role eq 'VETERINARIO' || sessionScope.role eq 'ADMINISTRADOR'}">
            <div style="margin-bottom:1.5rem;">
                <a href="${pageContext.request.contextPath}/animal/novo" class="btn btn-primary">
                    Registrar Animal
                </a>
            </div>
        </c:if>

        <div class="featured-grid">
            <c:choose>
                <c:when test="${empty animais}">
                    <div style="width:100%;text-align:center;font-size:1.2rem;padding:2rem;">
                        Nenhum animal encontrado 😢
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="animal" items="${animais}">
                        <div class="animal-card">
                            <div class="animal-image" style="display:flex;align-items:center;justify-content:center;font-size:2.5rem;">
                                🐾
                            </div>
                            <h3>${animal.nome}</h3>
                            <p><strong>Espécie:</strong> ${animal.especie}</p>
                            <p><strong>Habitat:</strong> ${animal.habitat.nome}</p>
                            <div class="animal-card-footer">
                                <a href="${pageContext.request.contextPath}/animal/detalhes?id=${animal.id}" class="btn btn-sm btn-outline">Ver detalhes</a>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
        <div style="margin-top:2rem;text-align:center;">
            <c:forEach begin="1" end="${totalPages}" var="i">
                <a href="?page=${i}&especie=${especie}&classe=${classe}&genero=${genero}&tipoAmbiente=${tipoAmbiente}" class="btn <c:if test='${i == page}'>btn-primary</c:if> btn-sm">${i}</a>
            </c:forEach>
        </div>
    </div>
</t:master>