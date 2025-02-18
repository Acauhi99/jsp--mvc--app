<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Form</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h2>${user != null ? 'Edit User' : 'Create User'}</h2>
    <form action="${pageContext.request.contextPath}/user" method="post">
        <input type="hidden" name="id" value="${user != null ? user.id : ''}">
        <label for="name">Name:</label>
        <input type="text" id="name" name="name" value="${user != null ? user.name : ''}" required>
        <br>
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" value="${user != null ? user.email : ''}" required>
        <br>
        <input type="submit" value="${user != null ? 'Update' : 'Create'}">
    </form>
    <br>
    <a href="${pageContext.request.contextPath}/views/list.jsp">Back to User List</a>
</body>
</html>