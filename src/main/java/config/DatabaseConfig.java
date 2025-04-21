package config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@WebListener
public class DatabaseConfig implements ServletContextListener {
    
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    private static EntityManagerFactory emf;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Obter as configurações do banco de dados
            String dbUrl = System.getenv("DB_URL");
            String dbUsername = System.getenv("DB_USERNAME");
            String dbPassword = System.getenv("DB_PASSWORD");
            
            logger.info("Database URL: " + dbUrl);
            logger.info("Database Username: " + dbUsername);
            
            if (dbUrl == null || dbUsername == null || dbPassword == null) {
                logger.severe("Database environment variables not set properly!");
                // Set defaults for development/testing
                dbUrl = "jdbc:postgresql://db:5432/jsp_mvc_app";
                dbUsername = "db";
                dbPassword = "123456";
                logger.info("Using default database settings");
            }
            
            // Registrar como propriedades do sistema
            System.setProperty("DB_URL", dbUrl);
            System.setProperty("DB_USERNAME", dbUsername);
            System.setProperty("DB_PASSWORD", dbPassword);
            
            // Configurar o EntityManagerFactory manualmente
            Map<String, String> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.url", dbUrl);
            properties.put("jakarta.persistence.jdbc.user", dbUsername);
            properties.put("jakarta.persistence.jdbc.password", dbPassword);
            properties.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
            
            // Criar o EntityManagerFactory
            logger.info("Initializing EntityManagerFactory...");
            emf = Persistence.createEntityManagerFactory("zoologicoPU", properties);
            sce.getServletContext().setAttribute("emf", emf);
            logger.info("EntityManagerFactory initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}