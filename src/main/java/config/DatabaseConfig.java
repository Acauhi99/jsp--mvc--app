package config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class DatabaseConfig implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    private static EntityManagerFactory emf;
    private static final String DB_DIRECTORY = "src/main/data";
    private static final String DB_FILE_PATH = DB_DIRECTORY + "/zoologico.db";
    private static final String PERSISTENCE_UNIT_NAME = "zoologicoPU";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ensureDatabaseDirectoryExists();

            Map<String, String> properties = buildDatabaseProperties();

            logger.log(Level.INFO, "Initializing EntityManagerFactory for persistence unit: {0}", PERSISTENCE_UNIT_NAME);
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
            sce.getServletContext().setAttribute("emf", emf);
            logger.log(Level.INFO, "EntityManagerFactory initialized successfully.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing EntityManagerFactory: " + e.getMessage(), e);
           
        }
    }

    private void ensureDatabaseDirectoryExists() {
        File dbDir = new File(DB_DIRECTORY);
        if (!dbDir.exists()) {
            logger.log(Level.INFO, "Creating database directory: {0}", dbDir.getAbsolutePath());
            if (!dbDir.mkdirs()) {
                 logger.log(Level.WARNING, "Failed to create database directory: {0}", dbDir.getAbsolutePath());
                 
            }
        } else {
             logger.log(Level.FINE, "Database directory already exists: {0}", dbDir.getAbsolutePath());
        }
    }

    private Map<String, String> buildDatabaseProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", "jdbc:sqlite:" + DB_FILE_PATH);
        properties.put("jakarta.persistence.jdbc.driver", "org.sqlite.JDBC");
        properties.put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        
        return properties;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (emf != null && emf.isOpen()) {
            logger.log(Level.INFO, "Closing EntityManagerFactory.");
            emf.close();
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
             logger.log(Level.SEVERE, "EntityManagerFactory is not initialized.");
            
        }
        return emf;
    }
}