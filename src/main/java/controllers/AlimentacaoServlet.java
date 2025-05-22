package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Alimentacao;
import handlers.AlimentacaoHandler;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/alimentacao/*")
public class AlimentacaoServlet extends BaseServlet {

    private final AlimentacaoHandler alimentacaoHandler;

    public AlimentacaoServlet() {
        this.alimentacaoHandler = new AlimentacaoHandler();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAnyRole(request, response, ROLE_VETERINARIO, ROLE_ADMINISTRADOR)) {
            return;
        }

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                listAlimentacoes(request, response);
                return;
            }

            if (pathInfo.equals("/novo")) {
                showNewForm(request, response);
                return;
            }

            if (pathInfo.startsWith("/editar/")) {
                showEditForm(request, response, pathInfo.substring("/editar/".length()));
                return;
            }

            showDetails(request, response, pathInfo.substring(1));

        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido: " + e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAnyRole(request, response, ROLE_VETERINARIO, ROLE_ADMINISTRADOR)) {
            return;
        }

        if (request.getParameter("_method") != null && request.getParameter("_method").equals("DELETE")) {
            processDelete(request, response);
            return;
        }

        try {
            processCreateOrUpdate(request, response);
        } catch (Exception e) {
            request.setAttribute("erro", "Erro ao processar solicitação: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("animais", alimentacaoHandler.getAllAnimais());
        request.setAttribute("funcionarios", alimentacaoHandler.getAllFuncionarios());
        forwardToView(request, response, "/WEB-INF/views/alimentacao/form.jsp");
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response, String idStr)
            throws ServletException, IOException {
        try {
            Optional<Alimentacao> optAlimentacao = alimentacaoHandler.getAlimentacaoById(idStr);

            if (optAlimentacao.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alimentação não encontrada");
                return;
            }

            request.setAttribute("alimentacao", optAlimentacao.get());
            request.setAttribute("animais", alimentacaoHandler.getAllAnimais());
            request.setAttribute("funcionarios", alimentacaoHandler.getAllFuncionarios());
            forwardToView(request, response, "/WEB-INF/views/alimentacao/form.jsp");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido: " + e.getMessage());
        }
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response, String idStr)
            throws ServletException, IOException {
        try {
            Optional<Alimentacao> optAlimentacao = alimentacaoHandler.getAlimentacaoById(idStr);

            if (optAlimentacao.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alimentação não encontrada");
                return;
            }

            request.setAttribute("alimentacao", optAlimentacao.get());
            forwardToView(request, response, "/WEB-INF/views/alimentacao/details.jsp");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido: " + e.getMessage());
        }
    }

    private void listAlimentacoes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String animalIdStr = request.getParameter("animalId");
        List<Alimentacao> alimentacoes = alimentacaoHandler.getAlimentacoesByAnimalId(animalIdStr);

        request.setAttribute("animais", alimentacaoHandler.getAllAnimais());
        request.setAttribute("alimentacoes", alimentacoes);
        forwardToView(request, response, "/WEB-INF/views/alimentacao/list.jsp");
    }

    private void processCreateOrUpdate(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String idStr = request.getParameter("id");
        String animalIdStr = request.getParameter("animalId");
        String tipoAlimento = request.getParameter("tipoAlimento");
        String quantidadeStr = request.getParameter("quantidade");
        String unidadeMedida = request.getParameter("unidadeMedida");
        String funcionarioIdStr = request.getParameter("funcionarioId");
        String observacoes = request.getParameter("observacoes");

        try {
            if (idStr != null && !idStr.isEmpty()) {
                alimentacaoHandler.updateAlimentacao(
                        idStr, animalIdStr, tipoAlimento, quantidadeStr,
                        unidadeMedida, funcionarioIdStr, observacoes);
                request.setAttribute("mensagem", "Alimentação atualizada com sucesso!");
            } else {
                alimentacaoHandler.createAlimentacao(
                        animalIdStr, tipoAlimento, quantidadeStr,
                        unidadeMedida, funcionarioIdStr, observacoes);
                request.setAttribute("mensagem", "Alimentação registrada com sucesso!");
            }

            response.sendRedirect(request.getContextPath() + "/alimentacao");
        } catch (Exception e) {
            request.setAttribute("erro", e.getMessage());
            request.setAttribute("animais", alimentacaoHandler.getAllAnimais());
            request.setAttribute("funcionarios", alimentacaoHandler.getAllFuncionarios());

            if (idStr != null && !idStr.isEmpty()) {
                // Caso de edição
                forwardToView(request, response, "/WEB-INF/views/alimentacao/form.jsp");
            } else {
                // Caso de criação
                forwardToView(request, response, "/WEB-INF/views/alimentacao/form.jsp");
            }
        }
    }

    protected void processDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da alimentação não fornecido");
            return;
        }

        try {
            alimentacaoHandler.deleteAlimentacao(idStr);
            request.setAttribute("mensagem", "Alimentação excluída com sucesso!");
            response.sendRedirect(request.getContextPath() + "/alimentacao");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido: " + e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
