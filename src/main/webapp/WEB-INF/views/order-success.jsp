<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Order Confirmation - Valliappan Mart"/>
</jsp:include>

<div class="container py-5" style="max-width: 860px;">
    
    <div class="card p-4 p-md-5 border-0 shadow-sm rounded">
        
        <div class="d-flex align-items-center gap-3 border-bottom pb-4 mb-4">
            <div class="rounded-circle bg-success text-white d-flex align-items-center justify-content-center" style="width: 56px; height: 56px; min-width: 56px;">
                <i class="bi bi-check-lg fs-2"></i>
            </div>
            <div>
                <h3 class="fw-bold mb-1 text-dark">Thank you, your order has been placed!</h3>
                <p class="text-muted mb-0 small">An email confirmation has been sent to <strong>${order.userEmail}</strong>.</p>
            </div>
        </div>

        <div class="row g-3 mb-4 bg-light p-3 rounded small">
            <div class="col-sm-6 col-md-3">
                <span class="text-muted d-block">Order Number:</span>
                <strong class="text-dark">${order.orderNumber}</strong>
            </div>
            <div class="col-sm-6 col-md-3">
                <span class="text-muted d-block">Tracking ID:</span>
                <strong class="text-primary">${order.trackingNumber}</strong>
            </div>
            <div class="col-sm-6 col-md-3">
                <span class="text-muted d-block">Payment Method:</span>
                <strong class="text-dark">${order.paymentMethod} (${order.paymentStatus})</strong>
            </div>
            <div class="col-sm-6 col-md-3">
                <span class="text-muted d-block">Total Amount:</span>
                <strong class="text-danger fs-6">₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##,###"/></strong>
            </div>
        </div>

        <div class="mb-4">
            <h6 class="fw-bold text-dark mb-2">Shipping to:</h6>
            <p class="text-secondary small mb-0">${order.shippingAddress}</p>
        </div>

        <div class="mb-4">
            <h6 class="fw-bold text-dark mb-3">Items in this Delivery</h6>
            <div class="d-flex flex-column gap-2">
                <c:forEach var="item" items="${order.items}">
                    <div class="d-flex align-items-center justify-content-between p-2 border rounded">
                        <div class="d-flex align-items-center gap-3">
                            <img src="${item.productImage}" alt="${item.productName}" class="rounded" style="width: 50px; height: 50px; object-fit: contain;">
                            <div>
                                <div class="fw-semibold small">${item.productName}</div>
                                <div class="text-muted small">Qty: ${item.quantity} &bull; ₹<fmt:formatNumber value="${item.unitPrice}" pattern="#,##,###"/> each</div>
                            </div>
                        </div>
                        <div class="fw-bold text-dark">₹<fmt:formatNumber value="${item.totalPrice}" pattern="#,##,###"/></div>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- Action Buttons -->
        <div class="d-flex flex-wrap gap-2 pt-3 border-top">
            <a href="${pageContext.request.contextPath}/order/track?orderNumber=${order.orderNumber}" class="btn btn-warning fw-semibold px-4">
                <i class="bi bi-geo-alt me-1"></i> Track Package
            </a>
            <a href="${pageContext.request.contextPath}/order/invoice?orderNumber=${order.orderNumber}" target="_blank" class="btn btn-outline-dark fw-semibold px-4">
                <i class="bi bi-file-earmark-pdf me-1"></i> View Tax Invoice
            </a>
            <a href="${pageContext.request.contextPath}/shop" class="btn btn-outline-secondary px-4 ms-sm-auto">
                Continue Shopping
            </a>
        </div>

    </div>

</div>

<jsp:include page="common/footer.jsp"/>
