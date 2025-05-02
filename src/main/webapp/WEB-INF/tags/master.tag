<%@ tag description="Master Page Template" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} - Zoo Park</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="${pageContext.request.contextPath}/js/script.js" defer></script>
</head>
<body>
    <div class="page-wrapper">
        <c:import url="/WEB-INF/components/header.jsp" />
        
        <c:if test="${not empty sessionScope.user}">
            <c:import url="/WEB-INF/components/menu.jsp" />
        </c:if>
        
        <main class="main-content">
            <div class="container">
                <jsp:doBody />
            </div>
        </main>
        
        <c:import url="/WEB-INF/components/footer.jsp" />
    </div>
</body>
</html>