package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Ingresso;
import models.Ingresso.TipoIngresso;
import models.Customer;
import repositories.CustomerRepository;
import repositories.IngressoRepository;
import dtos.ingresso.IngressoResponse;
import dtos.auth.LoginResponse;
import dtos.auth.UserDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = { "/ingresso", "/ingresso/comprar", "/ingresso/admin", "/ingresso/detalhes",
        "/ingresso/utilizar" })
public class IngressoServlet extends HttpServlet {

    private final IngressoRepository ingressoRepo = new IngressoRepository();
    private final CustomerRepository customerRepo = new CustomerRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        LoginResponse login = (LoginResponse) session.getAttribute("user");
        UserDetails userDetails = login.getUserDetails();
        boolean isAdmin = "ADMINISTRADOR".equals(login.getRole());

        // Rota admin - processada primeiro
        if ("/ingresso/admin".equals(path)) {
            if (!isAdmin) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Buscar todos os ingressos para o admin
            List<Ingresso> ingressos = ingressoRepo.findAll();

            // Aplicar mesmos filtros que já existem na rota normal
            String precoOrder = req.getParameter("precoOrder");
            String dataOrder = req.getParameter("dataOrder");
            String status = req.getParameter("status");
            int page = 1;
            int pageSize = 10;

            try {
                page = Integer.parseInt(req.getParameter("page"));
                if (page < 1)
                    page = 1;
            } catch (Exception ignored) {
            }

            // Filtro por status
            if (status != null && !status.isEmpty()) {
                if ("UTILIZADO".equalsIgnoreCase(status)) {
                    ingressos = ingressos.stream().filter(Ingresso::isUtilizado).collect(Collectors.toList());
                } else if ("DISPONIVEL".equalsIgnoreCase(status)) {
                    ingressos = ingressos.stream().filter(i -> !i.isUtilizado()).collect(Collectors.toList());
                }
            }

            // Ordenações (reaproveitando código)
            if ("asc".equalsIgnoreCase(precoOrder)) {
                ingressos.sort(Comparator.comparing(Ingresso::getValor));
            } else if ("desc".equalsIgnoreCase(precoOrder)) {
                ingressos.sort(Comparator.comparing(Ingresso::getValor).reversed());
            }

            if ("asc".equalsIgnoreCase(dataOrder)) {
                ingressos.sort(Comparator.comparing(Ingresso::getDataCompra));
            } else if ("desc".equalsIgnoreCase(dataOrder)) {
                ingressos.sort(Comparator.comparing(Ingresso::getDataCompra).reversed());
            }

            // Paginação
            int total = ingressos.size();
            int from = (page - 1) * pageSize;
            int to = Math.min(from + pageSize, total);

            // Converter para o formato da view
            List<IngressoResponse> ingressoResponses = new ArrayList<>();
            if (from < total) {
                for (Ingresso ingresso : ingressos.subList(from, to)) {
                    ingressoResponses.add(new IngressoResponse(ingresso));
                }
            }

            req.setAttribute("ingressos", ingressoResponses);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", (int) Math.ceil((double) total / pageSize));
            req.setAttribute("precoOrder", precoOrder);
            req.setAttribute("dataOrder", dataOrder);
            req.setAttribute("status", status);
            req.setAttribute("isAdmin", true);

            // Usar a página de listagem existente
            req.getRequestDispatcher("/WEB-INF/views/ingresso/list.jsp").forward(req, resp);
            return;
        }

        // Rota de detalhes - processada antes de verificar Customer
        if ("/ingresso/detalhes".equals(path)) {
            String ingressoId = req.getParameter("id");
            if (ingressoId == null || ingressoId.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + (isAdmin ? "/ingresso/admin" : "/ingresso"));
                return;
            }

            try {
                java.util.UUID id = java.util.UUID.fromString(ingressoId);
                Ingresso ingresso = ingressoRepo.findById(id).orElse(null);

                if (ingresso == null) {
                    resp.sendRedirect(req.getContextPath() + (isAdmin ? "/ingresso/admin" : "/ingresso"));
                    return;
                }

                // Se não é admin, precisamos verificar se é dono do ingresso
                if (!isAdmin) {
                    Customer user = customerRepo.findById(userDetails.getId()).orElse(null);
                    if (user == null || !user.getId().equals(ingresso.getComprador().getId())) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                }

                IngressoResponse ingressoResponse = new IngressoResponse(ingresso);
                req.setAttribute("ingresso", ingressoResponse);
                req.setAttribute("isAdmin", isAdmin);

                req.getRequestDispatcher("/WEB-INF/views/ingresso/details.jsp").forward(req, resp);
                return;
            } catch (IllegalArgumentException e) {
                resp.sendRedirect(req.getContextPath() + (isAdmin ? "/ingresso/admin" : "/ingresso"));
                return;
            }
        }

        // Para as outras rotas, precisamos de um Customer
        Customer user = customerRepo.findById(userDetails.getId()).orElse(null);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        if ("/ingresso/comprar".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/ingresso/buy.jsp").forward(req, resp);
            return;
        }

        String precoOrder = req.getParameter("precoOrder");
        String dataOrder = req.getParameter("dataOrder");
        String status = req.getParameter("status");
        int page = 1;
        int pageSize = 10;

        try {
            page = Integer.parseInt(req.getParameter("page"));
            if (page < 1)
                page = 1;
        } catch (Exception ignored) {
        }

        List<Ingresso> ingressos = ingressoRepo.findByCompradorId(user.getId());

        // Filtro por status
        if (status != null && !status.isEmpty()) {
            if ("UTILIZADO".equalsIgnoreCase(status)) {
                ingressos = ingressos.stream().filter(Ingresso::isUtilizado).collect(Collectors.toList());
            } else if ("DISPONIVEL".equalsIgnoreCase(status)) {
                ingressos = ingressos.stream().filter(i -> !i.isUtilizado()).collect(Collectors.toList());
            }
        }

        // Ordenação por preço
        if ("asc".equalsIgnoreCase(precoOrder)) {
            ingressos.sort(Comparator.comparing(Ingresso::getValor));
        } else if ("desc".equalsIgnoreCase(precoOrder)) {
            ingressos.sort(Comparator.comparing(Ingresso::getValor).reversed());
        }

        // Ordenação por data
        if ("asc".equalsIgnoreCase(dataOrder)) {
            ingressos.sort(Comparator.comparing(Ingresso::getDataCompra));
        } else if ("desc".equalsIgnoreCase(dataOrder)) {
            ingressos.sort(Comparator.comparing(Ingresso::getDataCompra).reversed());
        }

        // Paginação
        int total = ingressos.size();
        int from = (page - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        List<IngressoResponse> ingressoResponses = new ArrayList<>();
        if (from < total) {
            for (Ingresso ingresso : ingressos.subList(from, to)) {
                ingressoResponses.add(new IngressoResponse(ingresso));
            }
        }

        req.setAttribute("ingressos", ingressoResponses);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", (int) Math.ceil((double) total / pageSize));
        req.setAttribute("precoOrder", precoOrder);
        req.setAttribute("dataOrder", dataOrder);
        req.setAttribute("status", status);

        req.getRequestDispatcher("/WEB-INF/views/ingresso/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        LoginResponse login = (LoginResponse) session.getAttribute("user");
        UserDetails userDetails = login.getUserDetails();
        Customer user = customerRepo.findById(userDetails.getId()).orElse(null);

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        if ("/ingresso/comprar".equals(path)) {
            String tipoStr = req.getParameter("tipo");
            String quantidadeStr = req.getParameter("quantidade");

            if (tipoStr == null || quantidadeStr == null) {
                resp.sendRedirect(req.getContextPath() + "/ingresso/comprar");
                return;
            }

            int quantidade;
            try {
                quantidade = Integer.parseInt(quantidadeStr);
                if (quantidade < 1 || quantidade > 10)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/ingresso/comprar");
                return;
            }

            TipoIngresso tipo;
            try {
                tipo = TipoIngresso.valueOf(tipoStr);
            } catch (IllegalArgumentException e) {
                resp.sendRedirect(req.getContextPath() + "/ingresso/comprar");
                return;
            }

            double valor = calcularValorIngresso(tipo);

            for (int i = 0; i < quantidade; i++) {
                Ingresso ingresso = Ingresso.create(tipo, valor, user);
                ingressoRepo.save(ingresso);
            }

            resp.sendRedirect(req.getContextPath() + "/ingresso");
            return;
        }

        // Adicionar rota para utilizar ingresso
        if ("/ingresso/utilizar".equals(path)) {
            String ingressoId = req.getParameter("id");
            if (ingressoId == null || ingressoId.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/ingresso");
                return;
            }

            try {
                java.util.UUID id = java.util.UUID.fromString(ingressoId);
                Ingresso ingresso = ingressoRepo.findById(id).orElse(null);

                if (ingresso == null) {
                    resp.sendRedirect(req.getContextPath() + "/ingresso");
                    return;
                }

                // Verificar se o usuário tem permissão para utilizar este ingresso
                if (user == null || !user.getId().equals(ingresso.getComprador().getId())) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                // Verificar se o ingresso já foi utilizado
                if (ingresso.isUtilizado()) {
                    resp.sendRedirect(req.getContextPath() + "/ingresso/detalhes?id=" + ingressoId + "&erro=utilizado");
                    return;
                }

                // Marcar como utilizado
                ingresso.setUtilizado(true);
                ingressoRepo.update(ingresso);

                resp.sendRedirect(req.getContextPath() + "/ingresso/detalhes?id=" + ingressoId + "&sucesso=true");
                return;
            } catch (IllegalArgumentException e) {
                resp.sendRedirect(req.getContextPath() + "/ingresso");
                return;
            }
        }

        // Adicionar rota para admin alternar status do ingresso
        if ("/ingresso/admin/toggle-status".equals(path)) {
            String ingressoId = req.getParameter("id");
            if (ingressoId == null || ingressoId.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/ingresso/admin");
                return;
            }

            // Verificar se é admin
            if (!"ADMINISTRADOR".equals(login.getRole())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            try {
                java.util.UUID id = java.util.UUID.fromString(ingressoId);
                Ingresso ingresso = ingressoRepo.findById(id).orElse(null);

                if (ingresso == null) {
                    resp.sendRedirect(req.getContextPath() + "/ingresso/admin");
                    return;
                }

                // Inverter o status
                ingresso.setUtilizado(!ingresso.isUtilizado());
                ingressoRepo.update(ingresso);

                resp.sendRedirect(req.getContextPath() + "/ingresso/detalhes?id=" + ingressoId + "&sucesso=true");
                return;
            } catch (IllegalArgumentException e) {
                resp.sendRedirect(req.getContextPath() + "/ingresso/admin");
                return;
            }
        }
    }

    private double calcularValorIngresso(TipoIngresso tipo) {
        switch (tipo) {
            case ADULTO:
                return 50.0;
            case CRIANCA:
                return 25.0;
            case IDOSO:
                return 20.0;
            case ESTUDANTE:
                return 30.0;
            case DEFICIENTE:
                return 0.0;
            default:
                return 50.0;
        }
    }
}