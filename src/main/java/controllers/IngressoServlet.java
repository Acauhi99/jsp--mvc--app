package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Ingresso;
import models.Ingresso.TipoIngresso;
import models.Customer;
import repositories.CustomerRepository;
import repositories.IngressoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = { "/ingresso", "/ingresso/comprar", "/ingresso/admin", "/ingresso/detalhes",
        "/ingresso/utilizar" })
public class IngressoServlet extends BaseServlet {

    private final IngressoRepository ingressoRepo;
    private final CustomerRepository customerRepo;

    public IngressoServlet() {
        this.ingressoRepo = new IngressoRepository();
        this.customerRepo = new CustomerRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if (!requireAuthentication(req, resp)) {
            return;
        }

        if ("/ingresso/admin".equals(path)) {
            if (!requireRole(req, resp, ROLE_ADMINISTRADOR)) {
                return;
            }
            showAdminIngressoList(req, resp);
            return;
        }

        UUID userId = (UUID) req.getSession().getAttribute("userId");
        Customer user = customerRepo.findById(userId).orElse(null);

        if (user == null && ROLE_VISITANTE.equals(getUserRole(req))) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        switch (path) {
            case "/ingresso/detalhes":
                showIngressoDetails(req, resp, ROLE_ADMINISTRADOR.equals(getUserRole(req)), user);
                break;
            case "/ingresso/comprar":
                forwardToView(req, resp, "/WEB-INF/views/ingresso/buy.jsp");
                break;
            default:
                showUserIngressoList(req, resp, user);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!requireAuthentication(req, resp)) {
            return;
        }

        String path = req.getServletPath();
        UUID userId = (UUID) req.getSession().getAttribute("userId");
        Customer user = customerRepo.findById(userId).orElse(null);

        if (user == null && ROLE_VISITANTE.equals(getUserRole(req))) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        switch (path) {
            case "/ingresso/comprar":
                processIngressoPurchase(req, resp, user);
                break;
            case "/ingresso/utilizar":
                processIngressoUsage(req, resp, user);
                break;
            case "/ingresso/admin/toggle-status":
                if (!requireRole(req, resp, ROLE_ADMINISTRADOR)) {
                    return;
                }
                toggleIngressoStatus(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/ingresso");
                break;
        }
    }

    private void showAdminIngressoList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Ingresso> ingressos = ingressoRepo.findAll();

        String precoOrder = req.getParameter("precoOrder");
        String dataOrder = req.getParameter("dataOrder");
        String status = req.getParameter("status");
        int page = getPageParam(req);
        int pageSize = 10;

        List<Ingresso> filteredIngressos = filterAndSortIngressos(ingressos, status, precoOrder, dataOrder);
        int totalPages = (int) Math.ceil((double) filteredIngressos.size() / pageSize);

        List<Ingresso> pagedIngressos = paginateIngressos(filteredIngressos, page, pageSize);

        req.setAttribute("ingressos", pagedIngressos);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("precoOrder", precoOrder);
        req.setAttribute("dataOrder", dataOrder);
        req.setAttribute("status", status);
        req.setAttribute("isAdmin", true);

        forwardToView(req, resp, "/WEB-INF/views/ingresso/list.jsp");
    }

    private void showUserIngressoList(HttpServletRequest req, HttpServletResponse resp, Customer user)
            throws ServletException, IOException {
        List<Ingresso> ingressos = ingressoRepo.findByCompradorId(user.getId());

        String precoOrder = req.getParameter("precoOrder");
        String dataOrder = req.getParameter("dataOrder");
        String status = req.getParameter("status");
        int page = getPageParam(req);
        int pageSize = 10;

        List<Ingresso> filteredIngressos = filterAndSortIngressos(ingressos, status, precoOrder, dataOrder);
        int totalPages = (int) Math.ceil((double) filteredIngressos.size() / pageSize);

        List<Ingresso> pagedIngressos = paginateIngressos(filteredIngressos, page, pageSize);

        req.setAttribute("ingressos", pagedIngressos);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("precoOrder", precoOrder);
        req.setAttribute("dataOrder", dataOrder);
        req.setAttribute("status", status);

        forwardToView(req, resp, "/WEB-INF/views/ingresso/list.jsp");
    }

    private List<Ingresso> filterAndSortIngressos(List<Ingresso> ingressos, String status,
            String precoOrder, String dataOrder) {
        List<Ingresso> result = new ArrayList<>(ingressos);

        if (status != null && !status.isEmpty()) {
            if ("UTILIZADO".equalsIgnoreCase(status)) {
                result = result.stream().filter(Ingresso::isUtilizado).collect(Collectors.toList());
            } else if ("DISPONIVEL".equalsIgnoreCase(status)) {
                result = result.stream().filter(i -> !i.isUtilizado()).collect(Collectors.toList());
            }
        }

        if ("asc".equalsIgnoreCase(precoOrder)) {
            result.sort(Comparator.comparing(Ingresso::getValor));
        } else if ("desc".equalsIgnoreCase(precoOrder)) {
            result.sort(Comparator.comparing(Ingresso::getValor).reversed());
        }

        if ("asc".equalsIgnoreCase(dataOrder)) {
            result.sort(Comparator.comparing(Ingresso::getDataCompra));
        } else if ("desc".equalsIgnoreCase(dataOrder)) {
            result.sort(Comparator.comparing(Ingresso::getDataCompra).reversed());
        }

        return result;
    }

    private List<Ingresso> paginateIngressos(List<Ingresso> ingressos, int page, int pageSize) {
        int total = ingressos.size();
        int from = (page - 1) * pageSize;
        int to = Math.min(from + pageSize, total);

        if (from < total) {
            return ingressos.subList(from, to);
        } else {
            return new ArrayList<>();
        }
    }

    private void showIngressoDetails(HttpServletRequest req, HttpServletResponse resp, boolean isAdmin, Customer user)
            throws ServletException, IOException {
        String ingressoId = req.getParameter("id");
        if (ingressoId == null || ingressoId.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + (isAdmin ? "/ingresso/admin" : "/ingresso"));
            return;
        }

        try {
            UUID id = UUID.fromString(ingressoId);
            Ingresso ingresso = ingressoRepo.findById(id).orElse(null);

            if (ingresso == null) {
                resp.sendRedirect(req.getContextPath() + (isAdmin ? "/ingresso/admin" : "/ingresso"));
                return;
            }

            if (!isAdmin && (user == null || !user.getId().equals(ingresso.getComprador().getId()))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            req.setAttribute("ingresso", ingresso);
            req.setAttribute("isAdmin", isAdmin);

            forwardToView(req, resp, "/WEB-INF/views/ingresso/details.jsp");
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + (isAdmin ? "/ingresso/admin" : "/ingresso"));
        }
    }

    private void processIngressoPurchase(HttpServletRequest req, HttpServletResponse resp, Customer user)
            throws ServletException, IOException {
        String tipoStr = req.getParameter("tipo");
        String quantidadeStr = req.getParameter("quantidade");

        if (tipoStr == null || quantidadeStr == null) {
            resp.sendRedirect(req.getContextPath() + "/ingresso/comprar");
            return;
        }

        try {
            int quantidade = Integer.parseInt(quantidadeStr);
            if (quantidade < 1 || quantidade > 10) {
                resp.sendRedirect(req.getContextPath() + "/ingresso/comprar");
                return;
            }

            TipoIngresso tipo = TipoIngresso.valueOf(tipoStr);
            double valor = calcularValorIngresso(tipo);

            for (int i = 0; i < quantidade; i++) {
                Ingresso ingresso = Ingresso.create(tipo, valor, user);
                ingressoRepo.save(ingresso);
            }

            resp.sendRedirect(req.getContextPath() + "/ingresso");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/ingresso/comprar");
        }
    }

    private void processIngressoUsage(HttpServletRequest req, HttpServletResponse resp, Customer user)
            throws ServletException, IOException {
        String ingressoId = req.getParameter("id");
        if (ingressoId == null || ingressoId.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/ingresso");
            return;
        }

        try {
            UUID id = UUID.fromString(ingressoId);
            Ingresso ingresso = ingressoRepo.findById(id).orElse(null);

            if (ingresso == null) {
                resp.sendRedirect(req.getContextPath() + "/ingresso");
                return;
            }

            if (user == null || !user.getId().equals(ingresso.getComprador().getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            if (ingresso.isUtilizado()) {
                resp.sendRedirect(req.getContextPath() + "/ingresso/detalhes?id=" + ingressoId + "&erro=utilizado");
                return;
            }

            ingresso.setUtilizado(true);
            ingressoRepo.update(ingresso);

            resp.sendRedirect(req.getContextPath() + "/ingresso/detalhes?id=" + ingressoId + "&sucesso=true");
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/ingresso");
        }
    }

    private void toggleIngressoStatus(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ingressoId = req.getParameter("id");
        if (ingressoId == null || ingressoId.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/ingresso/admin");
            return;
        }

        try {
            UUID id = UUID.fromString(ingressoId);
            Ingresso ingresso = ingressoRepo.findById(id).orElse(null);

            if (ingresso == null) {
                resp.sendRedirect(req.getContextPath() + "/ingresso/admin");
                return;
            }

            ingresso.setUtilizado(!ingresso.isUtilizado());
            ingressoRepo.update(ingresso);

            resp.sendRedirect(req.getContextPath() + "/ingresso/detalhes?id=" + ingressoId + "&sucesso=true");
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/ingresso/admin");
        }
    }

    private int getPageParam(HttpServletRequest req) {
        int page = 1;
        try {
            String pageParam = req.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        return page;
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