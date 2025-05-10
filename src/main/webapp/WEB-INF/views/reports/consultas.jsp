<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>

<t:master title="Relatório de Consultas Veterinárias">
    <div class="container">
        <h1 class="section-title">Relatório de Consultas Veterinárias</h1>
        
        <div class="report-card report-filters">
            <h3 class="report-title">Filtros</h3>
            
            <form method="get" action="${pageContext.request.contextPath}/relatorio/consultas" class="report-filters-form">
                <div class="form-field">
                    <label for="dataInicio">Data Início</label>
                    <input type="date" id="dataInicio" name="dataInicio" value="${dataInicio}" class="form-control">
                </div>
                
                <div class="form-field">
                    <label for="dataFim">Data Fim</label>
                    <input type="date" id="dataFim" name="dataFim" value="${dataFim}" class="form-control">
                </div>
                
                <div class="form-field">
                    <label for="veterinarioId">Veterinário</label>
                    <select id="veterinarioId" name="veterinarioId" class="form-control">
                        <option value="">Todos</option>
                        <c:forEach var="vet" items="${veterinarios}">
                            <option value="${vet.id}" ${param.veterinarioId eq vet.id.toString() ? 'selected' : ''}>${vet.nome}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-field">
                    <label for="tipoConsulta">Tipo de Consulta</label>
                    <select id="tipoConsulta" name="tipoConsulta" class="form-control">
                        <option value="">Todos</option>
                        <c:forEach var="tipo" items="${tiposConsulta}">
                            <option value="${tipo}" ${param.tipoConsulta eq tipo.toString() ? 'selected' : ''}>${tipo}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-field">
                    <label for="statusConsulta">Status</label>
                    <select id="statusConsulta" name="statusConsulta" class="form-control">
                        <option value="">Todos</option>
                        <c:forEach var="status" items="${statusConsulta}">
                            <option value="${status}" ${param.statusConsulta eq status.toString() ? 'selected' : ''}>${status}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-action">
                    <button type="submit" class="btn btn-primary btn-full">Filtrar</button>
                </div>
            </form>
        </div>
        
        <div class="report-dashboard">
            <div class="stat-card primary-border">
                <div class="stat-label">Total de Consultas</div>
                <div class="stat-value">${totalConsultas}</div>
            </div>
 
            <c:forEach var="entry" items="${consultasPorStatus}">
                <c:if test="${entry.value > 0}">
                    <div class="stat-card ${entry.key == 'AGENDADA' ? 'primary-border' : 
                                             entry.key == 'EM_ANDAMENTO' ? 'warning-border' : 
                                             entry.key == 'CONCLUIDA' ? 'success-border' : 
                                             entry.key == 'CANCELADA' ? 'danger-border' : 'default-border'}">
                        <div class="stat-label">${entry.key}</div>
                        <div class="stat-value">${entry.value}</div>
                    </div>
                </c:if>
            </c:forEach>
        </div>
        
        <div class="report-grid">
            <div class="report-grid-item">
                <h3 class="report-title">Consultas por Tipo</h3>
                
                <div class="progress-bar-container">
                    <c:forEach var="entry" items="${consultasPorTipo}">
                        <c:if test="${entry.value > 0}">
                            <div>
                                <div class="progress-item-header">
                                    <span>${entry.key}</span>
                                    <span>${entry.value} consultas</span>
                                </div>
                                <div class="progress-bar-bg">
                                    <div class="progress-bar progress-adulto" style="width:${(entry.value / totalConsultas) * 100}%;"></div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        
            <div class="report-grid-item">
                <h3 class="report-title">Consultas por Veterinário</h3>
                
                <div class="progress-bar-container">
                    <c:forEach var="entry" items="${consultasPorVeterinario}">
                        <div>
                            <div class="progress-item-header">
                                <span>${entry.key}</span>
                                <span>${entry.value} consultas</span>
                            </div>
                            <div class="progress-bar-bg">
                                <div class="progress-bar progress-estudante" style="width:${(entry.value / totalConsultas) * 100}%;"></div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
        
        <div class="report-card">
            <h3 class="report-subtitle">Listagem de Consultas</h3>
            
            <c:choose>
                <c:when test="${empty consultas}">
                    <div class="alert alert-info">Nenhuma consulta encontrada para os filtros selecionados.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Data/Hora</th>
                                    <th>Animal</th>
                                    <th>Veterinário</th>
                                    <th>Tipo</th>
                                    <th>Status</th>
                                    <th>Diagnóstico</th>
                                    <th>Acompanhamento</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="consulta" items="${consultas}">
                                    <tr>
                                        <td>
                                            <fmt:parseDate value="${consulta.dataHora}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                            <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </td>
                                        <td>${consulta.animal.nome}</td>
                                        <td>${consulta.veterinario.nome}</td>
                                        <td><span class="badge badge-${consulta.tipoConsulta}">${consulta.tipoConsulta}</span></td>
                                        <td><span class="badge badge-${consulta.status}">${consulta.status}</span></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${empty consulta.diagnostico}">-</c:when>
                                                <c:otherwise>
                                                    <span title="${consulta.diagnostico}">
                                                        ${consulta.diagnostico.length() > 30 ? consulta.diagnostico.substring(0, 30).concat('...') : consulta.diagnostico}
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${consulta.acompanhamentoNecessario ? 'Sim' : 'Não'}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
     
        <div class="insight-section">
            <h3 class="report-title">Insights</h3>
            
            <ul class="insight-list">
                <c:if test="${totalConsultas > 0}">
                    <c:if test="${consultasPorStatus['CANCELADA'] > 0}">
                        <li>
                            <strong>${consultasPorStatus['CANCELADA']}</strong> consultas foram canceladas (${Math.round((consultasPorStatus['CANCELADA'] / totalConsultas) * 100)}% do total).
                        </li>
                    </c:if>
                    
                    <c:set var="emergencias" value="${consultasPorTipo['EMERGENCIA']}" />
                    <c:if test="${emergencias > 0}">
                        <li>
                            <strong>${emergencias}</strong> consultas foram de emergência (${Math.round((emergencias / totalConsultas) * 100)}% do total).
                        </li>
                    </c:if>
                    
                    <li>
                        O tipo de consulta mais frequente foi <strong>
                        <c:set var="maxTipo" value="" />
                        <c:set var="maxValor" value="0" />
                        <c:forEach var="entry" items="${consultasPorTipo}">
                            <c:if test="${entry.value > maxValor}">
                                <c:set var="maxTipo" value="${entry.key}" />
                                <c:set var="maxValor" value="${entry.value}" />
                            </c:if>
                        </c:forEach>
                        ${maxTipo}</strong> com ${maxValor} ocorrências.
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</t:master>