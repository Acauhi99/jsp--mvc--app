<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Nova Solicitação de Manutenção">
    <div class="form-container">
        <div class="form-header" style="text-align:center;">
            <span style="font-size:2rem;">🔧</span>
            <h1>Nova Solicitação</h1>
        </div>
        <form action="${pageContext.request.contextPath}/manutencao/salvar" method="post" class="auth-form">
            <div class="form-section">
                <div class="form-group">
                    <label for="habitat">Habitat</label>
                    <select name="habitat" id="habitat" required>
                        <option value="">Selecione</option>
                        <c:forEach var="h" items="${habitats}">
                            <option value="${h.id}">${h.nome}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="tipoManutencao">Tipo de Manutenção</label>
                    <select name="tipoManutencao" id="tipoManutencao" required>
                        <option value="">Selecione</option>
                        <option value="LIMPEZA">Limpeza</option>
                        <option value="REPARO">Reparo</option>
                        <option value="MODIFICACAO">Modificação</option>
                        <option value="MONITORAMENTO_AMBIENTAL">Monitoramento Ambiental</option>
                        <option value="TROCA_SUBSTRATO">Troca de Substrato</option>
                        <option value="PAISAGISMO">Paisagismo</option>
                        <option value="MANUTENCAO_EQUIPAMENTOS">Manutenção de Equipamentos</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="prioridade">Prioridade</label>
                    <select name="prioridade" id="prioridade" required>
                        <option value="BAIXA">Baixa</option>
                        <option value="MEDIA">Média</option>
                        <option value="ALTA">Alta</option>
                        <option value="URGENTE">Urgente</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="dataProgramada">Data Programada</label>
                    <input type="datetime-local" id="dataProgramada" name="dataProgramada">
                </div>
                <div class="form-group">
                    <label for="responsavel">Responsável</label>
                    <select name="responsavel" id="responsavel">
                        <option value="">Selecione</option>
                        <c:forEach var="f" items="${manutentores}">
                            <option value="${f.id}">${f.nome}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="descricao">Descrição</label>
                    <textarea id="descricao" name="descricao" rows="5" required
                        style="width:100%; min-width:100%; max-width:100%; min-height:120px; max-height:200px; resize:vertical; box-sizing:border-box;"></textarea>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-large btn-full">Salvar</button>
                <a href="${pageContext.request.contextPath}/manutencao" class="btn btn-outline btn-full" style="margin-top:1rem;">Cancelar</a>
            </div>
        </form>
    </div>
</t:master>