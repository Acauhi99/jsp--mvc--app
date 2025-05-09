<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>

<t:master title="Relatório de Consultas Veterinárias">
    <div class="container">
        <h1 class="section-title">Relatório de Consultas Veterinárias</h1>
        
        <!-- Filtros -->
        <div class="card" style="padding:1.5rem; margin-bottom:2rem; background:#fff; border-radius:8px; box-shadow:0 2px 4px rgba(0,0,0,0.1);">
            <h3 style="margin-bottom:1rem; color:#2c3e50; font-size:1.3rem;">Filtros</h3>
            
            <form method="get" action="${pageContext.request.contextPath}/relatorio/consultas" style="display:flex; flex-wrap:wrap; gap:1rem;">
                <div style="flex:1 1 200px;">
                    <label for="dataInicio" style="display:block; margin-bottom:0.5rem; font-weight:500;">Data Início</label>
                    <input type="date" id="dataInicio" name="dataInicio" value="${dataInicio}" class="form-control">
                </div>
                
                <div style="flex:1 1 200px;">
                    <label for="dataFim" style="display:block; margin-bottom:0.5rem; font-weight:500;">Data Fim</label>
                    <input type="date" id="dataFim" name="dataFim" value="${dataFim}" class="form-control">
                </div>
                
                <div style="flex:1 1 200px;">
                    <label for="veterinarioId" style="display:block; margin-bottom:0.5rem; font-weight:500;">Veterinário</label>
                    <select id="veterinarioId" name="veterinarioId" class="form-control">
                        <option value="">Todos</option>
                        <c:forEach var="vet" items="${veterinarios}">
                            <option value="${vet.id}" ${param.veterinarioId eq vet.id.toString() ? 'selected' : ''}>${vet.nome}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div style="flex:1 1 200px;">
                    <label for="tipoConsulta" style="display:block; margin-bottom:0.5rem; font-weight:500;">Tipo de Consulta</label>
                    <select id="tipoConsulta" name="tipoConsulta" class="form-control">
                        <option value="">Todos</option>
                        <c:forEach var="tipo" items="${tiposConsulta}">
                            <option value="${tipo}" ${param.tipoConsulta eq tipo.toString() ? 'selected' : ''}>${tipo}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div style="flex:1 1 200px;">
                    <label for="statusConsulta" style="display:block; margin-bottom:0.5rem; font-weight:500;">Status</label>
                    <select id="statusConsulta" name="statusConsulta" class="form-control">
                        <option value="">Todos</option>
                        <c:forEach var="status" items="${statusConsulta}">
                            <option value="${status}" ${param.statusConsulta eq status.toString() ? 'selected' : ''}>${status}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div style="flex:1 1 100px; display:flex; align-items:flex-end;">
                    <button type="submit" class="btn btn-primary" style="width:100%;">Filtrar</button>
                </div>
            </form>
        </div>
        
        <!-- Resumo de Estatísticas -->
        <div style="display:flex; flex-wrap:wrap; gap:1.5rem; margin-bottom:2rem;">
            <div style="flex:1 1 220px; background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1); border-left:5px solid #3498db;">
                <div style="font-size:1.1rem; color:#666; margin-bottom:0.5rem;">Total de Consultas</div>
                <div style="font-size:2.5rem; font-weight:bold; color:#2c3e50;">${totalConsultas}</div>
            </div>
            
            <!-- Consultas por Status -->
            <c:forEach var="entry" items="${consultasPorStatus}">
                <c:if test="${entry.value > 0}">
                    <div style="flex:1 1 220px; background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1); 
                        border-left:5px solid 
                        <c:choose>
                            <c:when test="${entry.key == 'AGENDADA'}">#3498db</c:when>
                            <c:when test="${entry.key == 'EM_ANDAMENTO'}">#f39c12</c:when>
                            <c:when test="${entry.key == 'CONCLUIDA'}">#2ecc71</c:when>
                            <c:when test="${entry.key == 'CANCELADA'}">#e74c3c</c:when>
                            <c:otherwise>#95a5a6</c:otherwise>
                        </c:choose>
                        ;">
                        <div style="font-size:1.1rem; color:#666; margin-bottom:0.5rem;">${entry.key}</div>
                        <div style="font-size:2.5rem; font-weight:bold; color:#2c3e50;">${entry.value}</div>
                    </div>
                </c:if>
            </c:forEach>
        </div>
        
        <!-- Distribuição por Tipo -->
        <div style="display:flex; flex-wrap:wrap; gap:2rem; margin-bottom:2rem;">
            <div style="flex:1 1 400px; min-width:300px; background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1);">
                <h3 style="margin-bottom:1rem; color:#2c3e50; font-size:1.3rem;">Consultas por Tipo</h3>
                
                <div>
                    <c:forEach var="entry" items="${consultasPorTipo}">
                        <c:if test="${entry.value > 0}">
                            <div style="margin-bottom:1rem;">
                                <div style="display:flex; justify-content:space-between; margin-bottom:0.3rem;">
                                    <span>${entry.key}</span>
                                    <span>${entry.value} consultas</span>
                                </div>
                                <div style="height:10px; background:#eee; border-radius:5px; overflow:hidden;">
                                    <div style="height:100%; background:#3498db; width:${(entry.value / totalConsultas) * 100}%;"></div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
            
            <!-- Consultas por Veterinário -->
            <div style="flex:1 1 400px; min-width:300px; background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1);">
                <h3 style="margin-bottom:1rem; color:#2c3e50; font-size:1.3rem;">Consultas por Veterinário</h3>
                
                <div>
                    <c:forEach var="entry" items="${consultasPorVeterinario}">
                        <div style="margin-bottom:1rem;">
                            <div style="display:flex; justify-content:space-between; margin-bottom:0.3rem;">
                                <span>${entry.key}</span>
                                <span>${entry.value} consultas</span>
                            </div>
                            <div style="height:10px; background:#eee; border-radius:5px; overflow:hidden;">
                                <div style="height:100%; background:#e67e22; width:${(entry.value / totalConsultas) * 100}%;"></div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
        
        <!-- Lista de Consultas -->
        <div style="background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1); margin-bottom:2rem;">
            <h3 style="margin-bottom:1.5rem; color:#2c3e50; font-size:1.3rem;">Listagem de Consultas</h3>
            
            <c:choose>
                <c:when test="${empty consultas}">
                    <div class="alert alert-info">Nenhuma consulta encontrada para os filtros selecionados.</div>
                </c:when>
                <c:otherwise>
                    <div style="overflow-x:auto;">
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
        
        <!-- Insights e Recomendações -->
        <div style="background:#f1f9ff; border-radius:8px; padding:1.5rem; margin-bottom:2rem; border-left:5px solid #3498db;">
            <h3 style="margin-bottom:1rem; color:#2c3e50; font-size:1.3rem;">Insights</h3>
            
            <ul style="list-style-type:circle; margin-left:1.5rem;">
                <c:if test="${totalConsultas > 0}">
                    <c:if test="${consultasPorStatus['CANCELADA'] > 0}">
                        <li style="margin-bottom:0.5rem;">
                            <strong>${consultasPorStatus['CANCELADA']}</strong> consultas foram canceladas (${Math.round((consultasPorStatus['CANCELADA'] / totalConsultas) * 100)}% do total).
                        </li>
                    </c:if>
                    
                    <c:set var="emergencias" value="${consultasPorTipo['EMERGENCIA']}" />
                    <c:if test="${emergencias > 0}">
                        <li style="margin-bottom:0.5rem;">
                            <strong>${emergencias}</strong> consultas foram de emergência (${Math.round((emergencias / totalConsultas) * 100)}% do total).
                        </li>
                    </c:if>
                    
                    <li style="margin-bottom:0.5rem;">
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