<%@ tag description="Master Page Template" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} - Zoo Park</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="page-wrapper">
        <c:import url="/WEB-INF/components/header.jsp" />
        
        <main class="main-content">
            <jsp:doBody />
        </main>
        
        <c:import url="/WEB-INF/components/footer.jsp" />
    </div>
</body>
</html>