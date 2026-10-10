<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="Internal Error - Valliappan Mart"/>
</jsp:include>

<div class="container py-5 text-center" style="max-width: 600px;">
    <div class="card p-5 shadow-sm border-0">
        <div class="display-1 fw-bold text-danger mb-3">500</div>
        <h3 class="fw-bold mb-3">Something Went Wrong</h3>
        <p class="text-muted mb-4">
            An unexpected error occurred while processing your request.
            Our technical team has been notified. Please try returning to the home page or refreshing the page.
        </p>
        <div>
            <a href="${pageContext.request.contextPath}/shop" class="btn btn-add-to-cart px-4 py-2 w-auto">
                <i class="bi bi-arrow-left me-2"></i> Return to Shopping
            </a>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
