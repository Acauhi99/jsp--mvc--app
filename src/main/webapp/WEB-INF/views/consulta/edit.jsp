<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:master title="Editar Consulta Veterinária">
    <div class="form-container">
        <div class="form-header" style="text-align:center;">
            <span style="font-size:2rem;">🩺</span>
            <h1>Editar Consulta</h1>
        </div>
        <form action="${pageContext.request.contextPath}/consulta/salvar" method="post" class="auth-form">
            <input type="hidden" name="id" value="${consulta.id}" />
            <div class="form-section">
                <div class="form-group">
                    <label for="animal">Animal</label>
                    <select name="animal" id="animal" required>
                        <option value="">Selecione</option>
                        <c:forEach var="a" items="${animais}">
                            <option value="${a.id}" <c:if test="${consulta.animal.id == a.id}">selected</c:if>>${a.nome} (${a.nomeCientifico})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="tipoConsulta">Tipo de Consulta</label>
                    <select name="tipoConsulta" id="tipoConsulta" required>
                        <option value="">Selecione</option>
                        <option value="ROTINA" <c:if test="${consulta.tipoConsulta == 'ROTINA'}">selected</c:if>>Rotina</option>
                        <option value="EMERGENCIA" <c:if test="${consulta.tipoConsulta == 'EMERGENCIA'}">selected</c:if>>Emergência</option>
                        <option value="TRATAMENTO" <c:if test="${consulta.tipoConsulta == 'TRATAMENTO'}">selected</c:if>>Tratamento</option>
                        <option value="CIRURGIA" <c:if test="${consulta.tipoConsulta == 'CIRURGIA'}">selected</c:if>>Cirurgia</option>
                        <option value="EXAME" <c:if test="${consulta.tipoConsulta == 'EXAME'}">selected</c:if>>Exame</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="status">Status</label>
                    <select name="status" id="status" required>
                        <option value="AGENDADA" <c:if test="${consulta.status == 'AGENDADA'}">selected</c:if>>Agendada</option>
                        <option value="EM_ANDAMENTO" <c:if test="${consulta.status == 'EM_ANDAMENTO'}">selected</c:if>>Em Andamento</option>
                        <option value="CONCLUIDA" <c:if test="${consulta.status == 'CONCLUIDA'}">selected</c:if>>Concluída</option>
                        <option value="CANCELADA" <c:if test="${consulta.status == 'CANCELADA'}">selected</c:if>>Cancelada</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="dataHora">Data e Hora</label>
                    <input type="datetime-local" id="dataHora" name="dataHora" 
                           value="<c:if test='${not empty consulta.dataHora}'>${fn:substring(consulta.dataHora,0,16)}</c:if>" required>
                </div>
                <div class="form-group">
                    <label for="veterinario">Veterinário Responsável</label>
                    <select name="veterinario" id="veterinario">
                        <option value="">Selecione</option>
                        <c:forEach var="v" items="${veterinarios}">
                            <option value="${v.id}" <c:if test="${not empty consulta.veterinario && consulta.veterinario.id == v.id}">selected</c:if>>${v.nome}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="diagnostico">Diagnóstico</label>
                    <textarea id="diagnostico" name="diagnostico" rows="4"
                        style="width:100%; min-width:100%; max-width:100%; min-height:100px; resize:vertical; box-sizing:border-box;">${consulta.diagnostico}</textarea>
                </div>
                <div class="form-group">
                    <label for="tratamento">Tratamento Recomendado</label>
                    <textarea id="tratamento" name="tratamento" rows="4"
                        style="width:100%; min-width:100%; max-width:100%; min-height:100px; resize:vertical; box-sizing:border-box;">${consulta.tratamento}</textarea>
                </div>
                <div class="form-group">
                    <label for="medicamentos">Medicamentos</label>
                    <textarea id="medicamentos" name="medicamentos" rows="3"
                        style="width:100%; min-width:100%; max-width:100%; min-height:80px; resize:vertical; box-sizing:border-box;">${consulta.medicamentos}</textarea>
                </div>
                <div class="form-group checkbox-group">
                    <input type="checkbox" id="acompanhamentoNecessario" name="acompanhamentoNecessario" 
                           <c:if test="${consulta.acompanhamentoNecessario}">checked</c:if>>
                    <label for="acompanhamentoNecessario">Necessita acompanhamento futuro</label>
                </div>
                <div class="form-group dataRetorno" style="display:${consulta.acompanhamentoNecessario ? 'block' : 'none'};">
                    <label for="dataRetorno">Data para Retorno</label>
                    <input type="datetime-local" id="dataRetorno" name="dataRetorno"
                           value="<c:if test='${not empty consulta.dataRetorno}'>${fn:substring(consulta.dataRetorno,0,16)}</c:if>">
                </div>
                <div class="form-group">
                    <label for="observacoes">Observações Adicionais</label>
                    <textarea id="observacoes" name="observacoes" rows="3"
                        style="width:100%; min-width:100%; max-width:100%; min-height:80px; resize:vertical; box-sizing:border-box;">${consulta.observacoes}</textarea>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-large btn-full">Salvar Alterações</button>
                <a href="${pageContext.request.contextPath}/consulta" class="btn btn-outline btn-full" style="margin-top:1rem;">Cancelar</a>
            </div>
        </form>
    </div>
    
    <script>
        document.getElementById('acompanhamentoNecessario').addEventListener('change', function() {
            var dataRetornoDiv = document.querySelector('.dataRetorno');
            if (this.checked) {
                dataRetornoDiv.style.display = 'block';
            } else {
                dataRetornoDiv.style.display = 'none';
                document.getElementById('dataRetorno').value = '';
            }
        });
    </script>
</t:master>