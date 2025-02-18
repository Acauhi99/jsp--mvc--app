<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.User" %>
<%@ page import="dao.UserDAO" %>

<%
    UserDAO userDAO = new UserDAO();
    List<User> userList = userDAO.getAllUsers();
%>

<!DOCTYPE html>
<html>
<head>
    <title>User List</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <h1>User List</h1>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                for (User user : userList) {
            %>
            <tr>
                <td><%= user.getId() %></td>
                <td><%= user.getName() %></td>
                <td><%= user.getEmail() %></td>
                <td>
                    <a href="form.jsp?id=<%= user.getId() %>">Edit</a>
                    <a href="UserServlet?action=delete&id=<%= user.getId() %>">Delete</a>
                </td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
    <a href="form.jsp">Add New User</a>
</body>
</html>