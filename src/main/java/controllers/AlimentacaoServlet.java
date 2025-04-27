package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
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
public class AlimentacaoServlet extends HttpServlet {
    
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
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/novo")) {
            List<Animal> animais = animalRepository.findAll();
            List<Funcionario> funcionarios = funcionarioRepository.findAll();
            
            request.setAttribute("animais", animais);
            request.setAttribute("funcionarios", funcionarios);
            request.getRequestDispatcher("/WEB-INF/views/alimentacao/form.jsp").forward(request, response);
            return;
        }
        
        if (pathInfo != null && pathInfo.startsWith("/editar/")) {
            String id = pathInfo.substring("/editar/".length());
            try {
                UUID alimentacaoId = UUID.fromString(id);
                Optional<Alimentacao> optAlimentacao = alimentacaoRepository.findById(alimentacaoId);
                
                if (optAlimentacao.isPresent()) {
                    request.setAttribute("alimentacao", optAlimentacao.get());
                    List<Animal> animais = animalRepository.findAll();
                    List<Funcionario> funcionarios = funcionarioRepository.findAll();
                    
                    request.setAttribute("animais", animais);
                    request.setAttribute("funcionarios", funcionarios);
                    request.getRequestDispatcher("/WEB-INF/views/alimentacao/form.jsp").forward(request, response);
                    return;
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alimentação não encontrada");
                    return;
                }
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
                return;
            }
        }
        
        if (pathInfo != null && !pathInfo.equals("/")) {
            String id = pathInfo.substring(1);
            try {
                UUID alimentacaoId = UUID.fromString(id);
                Optional<Alimentacao> optAlimentacao = alimentacaoRepository.findById(alimentacaoId);
                
                if (optAlimentacao.isPresent()) {
                    request.setAttribute("alimentacao", optAlimentacao.get());
                    request.getRequestDispatcher("/WEB-INF/views/alimentacao/details.jsp").forward(request, response);
                    return;
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alimentação não encontrada");
                    return;
                }
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
                return;
            }
        }
        
        String animalIdStr = request.getParameter("animalId");
        List<Alimentacao> alimentacoes;
        
        if (animalIdStr != null && !animalIdStr.isEmpty()) {
            try {
                UUID animalId = UUID.fromString(animalIdStr);
                alimentacoes = alimentacaoRepository.findByAnimalId(animalId);
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de animal inválido");
                return;
            }
        } else {
            alimentacoes = alimentacaoRepository.findAll();
        }
        
        request.setAttribute("alimentacoes", alimentacoes);
        request.getRequestDispatcher("/WEB-INF/views/alimentacao/list.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (request.getParameter("_method") != null && request.getParameter("_method").equals("DELETE")) {
            processDelete(request, response);
            return;
        }
        
        String idStr = request.getParameter("id");
        String animalIdStr = request.getParameter("animalId");
        String tipoAlimento = request.getParameter("tipoAlimento");
        String quantidadeStr = request.getParameter("quantidade");
        String unidadeMedida = request.getParameter("unidadeMedida");
        String funcionarioIdStr = request.getParameter("funcionarioId");
        String observacoes = request.getParameter("observacoes");
        
        if (animalIdStr == null || tipoAlimento == null || quantidadeStr == null || funcionarioIdStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Campos obrigatórios não fornecidos");
            return;
        }
        
        try {
            UUID animalId = UUID.fromString(animalIdStr);
            Double quantidade = Double.parseDouble(quantidadeStr);
            UUID funcionarioId = UUID.fromString(funcionarioIdStr);
            
            Optional<Animal> optAnimal = animalRepository.findById(animalId);
            Optional<Funcionario> optFuncionario = funcionarioRepository.findById(funcionarioId);
            
            if (optAnimal.isEmpty()) {
                throw new Exception("Animal não encontrado");
            }
            
            if (optFuncionario.isEmpty()) {
                throw new Exception("Funcionário não encontrado");
            }
            
            Animal animal = optAnimal.get();
            Funcionario funcionario = optFuncionario.get();
            
            if (idStr != null && !idStr.isEmpty()) {
                UUID id = UUID.fromString(idStr);
                Optional<Alimentacao> optAlimentacao = alimentacaoRepository.findById(id);
                
                if (optAlimentacao.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alimentação não encontrada");
                    return;
                }
                
                Alimentacao alimentacao = optAlimentacao.get();
                alimentacao.setAnimal(animal);
                alimentacao.setTipoAlimento(tipoAlimento);
                alimentacao.setQuantidade(quantidade);
                alimentacao.setUnidadeMedida(unidadeMedida);
                alimentacao.setFuncionarioResponsavel(funcionario);
                alimentacao.setObservacoes(observacoes);
                
                alimentacaoRepository.update(alimentacao);
                
                request.setAttribute("mensagem", "Alimentação atualizada com sucesso!");
            } else {
                Alimentacao alimentacao = Alimentacao.create(
                    animal,
                    tipoAlimento,
                    quantidade,
                    unidadeMedida,
                    LocalDateTime.now(),
                    funcionario,
                    observacoes
                );
                
                alimentacaoRepository.save(alimentacao);
                
                request.setAttribute("mensagem", "Alimentação registrada com sucesso!");
            }
            
            response.sendRedirect(request.getContextPath() + "/alimentacao");
            
        } catch (NumberFormatException e) {
            request.setAttribute("erro", "Formato de número inválido");
            doGet(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("erro", "ID inválido");
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("erro", "Erro ao processar solicitação: " + e.getMessage());
            doGet(request, response);
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
            UUID alimentacaoId = UUID.fromString(idStr);
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
}
