<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="404 Page Not Found - Valliappan Mart"/>
</jsp:include>

<div class="container py-5 text-center" style="max-width: 600px;">
    <div class="card p-5 shadow-sm border-0">
        <div class="display-1 fw-bold text-warning mb-3">404</div>
        <h3 class="fw-bold mb-3">Looking for something?</h3>
        <p class="text-muted mb-4">
            We're sorry. The Web address you entered is not a functioning page on our site.
            Please go to Valliappan Mart's home page to browse our featured products and deals.
        </p>
        <div>
            <a href="${pageContext.request.contextPath}/shop" class="btn btn-add-to-cart px-4 py-2 w-auto">
                <i class="bi bi-house-door me-2"></i> Go to Valliappan Mart Home
            </a>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
