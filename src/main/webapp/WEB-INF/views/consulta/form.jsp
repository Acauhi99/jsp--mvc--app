<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Nova Consulta Veterinária">
    <div class="form-container">
        <div class="form-header" style="text-align:center;">
            <span style="font-size:2rem;">🩺</span>
            <h1>Nova Consulta</h1>
        </div>
        <form action="${pageContext.request.contextPath}/consulta/salvar" method="post" class="auth-form">
            <div class="form-section">
                <div class="form-group">
                    <label for="animal">Animal</label>
                    <select name="animal" id="animal" required>
                        <option value="">Selecione</option>
                        <c:forEach var="a" items="${animais}">
                            <option value="${a.id}">${a.nome} (${a.nomeCientifico})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="tipoConsulta">Tipo de Consulta</label>
                    <select name="tipoConsulta" id="tipoConsulta" required>
                        <option value="">Selecione</option>
                        <option value="ROTINA">Rotina</option>
                        <option value="EMERGENCIA">Emergência</option>
                        <option value="TRATAMENTO">Tratamento</option>
                        <option value="CIRURGIA">Cirurgia</option>
                        <option value="EXAME">Exame</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="status">Status</label>
                    <select name="status" id="status" required>
                        <option value="AGENDADA">Agendada</option>
                        <option value="EM_ANDAMENTO">Em Andamento</option>
                        <option value="CONCLUIDA">Concluída</option>
                        <option value="CANCELADA">Cancelada</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="dataHora">Data e Hora</label>
                    <input type="datetime-local" id="dataHora" name="dataHora" required>
                </div>
                <div class="form-group">
                    <label for="veterinario">Veterinário Responsável</label>
                    <select name="veterinario" id="veterinario">
                        <option value="">Selecione</option>
                        <c:forEach var="v" items="${veterinarios}">
                            <option value="${v.id}">${v.nome}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="diagnostico">Diagnóstico</label>
                    <textarea id="diagnostico" name="diagnostico" rows="4"
                        style="width:100%; min-width:100%; max-width:100%; min-height:100px; resize:vertical; box-sizing:border-box;"></textarea>
                </div>
                <div class="form-group">
                    <label for="tratamento">Tratamento Recomendado</label>
                    <textarea id="tratamento" name="tratamento" rows="4"
                        style="width:100%; min-width:100%; max-width:100%; min-height:100px; resize:vertical; box-sizing:border-box;"></textarea>
                </div>
                <div class="form-group">
                    <label for="medicamentos">Medicamentos</label>
                    <textarea id="medicamentos" name="medicamentos" rows="3"
                        style="width:100%; min-width:100%; max-width:100%; min-height:80px; resize:vertical; box-sizing:border-box;"></textarea>
                </div>
                <div class="form-group checkbox-group">
                    <input type="checkbox" id="acompanhamentoNecessario" name="acompanhamentoNecessario">
                    <label for="acompanhamentoNecessario">Necessita acompanhamento futuro</label>
                </div>
                <div class="form-group dataRetorno" style="display:none;">
                    <label for="dataRetorno">Data para Retorno</label>
                    <input type="datetime-local" id="dataRetorno" name="dataRetorno">
                </div>
                <div class="form-group">
                    <label for="observacoes">Observações Adicionais</label>
                    <textarea id="observacoes" name="observacoes" rows="3"
                        style="width:100%; min-width:100%; max-width:100%; min-height:80px; resize:vertical; box-sizing:border-box;"></textarea>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-large btn-full">Salvar</button>
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