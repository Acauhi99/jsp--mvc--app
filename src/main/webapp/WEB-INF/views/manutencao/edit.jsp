<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:master title="Editar Solicitação de Manutenção">
    <div class="form-container">
        <div class="form-header" style="text-align:center;">
            <span style="font-size:2rem;">🔧</span>
            <h1>Editar Solicitação</h1>
        </div>
        <form action="${pageContext.request.contextPath}/manutencao/salvar" method="post" class="auth-form">
            <input type="hidden" name="id" value="${manutencao.id}" />
            <div class="form-section">
                <div class="form-group">
                    <label for="habitat">Habitat</label>
                    <select name="habitat" id="habitat" required>
                        <option value="">Selecione</option>
                        <c:forEach var="h" items="${habitats}">
                            <option value="${h.id}" <c:if test="${manutencao.habitat.id == h.id}">selected</c:if>>${h.nome}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="tipoManutencao">Tipo de Manutenção</label>
                    <select name="tipoManutencao" id="tipoManutencao" required>
                        <option value="">Selecione</option>
                        <option value="LIMPEZA" <c:if test="${manutencao.tipoManutencao == 'LIMPEZA'}">selected</c:if>>Limpeza</option>
                        <option value="REPARO" <c:if test="${manutencao.tipoManutencao == 'REPARO'}">selected</c:if>>Reparo</option>
                        <option value="MODIFICACAO" <c:if test="${manutencao.tipoManutencao == 'MODIFICACAO'}">selected</c:if>>Modificação</option>
                        <option value="MONITORAMENTO_AMBIENTAL" <c:if test="${manutencao.tipoManutencao == 'MONITORAMENTO_AMBIENTAL'}">selected</c:if>>Monitoramento Ambiental</option>
                        <option value="TROCA_SUBSTRATO" <c:if test="${manutencao.tipoManutencao == 'TROCA_SUBSTRATO'}">selected</c:if>>Troca de Substrato</option>
                        <option value="PAISAGISMO" <c:if test="${manutencao.tipoManutencao == 'PAISAGISMO'}">selected</c:if>>Paisagismo</option>
                        <option value="MANUTENCAO_EQUIPAMENTOS" <c:if test="${manutencao.tipoManutencao == 'MANUTENCAO_EQUIPAMENTOS'}">selected</c:if>>Manutenção de Equipamentos</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="prioridade">Prioridade</label>
                    <select name="prioridade" id="prioridade" required>
                        <option value="BAIXA" <c:if test="${manutencao.prioridade == 'BAIXA'}">selected</c:if>>Baixa</option>
                        <option value="MEDIA" <c:if test="${manutencao.prioridade == 'MEDIA'}">selected</c:if>>Média</option>
                        <option value="ALTA" <c:if test="${manutencao.prioridade == 'ALTA'}">selected</c:if>>Alta</option>
                        <option value="URGENTE" <c:if test="${manutencao.prioridade == 'URGENTE'}">selected</c:if>>Urgente</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="dataProgramada">Data Programada</label>
                    <input type="datetime-local" id="dataProgramada" name="dataProgramada"
                        value="<c:if test='${not empty manutencao.dataProgramada}'>${fn:substring(manutencao.dataProgramada,0,16)}</c:if>">
                </div>
                <div class="form-group">
                    <label for="responsavel">Responsável</label>
                    <select name="responsavel" id="responsavel">
                        <option value="">Selecione</option>
                        <c:forEach var="f" items="${manutentores}">
                            <option value="${f.id}" <c:if test="${not empty manutencao.responsavel && manutencao.responsavel.id == f.id}">selected</c:if>>${f.nome}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="descricao">Descrição</label>
                    <textarea id="descricao" name="descricao" rows="5" required
                        style="width:100%; min-width:100%; max-width:100%; min-height:120px; max-height:200px; resize:vertical; box-sizing:border-box;">${manutencao.descricao}</textarea>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-large btn-full">Salvar Alterações</button>
                <a href="${pageContext.request.contextPath}/manutencao" class="btn btn-outline btn-full" style="margin-top:1rem;">Cancelar</a>
            </div>
        </form>
    </div>
</t:master>