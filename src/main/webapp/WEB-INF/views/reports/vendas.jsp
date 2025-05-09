<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>

<t:master title="Relatório de Vendas de Ingressos">
    <div class="container">
        <h1 class="section-title">Relatório de Vendas de Ingressos</h1>
        
        <!-- Filtros -->
        <div class="card" style="padding:1.5rem; margin-bottom:2rem; background:#fff; border-radius:8px; box-shadow:0 2px 4px rgba(0,0,0,0.1);">
            <h3 style="margin-bottom:1rem; color:#2c3e50; font-size:1.3rem;">Filtros</h3>
            
            <form method="get" action="${pageContext.request.contextPath}/relatorio/vendas" style="display:flex; flex-wrap:wrap; gap:1rem;">
                <div style="flex:1 1 200px;">
                    <label for="dataInicio" style="display:block; margin-bottom:0.5rem; font-weight:500;">Data Início</label>
                    <input type="date" id="dataInicio" name="dataInicio" value="${dataInicio}" class="form-control">
                </div>
                
                <div style="flex:1 1 200px;">
                    <label for="dataFim" style="display:block; margin-bottom:0.5rem; font-weight:500;">Data Fim</label>
                    <input type="date" id="dataFim" name="dataFim" value="${dataFim}" class="form-control">
                </div>
                
                <div style="flex:1 1 200px;">
                    <label for="tipoIngresso" style="display:block; margin-bottom:0.5rem; font-weight:500;">Tipo de Ingresso</label>
                    <select id="tipoIngresso" name="tipoIngresso" class="form-control">
                        <option value="">Todos</option>
                        <c:forEach var="tipo" items="${tiposIngresso}">
                            <option value="${tipo}" ${param.tipoIngresso eq tipo.toString() ? 'selected' : ''}>${tipo}</option>
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
                <div style="font-size:1.1rem; color:#666; margin-bottom:0.5rem;">Total de Ingressos</div>
                <div style="font-size:2.5rem; font-weight:bold; color:#2c3e50;">${totalIngressos}</div>
            </div>
            
            <div style="flex:1 1 220px; background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1); border-left:5px solid #2ecc71;">
                <div style="font-size:1.1rem; color:#666; margin-bottom:0.5rem;">Valor Total</div>
                <div style="font-size:2.5rem; font-weight:bold; color:#2c3e50;">
                    <fmt:formatNumber value="${valorTotal}" type="currency" currencySymbol="R$" />
                </div>
            </div>
            
            <div style="flex:1 1 220px; background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1); border-left:5px solid #e67e22;">
                <div style="font-size:1.1rem; color:#666; margin-bottom:0.5rem;">Ticket Médio</div>
                <div style="font-size:2.5rem; font-weight:bold; color:#2c3e50;">
                    <fmt:formatNumber value="${totalIngressos > 0 ? valorTotal / totalIngressos : 0}" type="currency" currencySymbol="R$" />
                </div>
            </div>
            
            <div style="flex:1 1 220px; background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1); border-left:5px solid #9b59b6;">
                <div style="font-size:1.1rem; color:#666; margin-bottom:0.5rem;">Ingressos Utilizados</div>
                <div style="font-size:2.5rem; font-weight:bold; color:#2c3e50;">
                    ${ingressosUtilizados} (${Math.round(percentualUtilizados)}%)
                </div>
            </div>
        </div>
        
        <!-- Vendas por Tipo de Ingresso -->
        <div style="display:flex; flex-wrap:wrap; gap:2rem; margin-bottom:2rem;">
            <div style="flex:1 1 400px; min-width:300px; background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1);">
                <h3 style="margin-bottom:1rem; color:#2c3e50; font-size:1.3rem;">Vendas por Tipo de Ingresso</h3>
                
                <table style="width:100%; border-collapse:collapse;">
                    <thead>
                        <tr>
                            <th style="text-align:left; padding:0.5rem; border-bottom:1px solid #eee;">Tipo</th>
                            <th style="text-align:right; padding:0.5rem; border-bottom:1px solid #eee;">Quantidade</th>
                            <th style="text-align:right; padding:0.5rem; border-bottom:1px solid #eee;">Valor Total</th>
                            <th style="text-align:right; padding:0.5rem; border-bottom:1px solid #eee;">Média</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="tipo" items="${tiposIngresso}">
                            <c:set var="ingressosTipo" value="${ingressosPorTipo[tipo]}" />
                            <c:set var="estatisticas" value="${estatisticasPorTipo[tipo]}" />
                            
                            <c:if test="${not empty ingressosTipo}">
                                <tr>
                                    <td style="padding:0.5rem; border-bottom:1px solid #eee;">${tipo}</td>
                                    <td style="text-align:right; padding:0.5rem; border-bottom:1px solid #eee;">${ingressosTipo.size()}</td>
                                    <td style="text-align:right; padding:0.5rem; border-bottom:1px solid #eee;">
                                        <fmt:formatNumber value="${estatisticas.sum}" type="currency" currencySymbol="R$" />
                                    </td>
                                    <td style="text-align:right; padding:0.5rem; border-bottom:1px solid #eee;">
                                        <fmt:formatNumber value="${estatisticas.average}" type="currency" currencySymbol="R$" />
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            
            <!-- Gráfico Visual de Distribuição -->
            <div style="flex:1 1 400px; min-width:300px; background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1);">
                <h3 style="margin-bottom:1rem; color:#2c3e50; font-size:1.3rem;">Distribuição das Vendas</h3>
                
                <div style="display:flex; flex-direction:column; gap:1rem;">
                    <c:forEach var="tipo" items="${tiposIngresso}">
                        <c:set var="ingressosTipo" value="${ingressosPorTipo[tipo]}" />
                        <c:set var="estatisticas" value="${estatisticasPorTipo[tipo]}" />
                        
                        <c:if test="${not empty ingressosTipo}">
                            <div>
                                <div style="display:flex; justify-content:space-between; margin-bottom:0.3rem;">
                                    <span>${tipo}</span>
                                    <span>${ingressosTipo.size()} ingressos</span>
                                </div>
                                <div style="height:20px; background:#eee; border-radius:5px; overflow:hidden; position:relative;">
                                    <div style="height:100%; background:
                                        <c:choose>
                                            <c:when test="${tipo == 'ADULTO'}">#3498db</c:when>
                                            <c:when test="${tipo == 'CRIANCA'}">#2ecc71</c:when>
                                            <c:when test="${tipo == 'IDOSO'}">#9b59b6</c:when>
                                            <c:when test="${tipo == 'ESTUDANTE'}">#e67e22</c:when>
                                            <c:otherwise>#95a5a6</c:otherwise>
                                        </c:choose>
                                        ; width:${totalIngressos > 0 ? (ingressosTipo.size() / totalIngressos) * 100 : 0}%;">
                                    </div>
                                    <div style="position:absolute; right:10px; top:0; height:100%; display:flex; align-items:center; color:#fff; font-weight:bold; text-shadow:0 0 2px rgba(0,0,0,0.5);">
                                        <fmt:formatNumber value="${totalIngressos > 0 ? (ingressosTipo.size() / totalIngressos) * 100 : 0}" maxFractionDigits="1" />%
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </div>
        
        <!-- Gráfico de Vendas por Dia - VERSÃO CORRIGIDA -->
        <div style="background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1); margin-bottom:2rem;">
            <h3 style="margin-bottom:1.5rem; color:#2c3e50; font-size:1.3rem;">Vendas por Dia</h3>
            
            <div style="width:100%; height:350px; position:relative; margin-bottom:20px;">
                <c:set var="maxVenda" value="0" />
                <c:forEach var="venda" items="${vendasPorDia}">
                    <c:if test="${venda.value > maxVenda}">
                        <c:set var="maxVenda" value="${venda.value}" />
                    </c:if>
                </c:forEach>
                
                <!-- Calcular número total de dias para ajustar a exibição -->
                <c:set var="totalDias" value="${vendasPorDia.size()}" />
                
                <div style="display:flex; height:250px; align-items:flex-end; gap:${totalDias > 20 ? 1 : 4}px; position:relative; margin-bottom:40px;">
                    <c:forEach var="venda" items="${vendasPorDia}" varStatus="status">
                        <c:set var="altura" value="${maxVenda > 0 ? (venda.value / maxVenda) * 250 : 0}" />
                        <div style="flex:1; height:${altura > 0 ? altura : 1}px; background:#3498db; min-width:${totalDias > 30 ? 3 : 15}px; position:relative; border-radius:3px 3px 0 0;"
                             title="Data: ${venda.key} - Valor: R$ ${venda.value}">
                        
                            <!-- Mostrar valor acima da barra apenas se houver espaço -->
                            <c:if test="${altura > 15 || totalDias < 15}">
                                <div style="position:absolute; top:-20px; left:0; right:0; text-align:center; font-size:10px; white-space:nowrap;">
                                    <fmt:formatNumber value="${venda.value}" type="currency" currencySymbol="R$" />
                                </div>
                            </c:if>
                            
                            <!-- Mostrar apenas algumas datas quando há muitos dias -->
                            <c:if test="${totalDias <= 15 || status.index % (totalDias > 60 ? 5 : totalDias > 30 ? 3 : 1) == 0}">
                                <div style="position:absolute; bottom:-30px; left:0; right:0; text-align:center; 
                                            ${totalDias > 15 ? 'transform:rotate(-45deg); transform-origin:center left;' : ''}
                                            font-size:10px; overflow:hidden; text-overflow:ellipsis;">
                                    <fmt:parseDate value="${venda.key}" pattern="yyyy-MM-dd" var="parsedDate" />
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM" />
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
                <div style="height:1px; background:#ddd;"></div>
            </div>
        </div>
        
        <!-- Lista de Ingressos -->
        <div style="background:#fff; border-radius:8px; padding:1.5rem; box-shadow:0 2px 4px rgba(0,0,0,0.1); margin-bottom:2rem;">
            <h3 style="margin-bottom:1.5rem; color:#2c3e50; font-size:1.3rem;">
                Listagem de Ingressos 
                <c:if test="${totalRegistros > 0}">
                    <span style="font-size:0.9rem; font-weight:normal; color:#7f8c8d;">
                        (Mostrando ${(paginaAtual-1)*tamanhoPagina+1} - ${paginaAtual*tamanhoPagina > totalRegistros ? totalRegistros : paginaAtual*tamanhoPagina} de ${totalRegistros})
                    </span>
                </c:if>
            </h3>
            
            <c:choose>
                <c:when test="${empty ingressos}">
                    <div class="alert alert-info">Nenhum ingresso encontrado para os filtros selecionados.</div>
                </c:when>
                <c:otherwise>
                    <div style="overflow-x:auto;">
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
                    
                    <!-- Controles de Paginação -->
                    <c:if test="${totalPaginas > 1}">
                        <div style="display:flex; justify-content:center; margin-top:1.5rem; gap:0.5rem; flex-wrap:wrap;">
                            <!-- Botão Anterior -->
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
                            
                            <!-- Mostrar primeira página se estiver muito distante -->
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
                                    <span style="align-self:center; padding:0 0.5rem;">...</span>
                                </c:if>
                            </c:if>
                            
                            <!-- Números de páginas -->
                            <c:forEach begin="${inicio}" end="${fim}" var="i">
                                <c:choose>
                                    <c:when test="${i == paginaAtual}">
                                        <span class="btn btn-sm btn-primary" style="cursor:default;">${i}</span>
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
                            
                            <!-- Mostrar última página se estiver muito distante -->
                            <c:if test="${fim < totalPaginas}">
                                <c:if test="${fim < totalPaginas - 1}">
                                    <span style="align-self:center; padding:0 0.5rem;">...</span>
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
                            
                            <!-- Botão Próxima -->
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
        
        <!-- Insights e Recomendações -->
        <div style="background:#f1f9ff; border-radius:8px; padding:1.5rem; margin-bottom:2rem; border-left:5px solid #3498db;">
            <h3 style="margin-bottom:1rem; color:#2c3e50; font-size:1.3rem;">Insights</h3>
            
            <ul style="list-style-type:circle; margin-left:1.5rem;">
                <c:if test="${totalIngressos > 0}">
                    <li style="margin-bottom:0.5rem;">
                        Taxa de utilização de ingressos: <strong>${Math.round(percentualUtilizados)}%</strong>.
                        <c:if test="${percentualUtilizados < 80}">
                            <span style="color:#e74c3c;">Considere implementar lembretes para visitantes utilizarem seus ingressos.</span>
                        </c:if>
                    </li>
                    
                    <li style="margin-bottom:0.5rem;">
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
                    <li style="margin-bottom:0.5rem;">
                        Ticket médio: <strong><fmt:formatNumber value="${valorMedio}" type="currency" currencySymbol="R$" /></strong>.
                    </li>
                </c:if>
            </ul>
            
            <h3 style="margin-top:1.5rem; margin-bottom:1rem; color:#2c3e50; font-size:1.3rem;">Recomendações</h3>
            
            <ul style="list-style-type:circle; margin-left:1.5rem;">
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
                        <li style="margin-bottom:0.5rem;">
                            Considere promover mais os ingressos do tipo <strong>${tipoMenosVendido}</strong>,
                            pois representam apenas ${Math.round((minVendas / totalIngressos) * 100)}% das vendas.
                        </li>
                    </c:if>
                    
                    <li style="margin-bottom:0.5rem;">
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