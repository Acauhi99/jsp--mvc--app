<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Histórico Médico">
    <div class="container">
        <h1 class="section-title">Histórico Médico</h1>
        
        <form method="get" style="display:flex;gap:1rem;flex-wrap:wrap;margin-bottom:1.5rem;">
            <select name="animal" id="animalFilter" class="form-control" required>
                <option value="">Selecione um Animal</option>
                <c:forEach var="a" items="${animais}">
                    <option value="${a.id}" <c:if test="${param.animal == a.id}">selected</c:if>>${a.nome} (${a.nomeCientifico})</option>
                </c:forEach>
            </select>
            <button type="submit" class="btn btn-primary">Ver Histórico</button>
        </form>
        
        <c:if test="${not empty animal}">
            <div class="animal-info" style="background:#f5f5f5;padding:1.5rem;border-radius:8px;margin-bottom:2rem;">
                <h2 style="margin-bottom:1rem;color:var(--primary-color);">${animal.nome}</h2>
                <div style="display:grid;grid-template-columns:repeat(auto-fill, minmax(200px, 1fr));gap:1rem;">
                    <div>
                        <strong>Espécie:</strong> ${animal.especie}
                    </div>
                    <div>
                        <strong>Chegada ao Zoo:</strong> ${fn:substring(animal.dataChegada, 8, 10)}/${fn:substring(animal.dataChegada, 5, 7)}/${fn:substring(animal.dataChegada, 0, 4)}
                    </div>
                    <div>
                        <strong>Idade aproximada:</strong> ${animal.idade} anos
                    </div>
                    <div>
                        <strong>Habitat:</strong> ${animal.habitat.nome}
                    </div>
                </div>
            </div>
            
            <c:choose>
                <c:when test="${empty historicoConsultas}">
                    <div class="alert alert-info" style="padding:1rem;background:#e3f4eb;border-radius:4px;text-align:center;">
                        Nenhuma consulta registrada para este animal.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="timeline" style="position:relative;margin-left:20px;padding-left:20px;border-left:2px solid #ddd;">
                        <c:forEach var="consulta" items="${historicoConsultas}">
                            <div class="timeline-item" style="position:relative;margin-bottom:2rem;padding-bottom:1rem;">
                                <div class="timeline-marker" style="position:absolute;left:-31px;top:0;width:20px;height:20px;background:var(--primary-color);border-radius:50%;"></div>
                                <div class="timeline-date" style="font-weight:bold;margin-bottom:0.5rem;color:var(--primary-color);">
                                    ${fn:substring(consulta.dataHora, 8, 10)}/${fn:substring(consulta.dataHora, 5, 7)}/${fn:substring(consulta.dataHora, 0, 4)} - ${fn:substring(consulta.dataHora, 11, 16)}
                                </div>
                                <div class="timeline-content" style="background:white;padding:1rem;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1);">
                                    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:0.8rem;">
                                        <h3 style="margin:0;font-size:1.2rem;">${consulta.tipoConsulta}</h3>
                                        <span class="badge badge-${consulta.status}">${consulta.status}</span>
                                    </div>
                                    <div style="margin-bottom:0.8rem;">
                                        <strong>Veterinário:</strong> ${consulta.veterinario.nome}
                                    </div>
                                    <div style="margin-bottom:0.8rem;">
                                        <strong>Diagnóstico:</strong> ${fn:substring(consulta.diagnostico, 0, 100)}${fn:length(consulta.diagnostico) > 100 ? '...' : ''}
                                    </div>
                                    <a href="${pageContext.request.contextPath}/consulta/detalhes?id=${consulta.id}" class="btn btn-sm btn-primary">Ver Detalhes</a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>
    </div>
</t:master>