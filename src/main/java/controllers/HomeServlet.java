package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/")
public class HomeServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(HomeServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.info("HomeServlet doGet method called");
        try {
            request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
        } catch (Exception e) {
            logger.severe("Error forwarding to home.jsp: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}