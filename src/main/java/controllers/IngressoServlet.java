package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Ingresso;
import models.Customer;
import repositories.CustomerRepository;
import repositories.IngressoRepository;
import handlers.IngressoHandler;
import handlers.IngressoHandler.PageResult;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = { "/ingresso", "/ingresso/comprar", "/ingresso/admin", "/ingresso/detalhes",
        "/ingresso/utilizar" })
public class IngressoServlet extends BaseServlet {

    private final IngressoHandler ingressoHandler;
    private final CustomerRepository customerRepo;

    public IngressoServlet() {
        IngressoRepository ingressoRepo = new IngressoRepository();
        CustomerRepository customerRepo = new CustomerRepository();
        this.ingressoHandler = new IngressoHandler(ingressoRepo, customerRepo);
        this.customerRepo = customerRepo;
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
        List<Ingresso> ingressos = ingressoHandler.listarTodosIngressos();

        String precoOrder = req.getParameter("precoOrder");
        String dataOrder = req.getParameter("dataOrder");
        String status = req.getParameter("status");
        int page = getPageParam(req);
        int pageSize = 10;

        PageResult<Ingresso> pageResult = ingressoHandler.processarIngressosPaginados(
                ingressos, status, precoOrder, dataOrder, page, pageSize);

        req.setAttribute("ingressos", pageResult.getItems());
        req.setAttribute("page", pageResult.getCurrentPage());
        req.setAttribute("totalPages", pageResult.getTotalPages());
        req.setAttribute("precoOrder", precoOrder);
        req.setAttribute("dataOrder", dataOrder);
        req.setAttribute("status", status);
        req.setAttribute("isAdmin", true);

        forwardToView(req, resp, "/WEB-INF/views/ingresso/list.jsp");
    }

    private void showUserIngressoList(HttpServletRequest req, HttpServletResponse resp, Customer user)
            throws ServletException, IOException {
        List<Ingresso> ingressos = ingressoHandler.listarIngressosPorComprador(user.getId());

        String precoOrder = req.getParameter("precoOrder");
        String dataOrder = req.getParameter("dataOrder");
        String status = req.getParameter("status");
        int page = getPageParam(req);
        int pageSize = 10;

        PageResult<Ingresso> pageResult = ingressoHandler.processarIngressosPaginados(
                ingressos, status, precoOrder, dataOrder, page, pageSize);

        req.setAttribute("ingressos", pageResult.getItems());
        req.setAttribute("page", pageResult.getCurrentPage());
        req.setAttribute("totalPages", pageResult.getTotalPages());
        req.setAttribute("precoOrder", precoOrder);
        req.setAttribute("dataOrder", dataOrder);
        req.setAttribute("status", status);

        forwardToView(req, resp, "/WEB-INF/views/ingresso/list.jsp");
    }

    private void showIngressoDetails(HttpServletRequest req, HttpServletResponse resp, boolean isAdmin, Customer user)
            throws ServletException, IOException {
        String ingressoId = req.getParameter("id");
        if (ingressoId == null || ingressoId.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + (isAdmin ? "/ingresso/admin" : "/ingresso"));
            return;
        }

        ingressoHandler.buscarIngressoPorId(ingressoId).ifPresentOrElse(
                ingresso -> {
                    try {
                        if (isAdmin || (user != null
                                && ingressoHandler.temPermissaoParaVerIngresso(ingresso, user.getId(), false))) {
                            req.setAttribute("ingresso", ingresso);
                            req.setAttribute("isAdmin", isAdmin);
                            forwardToView(req, resp, "/WEB-INF/views/ingresso/details.jsp");
                        } else {
                            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        resp.sendRedirect(req.getContextPath() + (isAdmin ? "/ingresso/admin" : "/ingresso"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
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
            ingressoHandler.comprarIngressos(tipoStr, quantidade, user.getId());
            resp.sendRedirect(req.getContextPath() + "/ingresso");
        } catch (Exception e) {
            req.setAttribute("erro", e.getMessage());
            forwardToView(req, resp, "/WEB-INF/views/ingresso/buy.jsp");
        }
    }

    private void processIngressoUsage(HttpServletRequest req, HttpServletResponse resp, Customer user)
            throws ServletException, IOException {
        String ingressoId = req.getParameter("id");

        try {
            ingressoHandler.utilizarIngresso(ingressoId, user.getId());
            resp.sendRedirect(req.getContextPath() + "/ingresso/detalhes?id=" + ingressoId + "&sucesso=true");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/ingresso/detalhes?id=" + ingressoId + "&erro=" +
                    java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private void toggleIngressoStatus(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ingressoId = req.getParameter("id");

        try {
            ingressoHandler.alternarStatusIngresso(ingressoId);
            resp.sendRedirect(req.getContextPath() + "/ingresso/detalhes?id=" + ingressoId + "&sucesso=true");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/ingresso/detalhes?id=" + ingressoId + "&erro=" +
                    java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
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
}