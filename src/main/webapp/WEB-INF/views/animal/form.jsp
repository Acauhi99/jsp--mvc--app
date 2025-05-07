<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:master title="Registrar Novo Animal">
    <div class="form-container">
        <div class="form-header" style="text-align:center;">
            <span class="ticket-emoji">🦁</span>
            <h1>Registrar Novo Animal</h1>
        </div>
        <form action="${pageContext.request.contextPath}/animal/novo" method="post" class="auth-form">
            <div class="form-section">
                <div class="form-grid">
                    <div class="form-group">
                        <label for="nome">Nome</label>
                        <input type="text" id="nome" name="nome" required maxlength="100" />
                    </div>
                    <div class="form-group">
                        <label for="especie">Espécie</label>
                        <input type="text" id="especie" name="especie" required maxlength="100" />
                    </div>
                    <div class="form-group">
                        <label for="nomeCientifico">Nome Científico</label>
                        <input type="text" id="nomeCientifico" name="nomeCientifico" maxlength="150" />
                    </div>
                    <div class="form-group">
                        <label for="classe">Classe</label>
                        <select id="classe" name="classe" required>
                            <c:forEach var="c" items="${classes}">
                                <option value="${c}">${c}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="genero">Gênero</label>
                        <select id="genero" name="genero" required>
                            <c:forEach var="g" items="${generos}">
                                <option value="${g}">${g}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="habitatId">Habitat</label>
                        <select id="habitatId" name="habitatId" required>
                            <c:forEach var="h" items="${habitats}">
                                <option value="${h.id}">${h.nome}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="statusSaude">Status de Saúde</label>
                        <select id="statusSaude" name="statusSaude" required>
                            <option value="SAUDAVEL">Saudável</option>
                            <option value="EM_TRATAMENTO">Em Tratamento</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="dataChegada">Data de Chegada</label>
                        <input type="date" id="dataChegada" name="dataChegada" required />
                    </div>
                </div>
                <div class="form-group">
                    <label for="detalhesSaude">Detalhes de Saúde</label>
                    <textarea id="detalhesSaude" name="detalhesSaude" rows="4" maxlength="1000"></textarea>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-large btn-full">Registrar Animal</button>
                <a href="${pageContext.request.contextPath}/animal/galeria" class="btn btn-outline btn-full" style="margin-top:1rem;">Cancelar</a>
            </div>
        </form>
    </div>
</t:master>