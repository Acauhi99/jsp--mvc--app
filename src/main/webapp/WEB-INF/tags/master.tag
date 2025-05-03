<%@ tag description="Master Page Template" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} - Zoo Park</title>
    <link rel="icon" href="data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>🦁</text></svg>" type="image/svg+xml">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=System.currentTimeMillis()%>">
    <script src="${pageContext.request.contextPath}/js/script.js" defer></script>
</head>
<body>
    <div class="page-wrapper">
        <c:import url="/WEB-INF/components/header.jsp" />
        <div class="content-container">
            <c:if test="${not empty sessionScope.user}">
                <c:import url="/WEB-INF/components/menu.jsp" />
            </c:if>
            <main class="main-content">
                <div class="container">
                    <jsp:doBody />
                </div>
            </main>
        </div>
        <c:import url="/WEB-INF/components/footer.jsp" />
    </div>
</body>
</html>