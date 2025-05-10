<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>

<t:master title="Relatório de Vendas de Ingressos">
    <div class="container">
        <h1 class="section-title">Relatório de Vendas de Ingressos</h1>
        
        <div class="report-card report-filters">
            <h3 class="report-title">Filtros</h3>
            
            <form method="get" action="${pageContext.request.contextPath}/relatorio/vendas" class="report-filters-form">
                <div class="form-field">
                    <label for="dataInicio">Data Início</label>
                    <input type="date" id="dataInicio" name="dataInicio" value="${dataInicio}" class="form-control">
                </div>
                
                <div class="form-field">
                    <label for="dataFim">Data Fim</label>
                    <input type="date" id="dataFim" name="dataFim" value="${dataFim}" class="form-control">
                </div>
                
                <div class="form-field">
                    <label for="tipoIngresso">Tipo de Ingresso</label>
                    <select id="tipoIngresso" name="tipoIngresso" class="form-control">
                        <option value="">Todos</option>
                        <c:forEach var="tipo" items="${tiposIngresso}">
                            <option value="${tipo}" ${param.tipoIngresso eq tipo.toString() ? 'selected' : ''}>${tipo}</option>
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
                <div class="stat-label">Total de Ingressos</div>
                <div class="stat-value">${totalIngressos}</div>
            </div>
            
            <div class="stat-card success-border">
                <div class="stat-label">Valor Total</div>
                <div class="stat-value">
                    <fmt:formatNumber value="${valorTotal}" type="currency" currencySymbol="R$" />
                </div>
            </div>
            
            <div class="stat-card warning-border">
                <div class="stat-label">Ticket Médio</div>
                <div class="stat-value">
                    <fmt:formatNumber value="${totalIngressos > 0 ? valorTotal / totalIngressos : 0}" type="currency" currencySymbol="R$" />
                </div>
            </div>
            
            <div class="stat-card info-border">
                <div class="stat-label">Ingressos Utilizados</div>
                <div class="stat-value">
                    ${ingressosUtilizados} (${Math.round(percentualUtilizados)}%)
                </div>
            </div>
        </div>
        
        <div class="report-grid">
            <div class="report-grid-item">
                <h3 class="report-title">Vendas por Tipo de Ingresso</h3>
                
                <table class="report-table">
                    <thead>
                        <tr>
                            <th>Tipo</th>
                            <th>Quantidade</th>
                            <th>Valor Total</th>
                            <th>Média</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="tipo" items="${tiposIngresso}">
                            <c:set var="ingressosTipo" value="${ingressosPorTipo[tipo]}" />
                            <c:set var="estatisticas" value="${estatisticasPorTipo[tipo]}" />
                            
                            <c:if test="${not empty ingressosTipo}">
                                <tr>
                                    <td>${tipo}</td>
                                    <td>${ingressosTipo.size()}</td>
                                    <td>
                                        <fmt:formatNumber value="${estatisticas.sum}" type="currency" currencySymbol="R$" />
                                    </td>
                                    <td>
                                        <fmt:formatNumber value="${estatisticas.average}" type="currency" currencySymbol="R$" />
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            
            <div class="report-grid-item">
                <h3 class="report-title">Distribuição das Vendas</h3>
                
                <div class="progress-bar-container">
                    <c:forEach var="tipo" items="${tiposIngresso}">
                        <c:set var="ingressosTipo" value="${ingressosPorTipo[tipo]}" />
                        <c:set var="estatisticas" value="${estatisticasPorTipo[tipo]}" />
                        
                        <c:if test="${not empty ingressosTipo}">
                            <div>
                                <div class="progress-item-header">
                                    <span>${tipo}</span>
                                    <span>${ingressosTipo.size()} ingressos</span>
                                </div>
                                <div class="progress-bar-bg">
                                    <div class="progress-bar progress-tipo-${tipo}" style="width:${totalIngressos > 0 ? (ingressosTipo.size() / totalIngressos) * 100 : 0}%;">
                                    </div>
                                    <div class="progress-bar-text">
                                        <fmt:formatNumber value="${totalIngressos > 0 ? (ingressosTipo.size() / totalIngressos) * 100 : 0}" maxFractionDigits="1" />%
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </div>
        
        <div class="report-card">
            <h3 class="report-subtitle">Vendas por Dia</h3>
            
            <div class="chart-container">
                <c:set var="maxVenda" value="0" />
                <c:forEach var="venda" items="${vendasPorDia}">
                    <c:if test="${venda.value > maxVenda}">
                        <c:set var="maxVenda" value="${venda.value}" />
                    </c:if>
                </c:forEach>
                
                <c:set var="totalDias" value="${vendasPorDia.size()}" />
                
                <div class="bar-chart" style="gap:${totalDias > 20 ? 1 : 4}px;">
                    <c:forEach var="venda" items="${vendasPorDia}" varStatus="status">
                        <c:set var="altura" value="${maxVenda > 0 ? (venda.value / maxVenda) * 250 : 0}" />
                        <div class="bar-item" 
                             style="height:${altura > 0 ? altura : 1}px; min-width:${totalDias > 30 ? 3 : 15}px;"
                             title="Data: ${venda.key} - Valor: R$ ${venda.value}">
                        
                            <c:if test="${altura > 15 || totalDias < 15}">
                                <div class="bar-value">
                                    <fmt:formatNumber value="${venda.value}" type="currency" currencySymbol="R$" />
                                </div>
                            </c:if>
                    
                            <c:if test="${totalDias <= 15 || status.index % (totalDias > 60 ? 5 : totalDias > 30 ? 3 : 1) == 0}">
                                <div class="bar-label" style="${totalDias > 15 ? 'transform:rotate(-45deg); transform-origin:center left;' : ''}">
                                    <fmt:parseDate value="${venda.key}" pattern="yyyy-MM-dd" var="parsedDate" />
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM" />
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
                <div class="chart-divider"></div>
            </div>
        </div>
 
        <div class="report-card">
            <h3 class="report-subtitle">
                Listagem de Ingressos 
                <c:if test="${totalRegistros > 0}">
                    <span class="subtitle-info">
                        (Mostrando ${(paginaAtual-1)*tamanhoPagina+1} - ${paginaAtual*tamanhoPagina > totalRegistros ? totalRegistros : paginaAtual*tamanhoPagina} de ${totalRegistros})
                    </span>
                </c:if>
            </h3>
            
            <c:choose>
                <c:when test="${empty ingressos}">
                    <div class="alert alert-info">Nenhum ingresso encontrado para os filtros selecionados.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Data Compra</th>
                                    <th>Cliente</th>
                                    <th>Tipo</th>
                                    <th>Valor</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="ingresso" items="${ingressosPaginados}">
                                    <tr>
                                        <td>${ingresso.id}</td>
                                        <td>
                                            <fmt:parseDate value="${ingresso.dataCompra}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                            <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </td>
                                        <td>${ingresso.comprador.nome}</td>
                                        <td>${ingresso.tipo}</td>
                                        <td><fmt:formatNumber value="${ingresso.valor}" type="currency" currencySymbol="R$" /></td>
                                        <td>
                                            <span class="badge ${ingresso.utilizado ? 'badge-CONCLUIDA' : 'badge-PENDENTE'}">
                                                ${ingresso.utilizado ? 'Utilizado' : 'Não Utilizado'}
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
             
                    <c:if test="${totalPaginas > 1}">
                        <div class="pagination">
                            <c:if test="${paginaAtual > 1}">
                                <c:url var="urlAnterior" value="/relatorio/vendas">
                                    <c:if test="${not empty param.dataInicio}">
                                        <c:param name="dataInicio" value="${param.dataInicio}" />
                                    </c:if>
                                    <c:if test="${not empty param.dataFim}">
                                        <c:param name="dataFim" value="${param.dataFim}" />
                                    </c:if>
                                    <c:if test="${not empty param.tipoIngresso}">
                                        <c:param name="tipoIngresso" value="${param.tipoIngresso}" />
                                    </c:if>
                                    <c:param name="pagina" value="${paginaAtual - 1}" />
                                </c:url>
                                <a href="${urlAnterior}" class="btn btn-sm btn-outline">Anterior</a>
                            </c:if>
                            
                            <c:if test="${inicio > 1}">
                                <c:url var="urlPrimeira" value="/relatorio/vendas">
                                    <c:if test="${not empty param.dataInicio}">
                                        <c:param name="dataInicio" value="${param.dataInicio}" />
                                    </c:if>
                                    <c:if test="${not empty param.dataFim}">
                                        <c:param name="dataFim" value="${param.dataFim}" />
                                    </c:if>
                                    <c:if test="${not empty param.tipoIngresso}">
                                        <c:param name="tipoIngresso" value="${param.tipoIngresso}" />
                                    </c:if>
                                    <c:param name="pagina" value="1" />
                                </c:url>
                                <a href="${urlPrimeira}" class="btn btn-sm btn-outline">1</a>
                                <c:if test="${inicio > 2}">
                                    <span class="pagination-ellipsis">...</span>
                                </c:if>
                            </c:if>
                       
                            <c:forEach begin="${inicio}" end="${fim}" var="i">
                                <c:choose>
                                    <c:when test="${i == paginaAtual}">
                                        <span class="btn btn-sm btn-primary pagination-active">${i}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <c:url var="urlPagina" value="/relatorio/vendas">
                                            <c:if test="${not empty param.dataInicio}">
                                                <c:param name="dataInicio" value="${param.dataInicio}" />
                                            </c:if>
                                            <c:if test="${not empty param.dataFim}">
                                                <c:param name="dataFim" value="${param.dataFim}" />
                                            </c:if>
                                            <c:if test="${not empty param.tipoIngresso}">
                                                <c:param name="tipoIngresso" value="${param.tipoIngresso}" />
                                            </c:if>
                                            <c:param name="pagina" value="${i}" />
                                        </c:url>
                                        <a href="${urlPagina}" class="btn btn-sm btn-outline">${i}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                
                            <c:if test="${fim < totalPaginas}">
                                <c:if test="${fim < totalPaginas - 1}">
                                    <span class="pagination-ellipsis">...</span>
                                </c:if>
                                <c:url var="urlUltima" value="/relatorio/vendas">
                                    <c:if test="${not empty param.dataInicio}">
                                        <c:param name="dataInicio" value="${param.dataInicio}" />
                                    </c:if>
                                    <c:if test="${not empty param.dataFim}">
                                        <c:param name="dataFim" value="${param.dataFim}" />
                                    </c:if>
                                    <c:if test="${not empty param.tipoIngresso}">
                                        <c:param name="tipoIngresso" value="${param.tipoIngresso}" />
                                    </c:if>
                                    <c:param name="pagina" value="${totalPaginas}" />
                                </c:url>
                                <a href="${urlUltima}" class="btn btn-sm btn-outline">${totalPaginas}</a>
                            </c:if>
                            
                            <c:if test="${paginaAtual < totalPaginas}">
                                <c:url var="urlProxima" value="/relatorio/vendas">
                                    <c:if test="${not empty param.dataInicio}">
                                        <c:param name="dataInicio" value="${param.dataInicio}" />
                                    </c:if>
                                    <c:if test="${not empty param.dataFim}">
                                        <c:param name="dataFim" value="${param.dataFim}" />
                                    </c:if>
                                    <c:if test="${not empty param.tipoIngresso}">
                                        <c:param name="tipoIngresso" value="${param.tipoIngresso}" />
                                    </c:if>
                                    <c:param name="pagina" value="${paginaAtual + 1}" />
                                </c:url>
                                <a href="${urlProxima}" class="btn btn-sm btn-outline">Próxima</a>
                            </c:if>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
        
        <div class="insight-section">
            <h3 class="report-title">Insights</h3>
            
            <ul class="insight-list">
                <c:if test="${totalIngressos > 0}">
                    <li>
                        Taxa de utilização de ingressos: <strong>${Math.round(percentualUtilizados)}%</strong>.
                        <c:if test="${percentualUtilizados < 80}">
                            <span class="text-danger">Considere implementar lembretes para visitantes utilizarem seus ingressos.</span>
                        </c:if>
                    </li>
                    
                    <li>
                        <c:set var="tipoMaisVendido" value="" />
                        <c:set var="maxVendas" value="0" />
                        <c:forEach var="entry" items="${ingressosPorTipo}">
                            <c:if test="${entry.value.size() > maxVendas}">
                                <c:set var="tipoMaisVendido" value="${entry.key}" />
                                <c:set var="maxVendas" value="${entry.value.size()}" />
                            </c:if>
                        </c:forEach>
                        O tipo de ingresso mais vendido foi <strong>${tipoMaisVendido}</strong> 
                        com ${maxVendas} vendas (${Math.round((maxVendas / totalIngressos) * 100)}% do total).
                    </li>
                    
                    <c:set var="valorMedio" value="${valorTotal / totalIngressos}" />
                    <li>
                        Ticket médio: <strong><fmt:formatNumber value="${valorMedio}" type="currency" currencySymbol="R$" /></strong>.
                    </li>
                </c:if>
            </ul>
            
            <h3 class="recommendations-title">Recomendações</h3>
            
            <ul class="insight-list">
                <c:if test="${totalIngressos > 0}">
                    <c:set var="tipoMenosVendido" value="" />
                    <c:set var="minVendas" value="999999" />
                    <c:forEach var="entry" items="${ingressosPorTipo}">
                        <c:if test="${entry.value.size() < minVendas && entry.value.size() > 0}">
                            <c:set var="tipoMenosVendido" value="${entry.key}" />
                            <c:set var="minVendas" value="${entry.value.size()}" />
                        </c:if>
                    </c:forEach>
                    
                    <c:if test="${not empty tipoMenosVendido}">
                        <li>
                            Considere promover mais os ingressos do tipo <strong>${tipoMenosVendido}</strong>,
                            pois representam apenas ${Math.round((minVendas / totalIngressos) * 100)}% das vendas.
                        </li>
                    </c:if>
                    
                    <li>
                        <c:set var="maxDiario" value="0" />
                        <c:set var="maxData" value="" />
                        <c:forEach var="entry" items="${vendasPorDia}">
                            <c:if test="${entry.value > maxDiario}">
                                <c:set var="maxDiario" value="${entry.value}" />
                                <c:set var="maxData" value="${entry.key}" />
                            </c:if>
                        </c:forEach>
                        
                        <c:if test="${not empty maxData}">
                            <fmt:parseDate value="${maxData}" pattern="yyyy-MM-dd" var="dataMaisVendas" />
                            O dia com maior volume de vendas foi 
                            <strong><fmt:formatDate value="${dataMaisVendas}" pattern="dd/MM/yyyy" /></strong> 
                            com <fmt:formatNumber value="${maxDiario}" type="currency" currencySymbol="R$" />.
                            Analise quais eventos ou promoções ocorreram nesse dia.
                        </c:if>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</t:master>