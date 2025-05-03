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

@WebServlet(urlPatterns = { "/ingresso", "/ingresso/comprar" })
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