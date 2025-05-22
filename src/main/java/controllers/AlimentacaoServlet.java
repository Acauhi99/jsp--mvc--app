package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Alimentacao;
import models.Animal;
import models.Funcionario;
import repositories.AlimentacaoRepository;
import repositories.AnimalRepository;
import repositories.FuncionarioRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/alimentacao/*")
public class AlimentacaoServlet extends BaseServlet {

    private final AlimentacaoRepository alimentacaoRepository;
    private final AnimalRepository animalRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AlimentacaoServlet() {
        this.alimentacaoRepository = new AlimentacaoRepository();
        this.animalRepository = new AnimalRepository();
        this.funcionarioRepository = new FuncionarioRepository();
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
        request.setAttribute("animais", animalRepository.findAll());
        request.setAttribute("funcionarios", funcionarioRepository.findAll());
        forwardToView(request, response, "/WEB-INF/views/alimentacao/form.jsp");
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response, String idStr)
            throws ServletException, IOException {
        UUID alimentacaoId = parseUUID(idStr);
        Optional<Alimentacao> optAlimentacao = alimentacaoRepository.findById(alimentacaoId);

        if (optAlimentacao.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alimentação não encontrada");
            return;
        }

        request.setAttribute("alimentacao", optAlimentacao.get());
        request.setAttribute("animais", animalRepository.findAll());
        request.setAttribute("funcionarios", funcionarioRepository.findAll());
        forwardToView(request, response, "/WEB-INF/views/alimentacao/form.jsp");
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response, String idStr)
            throws ServletException, IOException {
        UUID alimentacaoId = parseUUID(idStr);
        Optional<Alimentacao> optAlimentacao = alimentacaoRepository.findById(alimentacaoId);

        if (optAlimentacao.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alimentação não encontrada");
            return;
        }

        request.setAttribute("alimentacao", optAlimentacao.get());
        forwardToView(request, response, "/WEB-INF/views/alimentacao/details.jsp");
    }

    private void listAlimentacoes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String animalIdStr = request.getParameter("animalId");
        List<Alimentacao> alimentacoes;

        if (animalIdStr != null && !animalIdStr.isEmpty()) {
            UUID animalId = parseUUID(animalIdStr);
            alimentacoes = alimentacaoRepository.findByAnimalId(animalId);
        } else {
            alimentacoes = alimentacaoRepository.findAll();
        }

        request.setAttribute("animais", animalRepository.findAll());
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

        validateRequiredFields(animalIdStr, tipoAlimento, quantidadeStr, funcionarioIdStr);

        UUID animalId = parseUUID(animalIdStr);
        Double quantidade = parseDouble(quantidadeStr);
        UUID funcionarioId = parseUUID(funcionarioIdStr);

        Animal animal = findAnimal(animalId);
        Funcionario funcionario = findFuncionario(funcionarioId);

        if (idStr != null && !idStr.isEmpty()) {
            updateAlimentacao(idStr, animal, tipoAlimento, quantidade, unidadeMedida, funcionario, observacoes);
            request.setAttribute("mensagem", "Alimentação atualizada com sucesso!");
        } else {
            createAlimentacao(animal, tipoAlimento, quantidade, unidadeMedida, funcionario, observacoes);
            request.setAttribute("mensagem", "Alimentação registrada com sucesso!");
        }

        response.sendRedirect(request.getContextPath() + "/alimentacao");
    }

    private void createAlimentacao(
            Animal animal, String tipoAlimento, Double quantidade,
            String unidadeMedida, Funcionario funcionario, String observacoes) {

        Alimentacao alimentacao = Alimentacao.create(
                animal,
                tipoAlimento,
                quantidade,
                unidadeMedida,
                LocalDateTime.now(),
                funcionario,
                observacoes);

        alimentacaoRepository.save(alimentacao);
    }

    private void updateAlimentacao(
            String idStr, Animal animal, String tipoAlimento, Double quantidade,
            String unidadeMedida, Funcionario funcionario, String observacoes)
            throws Exception {

        UUID id = parseUUID(idStr);
        Optional<Alimentacao> optAlimentacao = alimentacaoRepository.findById(id);

        if (optAlimentacao.isEmpty()) {
            throw new Exception("Alimentação não encontrada");
        }

        Alimentacao alimentacao = optAlimentacao.get();
        alimentacao.setAnimal(animal);
        alimentacao.setTipoAlimento(tipoAlimento);
        alimentacao.setQuantidade(quantidade);
        alimentacao.setUnidadeMedida(unidadeMedida);
        alimentacao.setFuncionarioResponsavel(funcionario);
        alimentacao.setObservacoes(observacoes);

        alimentacaoRepository.update(alimentacao);
    }

    protected void processDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da alimentação não fornecido");
            return;
        }

        try {
            UUID alimentacaoId = parseUUID(idStr);
            Optional<Alimentacao> alimentacao = alimentacaoRepository.findById(alimentacaoId);

            if (alimentacao.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alimentação não encontrada");
                return;
            }

            alimentacaoRepository.delete(alimentacaoId);

            request.setAttribute("mensagem", "Alimentação excluída com sucesso!");
            response.sendRedirect(request.getContextPath() + "/alimentacao");

        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void validateRequiredFields(String animalIdStr, String tipoAlimento,
            String quantidadeStr, String funcionarioIdStr)
            throws Exception {
        if (animalIdStr == null || tipoAlimento == null ||
                quantidadeStr == null || funcionarioIdStr == null) {
            throw new Exception("Campos obrigatórios não fornecidos");
        }
    }

    private UUID parseUUID(String uuidStr) {
        if (uuidStr == null || uuidStr.trim().isEmpty()) {
            throw new IllegalArgumentException("UUID não pode ser nulo ou vazio");
        }
        return UUID.fromString(uuidStr);
    }

    private Double parseDouble(String doubleStr) {
        if (doubleStr == null || doubleStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Valor numérico não pode ser nulo ou vazio");
        }
        return Double.parseDouble(doubleStr);
    }

    private Animal findAnimal(UUID animalId) throws Exception {
        Optional<Animal> optAnimal = animalRepository.findById(animalId);
        if (optAnimal.isEmpty()) {
            throw new Exception("Animal não encontrado");
        }
        return optAnimal.get();
    }

    private Funcionario findFuncionario(UUID funcionarioId) throws Exception {
        Optional<Funcionario> optFuncionario = funcionarioRepository.findById(funcionarioId);
        if (optFuncionario.isEmpty()) {
            throw new Exception("Funcionário não encontrado");
        }
        return optFuncionario.get();
    }
}
