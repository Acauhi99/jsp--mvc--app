<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Diagnostic Page</title>
</head>
<body>
    <h1>Application Diagnostic</h1>
    
    <h2>Environment Variables:</h2>
    <ul>
    <% 
        Map<String, String> env = System.getenv();
        for (String key : new TreeSet<>(env.keySet())) {
            String value = env.get(key);
            if (key.contains("PASSWORD") || key.contains("SECRET")) {
                value = "*****";
            }
            out.println("<li>" + key + " = " + value + "</li>");
        }
    %>
    </ul>
    
    <h2>System Properties:</h2>
    <ul>
    <% 
        Properties props = System.getProperties();
        for (String key : new TreeSet<>(props.stringPropertyNames())) {
            String value = props.getProperty(key);
            out.println("<li>" + key + " = " + value + "</li>");
        }
    %>
    </ul>
    
    <h2>Application Paths:</h2>
    <p>Context Path: <%= request.getContextPath() %></p>
    <p>Servlet Path: <%= request.getServletPath() %></p>
    <p>Real Path to /WEB-INF/views/home.jsp: <%= application.getRealPath("/WEB-INF/views/home.jsp") %></p>
</body>
</html>