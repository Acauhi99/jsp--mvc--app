package config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.postgresql.ds.PGSimpleDataSource;

import java.util.HashMap;
import java.util.Map;

@WebListener
public class DatabaseConfig implements ServletContextListener {
    
    private static EntityManagerFactory emf;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Obter as configurações do banco de dados
        String dbUrl = System.getenv("DB_URL");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");
        
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
        emf = Persistence.createEntityManagerFactory("zoologicoPU", properties);
        sce.getServletContext().setAttribute("emf", emf);
        
        // Registrar DataSource em JNDI
        try {
            // Criar DataSource
            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setURL(dbUrl);
            dataSource.setUser(dbUsername);
            dataSource.setPassword(dbPassword);
            
            // Registrar em JNDI
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            
            try {
                // Verificar se já existe antes de ligar
                envContext.lookup("jdbc/PostgresDB");
            } catch (NamingException e) {
                // Não existe, então vamos criar
                envContext.bind("jdbc/PostgresDB", dataSource);
            }
        } catch (NamingException e) {
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